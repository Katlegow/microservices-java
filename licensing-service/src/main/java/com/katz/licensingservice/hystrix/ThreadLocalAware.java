package com.katz.licensingservice.hystrix;

import com.katz.licensingservice.utils.UserContextHolder;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 * (c) Katz Solutions
 * @Author: KatlegoM
 * Date: 20220714
 *
 * <p>
 *     A simple custum hystrix concurrency strategy to help propagate/inject
 *     user context to the threads managed by hystrix.
 * </p>
 * */
public class ThreadLocalAware extends HystrixConcurrencyStrategy {
    private final HystrixConcurrencyStrategy existing;

    public ThreadLocalAware(HystrixConcurrencyStrategy existing) {
        this.existing = existing;
    }

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return existing == null ? super.getBlockingQueue(maxQueueSize) : existing.getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {

//        Callable<T> cal = () -> {
//            UserContextHolder.set(UserContextHolder.get());
//            return callable.call();
//        };

        return existing == null
                ? super.wrapCallable(new UserContextCallable<>(callable, UserContextHolder.get()))
                : existing.wrapCallable(new UserContextCallable<>(callable, UserContextHolder.get()));
    }
}
