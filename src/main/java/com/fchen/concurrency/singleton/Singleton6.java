package com.fchen.concurrency.singleton;

import com.fchen.concurrency.annoations.ThreadSafe;

/**
 * @Classname Singleton2
 * @Description 单例实现方式 饿汉模式
 * @Date 2019/5/6 20:17
 * @Author by Fchen
 */
@ThreadSafe
public class Singleton6 {
    /**
     * 构造方法私有化
     */
    private Singleton6() {
    }
    /**
     * 单例对象
     */
    private static Singleton6 instance;
    /**
     * 使用静态代码块
     */
    static {
        instance = new Singleton6();
    }


    /**
     * 静态的工厂方法
     * @return
     */
    public static Singleton6 getInstance(){
        return instance;
    }
}
