package com.ag04.batchui.dbqueue.config;

import com.ag04.batchui.dbqueue.consumer.QueueConsumer;
import com.ag04.batchui.dbqueue.consumer.QueueConsumerModule;
import com.ag04.batchui.dbqueue.repository.StartJobCommandRepository;
import com.ag04.batchui.dbqueue.retry.FixedDelayRetryPolicy;
import com.ag04.batchui.dbqueue.retry.LimitedRetryPolicy;
import com.ag04.batchui.dbqueue.retry.RetryPolicy;
import com.ag04.batchui.dbqueue.service.JobManagementService;
import com.ag04.batchui.dbqueue.service.StartJobCommandConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import java.time.Duration;

@ConditionalOnProperty(
        name = "batchui.dbqueue.consumer.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Configuration
public class DbQueueConsumerConfig {
    private final Logger logger = LoggerFactory.getLogger(DbQueueConsumerConfig.class);

    @Bean
    public StartJobCommandConsumer startJobCommandConsumer(
            EntityManager entityManager,
            StartJobCommandRepository repository,
            JobManagementService jobManagementService,
            ObjectMapper mapper
    ) {
        logger.info("--> Configuring StartJobCommandConsumer");
        return new StartJobCommandConsumer(entityManager, repository, jobManagementService, mapper);
    }

    @Bean
    public QueueConsumer startJobCommandConsumerQueueConsumer(
            StartJobCommandConsumer startJobCommandConsumer,
            PlatformTransactionManager transactionManager,
            @Value("${batchui.dbqueue.consumer.polling-interval:30}") int polledItemsLimit,
            @Value("${batchui.dbqueue.consumer.polling-items-limit:100}") int pollingPeriod
    ) {

        logger.info("--> Configuring QueueConsumer");
        RetryPolicy retryPolicy = new LimitedRetryPolicy(5, new FixedDelayRetryPolicy(Duration.ofMinutes(5)));
        QueueConsumer queueConsumer = new QueueConsumer(
                (QueueConsumerModule) startJobCommandConsumer,
                retryPolicy,
                transactionManager,
                polledItemsLimit,
                pollingPeriod
        );
        return queueConsumer;
    }
}
