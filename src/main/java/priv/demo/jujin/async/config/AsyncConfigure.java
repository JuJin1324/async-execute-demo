package priv.demo.jujin.async.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2020/11/26
 * Copyright (C) 2020, Centum Factorial all rights reserved.
 */

@Configuration
@EnableAsync
public class AsyncConfigure {

    /* 참조사이트
     * http://dveamer.github.io/java/SpringAsync.html
     * https://sunghs.tistory.com/76
     */
    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(6);
        taskExecutor.setMaxPoolSize(12);
        taskExecutor.setThreadNamePrefix("Executor-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
