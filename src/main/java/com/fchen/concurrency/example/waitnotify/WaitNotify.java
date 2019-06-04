package com.fchen.concurrency.example.waitnotify;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Classname WaitNotify
 * @Description 等待通知机制
 * @Date 2019/6/4 23:30
 * @Author by Chen
 */
@Slf4j
public class WaitNotify {
    static boolean flag = true;
    static Object lock = new Object();

    public static void main(String[] args) throws Exception{
        Thread waitThread = new Thread(new Wait(),"WaitThread");
        waitThread.start();
        TimeUnit.SECONDS.sleep(1);
        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        notifyThread.start();
    }
     static class Wait implements Runnable{
         @Override
         public void run() {
             // 加锁，拥有lock的Monitor
             synchronized (lock){
                 // 当条件不满足时，继续wait，同时释放lock锁
                 while (flag){
                     try {
                         log.info("{} flag is true. wait @ {}",
                                 Thread.currentThread(), new SimpleDateFormat("HH:mm:ss").format(new Date()));
                         lock.wait();
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 }
                 // 条件满足时，完成工作
                 log.info("{} flag is false. wait @ {}",
                         Thread.currentThread(), new SimpleDateFormat("HH:mm:ss").format(new Date()));
             }
         }
     }

     static class Notify implements Runnable{
         @Override
         public void run() {
             //加锁，拥有lock的Monitor
             try {
                 synchronized (lock){
                     // 获取当前lock锁，然后进行通知，通知时不会释放lock锁
                     // 知道当前线程释放了lock后，WaitThread才能从Wait方法中返回
                     log.info("{} hold lock. notify @ {}",
                             Thread.currentThread(), new SimpleDateFormat("HH:mm:ss").format(new Date()));
                     lock.notifyAll();
                     flag = false;
                     TimeUnit.SECONDS.sleep(5);
                 }
                 //再次加锁
                 synchronized (lock){
                     log.info("{} hold lock again. sleep @ {}",
                             Thread.currentThread(), new SimpleDateFormat("HH:mm:ss").format(new Date()));
                 }
                 TimeUnit.SECONDS.sleep(5);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
     }
}
