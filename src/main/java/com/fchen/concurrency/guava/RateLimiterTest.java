package com.fchen.concurrency.guava;

import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author: Fchen
 * @date: 2021/9/27 3:36 下午
 * @desc: 令牌桶
 */
public class RateLimiterTest {

    private static ScheduledExecutorService schedule = new ScheduledThreadPoolExecutor(1,
            new ThreadFactoryBuilder().setNameFormat("RaleLimit").setDaemon(true).build());

    public static void main(String[] args) {

        RateLimiter limiter = RateLimiter.create(2);

        schedule.scheduleAtFixedRate(() -> {
            System.out.println("get ticket: " +  limiter.acquire(20) + "s");
            System.out.println("get ticket: " +  limiter.acquire(1) + "s");
            System.out.println("get ticket: " +  limiter.acquire(1) + "s");
            System.out.println("-------------");
        }, 1, 1, TimeUnit.SECONDS);

        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
