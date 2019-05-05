package com.fchen.concurrency.example.sync;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Classname SynchronizedExample1
 * @Description SynchronizedExample1
 * @Date 2019/4/29 20:13
 * @Author by Fchen
 */
@Slf4j
public class SynchronizedExample1 {
    /**
     * 修饰一个代码块
     *  同步代码块作用于当前对象，不同调用对象之间互不影响
     *  当一个方法里面整个方法都是用 synchronized 修饰时，他的做用与修饰整个方法是一样的
     */
    public void test1(int j){
        synchronized (this){
            for(int i = 0; i < 10; i++){
                log.info("test1 {} - {}",j,i);
            }
        }
    }

    /**
     * 修饰一个方法
     *  作用于调用对象，不同的调用对象之间相互不影响
     *  如果子类继承的父类，以test2为例 子类的方法是不含有 synchronized 关键字的
     *   原因: synchronized 不是方法申明的一部分 ，子类想使用synchronized 要显示的申明
     */
    public synchronized void test2(int j){
        for(int i = 0; i < 10; i++){
            log.info("test2 {} - {}",j,i);
        }
    }

    public static void main(String[] args) {
        SynchronizedExample1 example1 = new SynchronizedExample1();
        SynchronizedExample1 example2 = new SynchronizedExample1();

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(()->{
            example1.test2(1);
        });
        executorService.execute(()->{
            example2.test2(2);
        });

    }
}
