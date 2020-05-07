package com.ag04.batchui.dbqueue.service;
import org.springframework.batch.core.JobParameters;

public interface JobManagementService {

    /**
     * Starts a new Job Execution.
     *
     * @param jobBeanName name of the Job bean to be started.
     * @param jobParameters
     * @return id of the new JobExecution
     * @throws Exception
     */
    Long startNewJob(String jobBeanName, JobParameters jobParameters) throws Exception;

    /**
     * Starts a new Job Execution.
     *
     * @param jobBeanName name of the Job bean to be started.
     * @param jobParameters
     * @return id of the new JobExecution
     * @throws Exception
     */
    Long startNewJobAsync(String jobBeanName, JobParameters jobParameters) throws Exception;
}
