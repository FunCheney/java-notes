package com.fchen.concurrency.future;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.concurrent.*;

/**
 * @author Fchen
 */
@Slf4j
public class CompletableFutureTest07 implements Serializable {
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 3,
            TimeUnit.MINUTES, new SynchronousQueue<>(), new ThreadPoolExecutor.AbortPolicy());
    public static void main(String[] args) {
        // thenApplyAsync 能接受上一次的结果，有返回值
        final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            log.info("当前线程：" + Thread.currentThread().getName());
            int i = 10 / 4;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("运行结果：" + i);
            return i;
        }, executor).thenApplyAsync(res -> {
            log.info("当前线程：" + Thread.currentThread().getName());
            log.info("thenAcceptAsync: " + (res + 10));
            return res;
        }, executor);

        try {
            final Integer res = future.get();
            log.info("res: {} ", res);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }
}
