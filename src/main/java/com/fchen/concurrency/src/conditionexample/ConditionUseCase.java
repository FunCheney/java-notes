package com.fchen.concurrency.src.conditionexample;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Classname ConditionUseCase
 * @Description Condition使用示例
 * @Date 2019/8/18 21:16
 * @Author by Chen
 */
public class ConditionUseCase {
    /** 创建锁*/
    private Lock lock = new ReentrantLock();
    /** 使用锁的newCondition()方法*/
    private Condition condition = lock.newCondition();

    public void conditionWait(){
        lock.lock();
        try {
            /** 调用Condition的await() 方法*/
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void conditionSingal(){
        lock.unlock();
        try {
            /** 调用condition的signal()方法*/
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}
