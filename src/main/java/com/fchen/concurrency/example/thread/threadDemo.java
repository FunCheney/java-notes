package com.fchen.concurrency.example.thread;

/**
 * @Classname threadDemo
 * @Description 线程的优先级
 * @Date 2019/5/20 20:29
 * @Author by Fchen
 */
public class threadDemo {
    public static void main(String[] args) {
        Thread t = new Thread(new Target());
        Thread t1 = new Thread(new Target());
        Thread t2 = new Thread(new Target());
        Thread t3 = new Thread(new Target());
        Thread t4 = new Thread(new Target());

        t.setPriority(Thread.NORM_PRIORITY);
        t1.setPriority(Thread.MAX_PRIORITY);
        t.start();
        t1.start();
    }
}
