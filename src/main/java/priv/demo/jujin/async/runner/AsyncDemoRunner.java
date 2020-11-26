package priv.demo.jujin.async.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import priv.demo.jujin.async.service.AsyncExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2020/11/26
 * Copyright (C) 2020, Centum Factorial all rights reserved.
 */

@Component
@Slf4j
public class AsyncDemoRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        AsyncExecutor<String> asyncExecutor = new AsyncExecutor<>();
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            futures.add(asyncExecutor.executeAsync(() -> {
                log.info("start thread!");
                try {
                    Thread.sleep((int)(Math.random()*10) * 1000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
                return "hello";
            }));
        }
        futures.forEach(stringFuture -> {
            while (true) {
                if (stringFuture.isDone()) {
                    try {
                        log.info(stringFuture.get());
                    } catch (InterruptedException | ExecutionException e) {
                        log.error(e.getMessage());
                    }
                    break;
                }
            }
        });
    }
}
