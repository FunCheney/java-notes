package com.fchen.concurrency.future;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Fchen
 */
@Slf4j
public class CompletableFutureTest01 implements Serializable {

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 3,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.AbortPolicy());
    public static void main(String[] args) {
        CompletableFuture.runAsync(() -> {
            log.info("当前线程：" + Thread.currentThread().getName());
            int i = 10 / 2;
            log.info("运行结果：" + i);
        });

        CompletableFuture.runAsync(() -> {
            log.info("当前线程：" + Thread.currentThread().getName());
            int i = 10 / 2;
            log.info("运行结果：" + i);
        }, executor);

    }

}
