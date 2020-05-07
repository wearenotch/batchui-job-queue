package com.ag04.batchui.dbqueue.service;

import com.ag04.batchui.dbqueue.domain.JobExecutionParamDto;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.util.Date;
import java.util.List;

public class JobParamsUtil {

    public static JobParameters convert(List<JobExecutionParamDto> paramsList) {
        JobParametersBuilder jpb = new JobParametersBuilder();
        for (JobExecutionParamDto param : paramsList) {
            if (JobParameter.ParameterType.STRING.toString().equalsIgnoreCase(param.getTypeCd())) {
                jpb.addString(param.getKeyName(), param.getStringVal());
            } else if (JobParameter.ParameterType.LONG.toString().equalsIgnoreCase(param.getTypeCd())) {
                jpb.addLong(param.getKeyName(), param.getLongVal());
            } else if (JobParameter.ParameterType.DOUBLE.toString().equalsIgnoreCase(param.getTypeCd())) {
                jpb.addDouble(param.getKeyName(), param.getDoubleVal());
            } else if (JobParameter.ParameterType.DATE.toString().equalsIgnoreCase(param.getTypeCd())) {
                jpb.addDate(param.getKeyName(), Date.from(param.getDateVal().toInstant()));
            } else {
                //log.error("Invalid value found for ParameterType: '{}'", param.getTypeCd());
                throw new IllegalArgumentException("Unknown value for Job ParameterType: '" + param.getTypeCd() + "'");
            }
        }
        return jpb.toJobParameters();
    }
}
