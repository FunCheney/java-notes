package com.fchen.concurrency.src.aqsexample;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Classname MyDemo
 * @Description 读写锁的使用
 * @Date 2019/8/8 19:55
 * @Author by Fchen
 */
public class MyDemo {
    private Map<String,Object> map = new HashMap<>();

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private Lock r = readWriteLock.readLock();

    private Lock w = readWriteLock.writeLock();

    public Object get(String key){
        r.lock();
        System.out.println(Thread.currentThread().getName()+ "读操作执行");
        try {
            Thread.sleep(3000);
            return map.get(key);
        } catch (Exception e){
            throw new RuntimeException();
        }finally {
            r.unlock();
            System.out.println(Thread.currentThread().getName()+ "写操作执行完毕");
        }
    }

    public void put(String key,Object value){
        w.lock();
        System.out.println(Thread.currentThread().getName()+ "写操作执行");
        try {
            Thread.sleep(3000);
            map.put(key,value);
        }catch (Exception e){
            throw new RuntimeException();
        }finally {
            w.unlock();
            System.out.println(Thread.currentThread().getName()+ "写操作执行完毕");
        }
    }


}
