package com.ag04.batchui.dbqueue.config;

import com.ag04.batchui.dbqueue.QueueConsumer;
import com.ag04.batchui.dbqueue.QueueConsumerModule;
import com.ag04.batchui.dbqueue.repository.StartJobCommandRepository;
import com.ag04.batchui.dbqueue.retry.FixedDelayRetryPolicy;
import com.ag04.batchui.dbqueue.retry.LimitedRetryPolicy;
import com.ag04.batchui.dbqueue.retry.RetryPolicy;
import com.ag04.batchui.dbqueue.service.JobManagementService;
import com.ag04.batchui.dbqueue.service.StartJobCommandConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import java.time.Duration;

@Profile("remoteContext")
@Configuration
public class DbQueueConsumerConfig {

    @Bean
    public StartJobCommandConsumer startJobCommandConsumer(
            EntityManager entityManager,
            StartJobCommandRepository repository,
            JobManagementService jobManagementService,
            ObjectMapper mapper
    ) {
        return new StartJobCommandConsumer(entityManager, repository, jobManagementService, mapper);
    }

    @Bean
    public QueueConsumer startJobCommandConsumerQueueConsumer(
            StartJobCommandConsumer startJobCommandConsumer,
            PlatformTransactionManager transactionManager
    ) {
        RetryPolicy retryPolicy = new LimitedRetryPolicy(5, new FixedDelayRetryPolicy(Duration.ofMinutes(5)));
        QueueConsumer queueConsumer = new QueueConsumer(
                (QueueConsumerModule) startJobCommandConsumer,
                retryPolicy,
                transactionManager,
                100,
                30
        );
        return queueConsumer;
    }
}
