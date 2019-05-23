package com.fchen.concurrency.example.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @Classname FutureStudy1
 * @Description
 * @Date 2019/5/23 15:45
 * @Author by Fchen
 */
@Slf4j
public class FutureStudy1 {
    static class MyCallable implements Callable<String>{
        @Override
        public String call() throws Exception {
            log.info("do something in callable");
            Thread.sleep(5000);
            return "ok";
        }
    }
    public static void main(String[] args)  {
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            Future<String> fulture = executorService.submit(new MyCallable());
            log.info("do other thing");
            Thread.sleep(1000);
            String result = fulture.get();
            log.info("result:{}",result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
