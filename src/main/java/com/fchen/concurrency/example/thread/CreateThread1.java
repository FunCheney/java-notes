package com.fchen.concurrency.example.thread;

/**
 * @Classname CreateThread1
 * @Description 继承Thread类
 * @Date 2019/5/19 18:04
 * @Author by Chen
 */
public class CreateThread1 extends Thread {
    @Override
    public void run() {
        System.out.println(getName() + "执行线程");
    }

    public static void main(String[] args) {
        CreateThread1 t1 = new CreateThread1();
        t1.run();
    }
}
