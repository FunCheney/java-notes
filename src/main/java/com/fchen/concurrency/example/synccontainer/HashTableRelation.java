package com.fchen.concurrency.example.synccontainer;

import com.fchen.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @Classname ArrayListRelation
 * @Description
 * @Date 2019/5/12 16:10
 * @Author by Chen
 */
@Slf4j
@ThreadSafe
public class HashTableRelation {
    private static Hashtable<Integer,Integer> map = new Hashtable<>();

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
                    put(count,count);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("size:{}",map.size());
    }

    public static  void put(int i,int v){
        map.put(i,v);

    }
}
