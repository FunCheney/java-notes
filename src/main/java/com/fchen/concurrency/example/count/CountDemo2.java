package com.fchen.concurrency.example.count;

import com.fchen.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Classname CountDemo2
 * @Description TODO
 * @Date 2019/4/28 21:26
 * @Author by Fchen
 */
@Slf4j
@ThreadSafe
public class CountDemo2 {
    //请求总数
    public static int clientTotal = 5000;

    //同时并发的线程数
    public static int threadTotal = 200;

    public static AtomicInteger count = new AtomicInteger(0) ;

    public static void main(String[] args) throws Exception{
        //线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        //信号量
        final Semaphore semaphore = new Semaphore(threadTotal);
        //计数器闭锁
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 0; i < clientTotal; i++){
            executorService.execute(()->{
                try {
                    semaphore.acquire();
                    add();
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("count:{}",count.get());

    }

    /**
     * 这里线程不安全
     */
    public static  void add(){
        count.incrementAndGet();
//        count ++;
    }
}