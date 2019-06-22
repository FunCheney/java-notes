package com.fchen.concurrency.example.vola;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @Classname VolatileNotSafe
 * @Description volatile （++操作）线程不安全示例
 * @Date 2019/6/22 23:02
 * @Author by Chen
 */
@Slf4j
public class VolatileNotSafe {
    //请求总数
    public static int clientTotal = 1000;

    //同时并发的线程数
    public static int threadTotal = 20;

    public static int count = 0;

    /**
     * synchronized 修饰静态方法，锁是该类的Class对象
     */
//    public static synchronized void addCount(){
//        count++;
//    }

    public static  void addCount(){
        count++;
    }

    public static void main(String[] args) throws Exception {
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
                    addCount();
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("count:{}",count);
    }
}
