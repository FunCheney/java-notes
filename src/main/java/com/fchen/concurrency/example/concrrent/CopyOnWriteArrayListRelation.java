package com.fchen.concurrency.example.concrrent;

import com.fchen.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Classname CopyOnWriteArrayListRelation
 * @Description 线程安全 写操作时复制 当有新的元素加入时，先从原有的数组中拷贝一份
 *       然后再新的数组上新增，最后再将原来的数组指向新的数组
 *        1.写操作是拷贝数组，耗费内存
 *        2.不能用于实时性的要求，需要复制拷贝。但可以保证数据的最终一致性。适用于读多写少的场景
 *       设计思想：
 *        1.读写分离
 *        2.最终一致性
 *        3.使用时另外开辟空间，解决并发冲突
 *        4.读操作实在原数据上进行，不需要加锁。写操作需要加锁
 */
@Slf4j
@ThreadSafe
public class CopyOnWriteArrayListRelation {
    private static List<Integer> list = new CopyOnWriteArrayList<>();

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
        log.info("size:{}",list.size());
    }

    public static  void add(int i){
        list.add(i);

    }
}
