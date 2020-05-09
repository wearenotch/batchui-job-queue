package com.ag04.batchui.dbqueue;

import com.ag04.batchui.dbqueue.service.JobManagementServiceAsync;
import com.ag04.batchui.dbqueue.service.impl.JobManagementServiceAsyncImpl;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "com.ag04.batchui.dbqueue")
@EnableJpaRepositories("com.ag04.batchui.dbqueue.repository")
public class DbQueueModuleConfiguration {

    @Bean
    public JobManagementServiceAsync jobManagementServiceAsync(JobLauncher jobLauncher) {
        return new JobManagementServiceAsyncImpl(jobLauncher);
    }
}
