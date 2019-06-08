package com.fchen.concurrency.example.sync.compare;

import lombok.extern.slf4j.Slf4j;

/**
 * @Classname MyTask
 * @Description
 * @Date 2019/6/8 18:50
 * @Author by Chen
 */
@Slf4j
public class MyTask {
    private String getData1;
    private String getData2;
    public synchronized void doLongTimeTask1(){
        try {
          log.info("begain task");
          Thread.sleep(3000);
          getData1 = "长时间处理任务后返回值1 threadName = " + Thread.currentThread().getName();
            getData2 = "长时间处理任务后返回值2 threadName = " + Thread.currentThread().getName();
            log.info("{}",getData1);
            log.info("{}",getData2);
            log.info("end task");
        }catch (Exception e){

        }
    }

    public void doLongTimeTask(){
        try {
            log.info("begain task");
            Thread.sleep(3000);
            String privateGetData1 = "长时间处理任务后返回值1 threadName = " + Thread.currentThread().getName();
            String privateGetData2 = "长时间处理任务后返回值2 threadName = " + Thread.currentThread().getName();
            synchronized (this){
                getData1 = privateGetData1;
                getData2 = privateGetData2;
            }
            log.info("{}",getData1);
            log.info("{}",getData2);
            log.info("end task");
        }catch (Exception e){

        }
    }
}
