package com.fchen.concurrency.example.sync.compare;

/**
 * @Classname MyThread2
 * @Description TODO
 * @Date 2019/6/8 18:56
 * @Author by Chen
 */
public class MyThread2 extends Thread {
    private MyTask task;
    public MyThread2(MyTask task){
        this.task = task;
    }

    @Override
    public void run() {
        super.run();
        CommonUtils.begainTime2 = System.currentTimeMillis();
        task.doLongTimeTask();
        CommonUtils.endTime2 = System.currentTimeMillis();
    }
}
