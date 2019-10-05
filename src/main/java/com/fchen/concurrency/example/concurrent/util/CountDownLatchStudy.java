package com.fchen.concurrency.example.concurrent.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Classname CountDownLatchStudy
 * @Description CountDownLatch学习
 * @Date 2019/5/16 20:50
 * @Author by Fchen
 */
@Slf4j
public class CountDownLatchStudy {
    private static int threadCount = 200;
    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for(int i = 0; i < threadCount; i++){
            final int threadNum = i;
            exec.execute(() ->{
                try {
                    test(threadNum);
                }catch (Exception e){
                    log.info("exception",e);
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        log.info("finish");
        exec.shutdown();
    }

    public static void test(int threadNum) throws Exception{
        Thread.sleep(100);
        log.info("{}",threadNum);
        Thread.sleep(100);
    }

}
