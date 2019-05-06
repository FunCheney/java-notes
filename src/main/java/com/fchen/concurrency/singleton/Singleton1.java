package com.fchen.concurrency.singleton;

import com.fchen.concurrency.annoations.NotThreadSafe;

/**
 * @Classname Singleton1
 * @Description 单例实现方式 懒汉模式
 * @Date 2019/5/6 20:17
 * @Author by Fchen
 */
@NotThreadSafe
public class Singleton1 {
    /**
     * 构造方法私有化
     */
    private Singleton1() {
    }

    /**
     * 单例对象
     */
    private static Singleton1 instance = null;

    /**
     * 静态的工厂方法
     * @return
     */
    public static Singleton1 getInstance(){
        if(instance == null){
            // 多线程的情况下 这里可能会被调用两次 拿到两个不同的对象
            instance = new Singleton1();
        }
        return instance;
    }
}
