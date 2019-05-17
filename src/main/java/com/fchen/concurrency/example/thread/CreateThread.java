package com.fchen.concurrency.example.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @Classname CreateThread
 * @Description 创建线程
 * @Date 2019/5/17 21:51
 * @Author by Fchen
 */
@Slf4j
public class CreateThread implements Runnable{
    @Override
    public void run() {
        try {
            Thread.sleep(5000);
            log.info("创建线程的方式1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //创建线程，并指定任务
        Thread thread = new Thread(new CreateThread());
        //启动线程
        thread.start();
    }
}
