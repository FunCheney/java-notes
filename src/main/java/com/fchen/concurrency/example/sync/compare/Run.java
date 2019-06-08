package com.fchen.concurrency.example.sync.compare;

import lombok.extern.slf4j.Slf4j;

/**
 * @Classname Run
 * @Description TODO
 * @Date 2019/6/8 19:00
 * @Author by Chen
 */

@Slf4j
public class Run {
    public static void main(String[] args) {
        MyTask task = new MyTask();
        MyThread1 thread1 = new MyThread1(task);
        thread1.start();
        MyThread2 thread2 = new MyThread2(task);
        thread2.start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        long beginTime = CommonUtils.begainTime1;
        if(CommonUtils.begainTime2 < CommonUtils.begainTime1){
            beginTime = CommonUtils.begainTime2;
        }
        long endTime = CommonUtils.endTime1;
        if(CommonUtils.endTime2 > CommonUtils.endTime1){
            endTime = CommonUtils.endTime2;
        }
        log.info("耗时：{}",(endTime - beginTime) / 1000);
    }
}
