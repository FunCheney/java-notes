package com.fchen.concurrency.example.concurrent.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Classname CyclicBarrierStudy
 * @Description CyclicBarrier类学习
 * @Date 2019/5/19 21:57
 * @Author by Chen
 */
@Slf4j
public class CyclicBarrierStudy {
    //给定一个值，确定当前有多少个线程同步等待
    private  static CyclicBarrier barrier = new CyclicBarrier(5);
    public static void main(String[] args) throws Exception{
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++){
            final int threadNum = i;
            Thread.sleep(1000);
            executor.execute(()->{
                try {
                    race(threadNum);
                } catch (Exception e) {
                    log.error("Exception:{}",e);
                }
            });
        }
        executor.shutdown();
    }

    private static void race(int threadNum) throws Exception{
        Thread.sleep(1000);
        log.info("{} is ready",threadNum);
        //每一个线程调用自己的await()方法，当达到定义的数目之后，
        // await后满的操作就可以执行了
        barrier.await();
        log.info("{} continue",threadNum);
    }
}
