package com.fchen.concurrency.src.conditionexample.demo2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Classname MyDemo
 * @Description 线程按照顺序执行
 * @Date 2019/8/18 20:50
 * @Author by Chen
 */
public class MyDemo {

    private  int flag;
    Lock lock = new ReentrantLock();
    Condition test1 = lock.newCondition();
    Condition test2 = lock.newCondition();
    Condition test3 = lock.newCondition();

    public  void test1(){
        lock.lock();
        while (flag != 0){
            try {
                test1.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("T1线程执行");
        flag++;
        test2.signal();
        lock.unlock();
    }

    public void test2(){
        lock.lock();
        while (flag != 1){
            try {
                test2.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("T2线程执行");
        flag++;
        test3.signal();
        lock.unlock();
    }

    public void test3(){
        lock.lock();
        while (flag != 2){
            try {
                test3.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("T3线程执行");
        flag = 0;
        test1.signal();
        lock.unlock();
    }

    public static void main(String[] args) {
        MyDemo demo = new MyDemo();
        Thread1 a = new Thread1(demo);
        Thread2 b = new Thread2(demo);
        Thread3 c = new Thread3(demo);

        new Thread(a).start();
        new Thread(b).start();
        new Thread(c).start();
    }
}

class Thread1 implements Runnable{
    private MyDemo demo;

    public Thread1(MyDemo demo) {
        this.demo = demo;
    }

    @Override
    public void run() {
        while (true){
            demo.test1();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Thread2 implements Runnable{
    private MyDemo demo;

    public Thread2(MyDemo demo) {
        this.demo = demo;
    }

    @Override
    public void run() {
        while (true){
            demo.test2();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Thread3 implements Runnable{
    private MyDemo demo;

    public Thread3(MyDemo demo) {
        this.demo = demo;
    }

    @Override
    public void run() {
        while (true){
            demo.test3();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
