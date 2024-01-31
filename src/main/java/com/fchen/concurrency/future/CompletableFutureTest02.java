package com.fchen.concurrency.future;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author Fchen
 */
@Slf4j
public class CompletableFutureTest02 implements Serializable {
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 3,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.AbortPolicy());
    public static void main(String[] args) {
        final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            log.info("当前线程" + Thread.currentThread().getName());
            int i = 10 / 0;
            log.info("运行结果：" + i);
            return i;
        }, executor).whenComplete((res, exception) -> {
            //whenComplete虽然能得到异常信息，但是没办法修改返回值
            log.info("异步任务成功完成...结果是：" + res + ";异常是：" + exception);
        }).exceptionally(throwable -> {
            // 这里返回默认值
            return 10;
        });

        final CompletableFuture<Integer> handle = CompletableFuture.supplyAsync(() -> {
            log.info("当前线程" + Thread.currentThread().getName());
            int i = 10 / 0;
            log.info("运行结果：" + i);
            return i;
        }, executor).handle((res, e) -> {
            if (Objects.isNull(e)) {
                return 0;
            } else {
                log.error("e---> ", e);
                return 2;
            }
        });
        try {
            log.info("hanlde --> " + handle.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
