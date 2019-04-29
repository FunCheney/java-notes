package com.fchen.concurrency.example.atomic;

import com.fchen.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Classname AtomicBooleanCase
 * @Description AtomicBoolean测试用例
 * @Date 2019/4/29 19:53
 * @Author by Fchen
 */
@Slf4j
@ThreadSafe
public class AtomicBooleanCase {

    private static AtomicBoolean isHappend = new AtomicBoolean(false);

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
                    test();
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("isHappend:{}",isHappend.get());

    }

    /**
     * 这里是线程安全的
     */
    public static void test(){
        if(isHappend.compareAndSet(false,true)){
            log.info("execute");
        }
    }
}
