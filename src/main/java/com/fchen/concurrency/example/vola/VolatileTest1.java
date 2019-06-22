package com.fchen.concurrency.example.vola;

import lombok.extern.slf4j.Slf4j;

/**
 * @Classname VolatileTest1
 * @Description volatile 自增变量测试
 * @Date 2019/6/13 21:38
 * @Author by Chen
 */
@Slf4j
public class VolatileTest1 {
    public static volatile int race = 0;

    public static void increase(){
        race++;
    }
    private static final int THREAD_COUNT = 5;

    public static void main(String[] args) {
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++){
                        increase();
                    }
                }
            });
            threads[i].start();
        }

        while (Thread.activeCount() > 1)
            Thread.yield();

        log.info("{}",race);
    }
}
