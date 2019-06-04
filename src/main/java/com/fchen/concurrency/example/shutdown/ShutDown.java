package com.fchen.concurrency.example.shutdown;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Classname ShutDown
 * @Description 线程的终止
 * @Date 2019/6/4 23:15
 * @Author by Chen
 */
@Slf4j
public class ShutDown {
    public static void main(String[] args) throws Exception{
        Runner one = new Runner();
        Thread countThread = new Thread(one,"countThread");
        countThread.start();
        //睡眠1秒，main 线程对countThread进行中断，使countThread能够感知线程而结束
        TimeUnit.SECONDS.sleep(1);
        countThread.interrupt();
        Runner two = new Runner();
        countThread = new Thread(two,"countThread");
        countThread.start();
        //睡眠1秒，main 线程对Runner two 进行取消，使countThread能够感知线程而结束
        TimeUnit.SECONDS.sleep(1);
        two.cancel();
    }

    public static class Runner implements Runnable{
        private long i;
        private volatile boolean on = true;
        @Override
        public void run() {
            while (on && !Thread.currentThread().isInterrupted()){
                i++;
            }
            log.info("count i = {}",i);
        }

        public void cancel(){
            on = false;
        }
    }

}
