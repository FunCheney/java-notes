package com.fchen.concurrency.example.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @Classname Semaphore
 * @Description Semaphore 信号量学习
 * @Date 2019/5/16 20:50
 * @Author by Fchen
 */
@Slf4j
public class SemaphoreStudy3 {
    private static int threadCount = 20;
    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(3);
        for(int i = 0; i < threadCount; i++){
            final int threadNum = i;
            exec.execute(() ->{
                try {
                    //尝试获取一个许可
                    if(semaphore.tryAcquire()){
                        test(threadNum);
                        //释放一个许可
                        semaphore.release();
                    }
                }catch (Exception e){
                    log.info("exception",e);
                }finally {
                }
            });
        }
        exec.shutdown();
    }

    public static void test(int threadNum) throws Exception{
        log.info("{}",threadNum);
        Thread.sleep(1000);
    }

}
