package priv.demo.jujin.async.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import javax.security.auth.callback.Callback;
import java.util.concurrent.Future;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2020/11/26
 * Copyright (C) 2020, Centum Factorial all rights reserved.
 */


public class AsyncExecutor<T> {

    @Async("threadPoolTaskExecutor")
    public Future<T> executeAsync(CallBack<T> callBack) {
        return new AsyncResult<>(callBack.execute());
    }

    public interface CallBack<T> {
        T execute();
    }
}
