package com.ag04.batchui.dbqueue.service.impl;

import com.ag04.batchui.dbqueue.service.JobManagementServiceAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Local implementation of JobManagementService.
 *
 * This implementation has access to local ApplicationContexts and attempts to fetch target Job Beans from there.
 *
 */
public class JobManagementServiceAsyncImpl implements JobManagementServiceAsync {
    private final Logger log = LoggerFactory.getLogger(JobManagementServiceAsyncImpl.class);

    @Autowired
    private ApplicationContext ctx;

    private final JobLauncher jobLauncher;

    public JobManagementServiceAsyncImpl(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    @Async
    @Override
    public Long startNewJob(String jobBeanName, JobParameters jobParameters ) throws Exception {
        Job job = (Job) ctx.getBean(jobBeanName);

        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        return jobExecution.getId();
    }
}
