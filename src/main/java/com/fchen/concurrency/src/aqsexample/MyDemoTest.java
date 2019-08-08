package com.fchen.concurrency.src.aqsexample;

/**
 * @Classname MyDemoTest
 * @Description
 * @Date 2019/8/8 20:02
 * @Author by Fchen
 */
public class MyDemoTest {
    public static void main(String[] args) {
        MyDemo d = new MyDemo();

        new Thread( new Runnable(){
            @Override
            public void run() {
                d.put("a",10);
            }
        }).start();

        new Thread( new Runnable(){
            @Override
            public void run() {
                d.put("b",10);
            }
        }).start();

        new Thread( new Runnable(){
            @Override
            public void run() {
                d.put("c",10);
            }
        }).start();
    }

}
