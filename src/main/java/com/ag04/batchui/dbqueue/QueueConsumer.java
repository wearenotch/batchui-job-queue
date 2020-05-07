package com.ag04.batchui.dbqueue;

import com.ag04.batchui.dbqueue.retry.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class QueueConsumer {
    private final Logger logger = LoggerFactory.getLogger(QueueConsumer.class);

    private final TransactionTemplate transactionTemplate;

    private final QueueConsumerModule queueConsumerModule;
    private final RetryPolicy retryPolicy;
    private final long pollingPeriodInSecs;
    private final int itemsPollSize;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> processingTask;

    public QueueConsumer(
            QueueConsumerModule<?> queueConsumerModule,
            RetryPolicy retryPolicy,
            PlatformTransactionManager transactionManager,
            int polledItemsLimit,
            long pollingPeriodInSecs
    ) {
        if (polledItemsLimit < 1) {
            throw new IllegalArgumentException("Polled items size cannot be less than 1, bus is " + polledItemsLimit);
        }
        if (pollingPeriodInSecs < 1) {
            throw new IllegalArgumentException("Polling period cannot be less than 1, bus is " + polledItemsLimit);
        }
        this.queueConsumerModule = Objects.requireNonNull(queueConsumerModule);
        this.retryPolicy = Objects.requireNonNull(retryPolicy);
        this.pollingPeriodInSecs = pollingPeriodInSecs;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.itemsPollSize = polledItemsLimit;
    }

    @PostConstruct
    public void init() {
        startProcessingTask();
    }

    private void startProcessingTask() {
        logger.info("Starting queue processing task with delay of {} secs", this.pollingPeriodInSecs);
        Runnable command = this::processQueuedItems;
        this.processingTask = this.scheduledExecutorService.scheduleWithFixedDelay(command, pollingPeriodInSecs, pollingPeriodInSecs, TimeUnit.SECONDS);
    }

    private void stopProcessingTask() {
        if (this.processingTask != null && !this.processingTask.isCancelled()) {
            logger.info("Stopping queue processing task");
            this.processingTask.cancel(true);
        }
    }

    @PreDestroy
    public void destroy() throws Exception {
        stopProcessingTask();
        this.scheduledExecutorService.shutdownNow();
    }

    public void processQueuedItems() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<?> itemIds = this.queueConsumerModule.findItemIdsWhereQueueingNextAttemptTimeIsBefore(now, itemsPollSize);

            if (!itemIds.isEmpty()) {
                logger.info("Fetched {} pending queued items", itemIds.size());
                for (Object itemId : itemIds) {
                    processItemAndHandleErrorIfRaised(itemId);
                }
            }
        } catch (Throwable th) {
            logger.error("Error while fetching queued items: " + th.getMessage(), th);
        }
    }

    private void processItemAndHandleErrorIfRaised(Object itemId) {
        try {
            executeUnderTransaction(() -> processItem(itemId));
        } catch (Throwable error) {
            executeUnderTransaction(() -> registerProcessingFailure(itemId, error));
        }
    }

    private void executeUnderTransaction(Runnable runnable) {
        this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                runnable.run();
            }
        });
    }

    public void processItem(Object itemId) {
        Optional<QueueingState> queueingStateOptional = this.queueConsumerModule.processItem(itemId);
        if (queueingStateOptional.isPresent()) {
            queueingStateOptional.get().registerAttemptSuccess(LocalDateTime.now());
        } else {
            logger.warn("No queued item found under ID {} to process it", itemId);
        }
    }

    private void registerProcessingFailure(Object itemId, Throwable error) {
        logger.error("Error while processing item by ID " + itemId + ": " + error.getMessage(), error);

        Optional<QueueingState> queueingStateOptional = this.queueConsumerModule.getQueueingStateForItem(itemId);
        if (queueingStateOptional.isPresent()) {
            QueueingState queueingState = queueingStateOptional.get();
            queueingState.registerAttemptFailure(LocalDateTime.now(), error);

            Optional<LocalDateTime> retryAttemptTimeOptional = retryPolicy.calculateNextAttemptTime(queueingState.getLastAttemptTime(), queueingState.getAttemptCount());
            if (retryAttemptTimeOptional.isPresent()) {
                LocalDateTime nextAttemptTime = retryAttemptTimeOptional.get();
                logger.info("Retry for item by ID {} scheduled for time: {}", itemId, nextAttemptTime);
                queueingState.scheduleNextAttempt(nextAttemptTime);
            } else {
                logger.warn("No retry scheduled for item by ID {}", itemId);
            }
        } else {
            logger.warn("No queued item found under ID {} to register failed attempt", itemId);
        }
    }
}
