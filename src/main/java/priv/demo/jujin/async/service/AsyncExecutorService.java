package priv.demo.jujin.async.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.security.auth.callback.Callback;
import java.util.concurrent.Future;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2020/11/26
 * Copyright (C) 2020, Centum Factorial all rights reserved.
 */


@Service
@Slf4j
public class AsyncExecutorService {

    @Async("threadPoolTaskExecutor")
    public Future<String> sayHelloAsync() {
        try {
            log.info("Method sayHello called in Async way.");
            Thread.sleep((int)(Math.random()*10) * 1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        return new AsyncResult<>("hello!");
    }
}
