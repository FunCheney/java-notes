package com.fchen.concurrency.example.waitnotify;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Classname PrintTest
 * @Description 交替打印数组与字符串
 * @Date 2019/10/17 12:43
 * @Author by Chen
 */
@Slf4j
public class PrintTest {
    static boolean flag = true;
    static Object lock = new Object();

    public static void main(String[] args) throws Exception{
        Thread waitThread = new Thread(new PrintTest.Wait(),"WaitThread");
        waitThread.start();
        Thread notifyThread = new Thread(new PrintTest.Notify(), "NotifyThread");
        notifyThread.start();
    }
    static class Wait implements Runnable{
        @Override
        public void run() {
            // 加锁，拥有lock的Monitor
            String num = "123456789";
            synchronized (lock){
                // 当条件不满足时，继续wait，同时释放lock锁
                try {
                    for (int i = 0; i < num.length(); ){
                        if(flag){
                            System.out.println(num.charAt(i));
                            flag = false;
                            i++;
                            lock.notify();
                        }else {
                            lock.wait();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Notify implements Runnable{
        @Override
        public void run() {
            //加锁，拥有lock的Monitor
            String str = "abcdefgh";
            try {
                synchronized (lock){
                    // 获取当前lock锁，然后进行通知，通知时不会释放lock锁
                    // 知道当前线程释放了lock后，WaitThread才能从Wait方法中返回

                    for (int i = 0; i < str.length();){
                        if(!flag){
                            System.out.println(str.charAt(i));
                            flag = true;
                            i++;
                            lock.notify();
                        }else {
                            lock.wait();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
