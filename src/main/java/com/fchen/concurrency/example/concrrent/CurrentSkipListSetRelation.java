package com.fchen.concurrency.example.concrrent;

import com.fchen.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.*;

/**
 * @Classname CurrentSkipListSetRelation
 *    这里的线程安全是最基础的 add() remove() 操作
 *      removeAll() addAll() 等是线程不安全的，需要额外增加锁
 * @Description
 */
@Slf4j
@ThreadSafe
public class CurrentSkipListSetRelation {
    private static Set<Integer> set = new ConcurrentSkipListSet<>();

    //请求总数
    public static int clientTotal = 5000;

    //同时并发的线程数
    public static int threadTotal = 200;

    public static int count = 0;

    public static void main(String[] args) throws Exception{
        //线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        //信号量
        final Semaphore semaphore = new Semaphore(threadTotal);
        //计数器闭锁
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 0; i < clientTotal; i++){
            final int count = i;
            executorService.execute(()->{
                try {
                    semaphore.acquire();
                    add(count);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("size:{}",set.size());
    }

    public static  void add(int i){
        set.add(i);

    }
}
