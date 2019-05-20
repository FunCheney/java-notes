package com.fchen.concurrency.example.thread;

/**
 * @Classname Target
 * @Description TODO
 * @Date 2019/5/20 20:30
 * @Author by Fchen
 */
public class Target implements Runnable {
    @Override
    public void run() {
        while (true){
            System.out.println(Thread.currentThread().getName() + "....." );
        }
    }
}
