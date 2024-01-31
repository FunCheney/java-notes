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
public class CompletableFutureTest03 implements Serializable {
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 3,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.AbortPolicy());
    public static void main(String[] args) {
        // thenRunAsync 不能接受上一次的结果，也没有返回值
       CompletableFuture.supplyAsync(() -> {
           log.info("当前线程：" + Thread.currentThread().getName());
           int i = 10 / 4;
           try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
           log.info("运行结果：" + i);
           return i;
       },executor).thenRunAsync(() -> {
           log.info("当前线程：" + Thread.currentThread().getName());
           log.info("任务二启动。。。");
       }, executor);

    }
}
