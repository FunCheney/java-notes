package com.fchen.concurrency.example.atomic;

import com.fchen.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Classname LongAdderCase
 * @Description
 * @Date 2019/4/28 21:26
 * @Author by Fchen
 */
@Slf4j
@ThreadSafe
public class LongAdderCase {
    //请求总数
    public static int clientTotal = 5000;

    //同时并发的线程数
    public static int threadTotal = 200;

    /**
     * LongAdder 实现思想：
     *   将热点数据分离，将AtomicLong的内部核心数据value分成一个数组，每个线程访问时
     * 通过Hash等算法 运算到其中一个数字进行计数，最终的结果为这个数组的求和累计。
     * 其中的热点数据value会被分成多个子单元。每个子单元独自维护内部的值。当前对象的值，
     * 有每个子单元的值累计合成。这样的话，热点就进行了有效的分离，并提高了并行度。
     * LongAdder相当于在AtomicLong的基础上，将更新压力分散到各个节点上。
     *    在低并发的时候，直接更新 保证和Atomic的效率基本一致
     *    在高并发时，分散更新，提高性能
     *
     *   缺点：在统计的时候，如果有并发更新，则统计数据有误差
     */
    public static LongAdder count = new LongAdder() ;

    public static void main(String[] args) throws Exception{
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
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

    /**
     * 这里是线程安全的
     */
    public static  void add(){
        count.increment();;
//        count ++;
    }
}
