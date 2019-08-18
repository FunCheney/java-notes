package com.fchen.concurrency.src.conditionexample.demo1;

/**
 * @Classname MyDemo
 * @Description 线程见通信
 * @Date 2019/8/18 20:50
 * @Author by Chen
 */
public class MyDemo {

    private  int flag;

    public synchronized void test1(){
        while (flag != 0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("test1");
        flag++;
        notifyAll();
    }

    public synchronized void test2(){
        while (flag != 1){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("test2");
        flag++;
        notifyAll();
    }

    public synchronized void test3(){
        while (flag != 2){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("test3");
        flag = 0;
        notifyAll();
    }

    public static void main(String[] args) {
        MyDemo demo = new MyDemo();
        Test1 a = new Test1(demo);
        Test2 b = new Test2(demo);
        Test3 c = new Test3(demo);

        new Thread(a).start();
        new Thread(b).start();
        new Thread(c).start();
    }
}

class Test1 implements Runnable{
    private MyDemo demo;

    public Test1(MyDemo demo) {
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

class Test2 implements Runnable{
    private MyDemo demo;

    public Test2(MyDemo demo) {
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

class Test3 implements Runnable{
    private MyDemo demo;

    public Test3(MyDemo demo) {
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
