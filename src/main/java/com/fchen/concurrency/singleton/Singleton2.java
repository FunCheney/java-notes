package com.fchen.concurrency.singleton;

import com.fchen.concurrency.annoations.ThreadSafe;

/**
 * @Classname Singleton2
 * @Description 单例实现方式 饿汉模式
 * @Date 2019/5/6 20:17
 * @Author by Fchen
 */
@ThreadSafe
public class Singleton2 {
    /**
     * 构造方法私有化
     */
    private Singleton2() {
    }

    /**
     * 单例对象
     */
    private static Singleton2 instance = new Singleton2();

    /**
     * 静态的工厂方法
     * @return
     */
    public static Singleton2 getInstance(){
        return instance;
    }
}
