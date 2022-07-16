package com.katz.licensingservice.hystrix;

import com.katz.licensingservice.model.UserContext;
import com.katz.licensingservice.utils.UserContextHolder;

import java.util.concurrent.Callable;

/**
 * (c) Katz Solutions
 * @Author: KatlegoM
 * Date: 20220714
 *
 * <p>
 *     A simple callable class used to inject/propagate user context
 *     from running executing parent thread to threads managed by
 *     hystrix.
 * </p>
 * */
public final class UserContextCallable<T> implements Callable<T> {

    private final Callable<T> delegate;
    private UserContext userContext;

    public UserContextCallable(Callable<T> delegate, UserContext userContext) {
        this.delegate = delegate;
        this.userContext = userContext;
    }

    @Override
    public T call() throws Exception {
        try {
            UserContextHolder.set(this.userContext);
            return delegate.call();
        }finally {
            this.userContext = null;
        }
    }
}
