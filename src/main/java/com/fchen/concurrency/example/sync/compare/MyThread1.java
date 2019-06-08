package com.fchen.concurrency.example.sync.compare;

/**
 * @Classname MyThread1
 * @Description TODO
 * @Date 2019/6/8 18:56
 * @Author by Chen
 */
public class MyThread1 extends Thread {
    private MyTask task;
    public MyThread1(MyTask task){
        this.task = task;
    }

    @Override
    public void run() {
        super.run();
        CommonUtils.begainTime1 = System.currentTimeMillis();
        task.doLongTimeTask();
        CommonUtils.endTime1 = System.currentTimeMillis();
    }
}
