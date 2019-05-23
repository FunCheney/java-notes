package com.fchen.concurrency.example.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @Classname FutureTaskStudy
 * @Description
 * @Date 2019/5/23 15:53
 * @Author by Fchen
 */
@Slf4j
public class FutureTaskStudy {
    public static void main(String[] args) throws  Exception{
        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                log.info("do something in callable");
                Thread.sleep(5000);
                return "ok";
            }
        });
        new Thread(futureTask).start();
        log.info("do other thing");
        Thread.sleep(1000);
        String result = futureTask.get();
        log.info("result:{}",result);
    }
}
