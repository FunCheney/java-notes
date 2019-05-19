package com.fchen.concurrency.example.lock;

import com.fchen.concurrency.annoations.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Classname LockStudy1
 * @Description lock
 * @Date 2019/4/28 12:49
 * @Author by Fchen
 */
@Slf4j
@NotThreadSafe
public class LockStudy1 {
    //请求总数
    public static int clientTotal = 5000;

    //同时并发的线程数
    public static int threadTotal = 200;

    public static int count = 0;

    private final static Lock lock= new ReentrantLock();

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
        log.info("count:{}",count);

    }

    public static  void add(){
        lock.lock();
        try {
            count ++;
        }finally {
            lock.unlock();
        }
    }

}
