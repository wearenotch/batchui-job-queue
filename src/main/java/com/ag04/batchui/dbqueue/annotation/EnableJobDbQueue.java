package com.ag04.batchui.dbqueue.annotation;
import com.ag04.batchui.dbqueue.DbQueueModuleConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(DbQueueModuleConfiguration.class)
@Configuration
public @interface EnableJobDbQueue {

}
