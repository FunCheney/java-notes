package com.fchen.concurrency.singleton;

import com.fchen.concurrency.annoations.ThreadSafe;

/**
 * @Classname Singleton1
 * @Description 单例实现方式 懒汉模式
 * @Date 2019/5/6 20:17
 * @Author by Fchen
 */
@ThreadSafe
public class Singleton5 {
    /**
     * 构造方法私有化
     */
    private Singleton5() {
    }

    /**
     * 单例对象 volatile 加 双重检测机制 禁止 指令重排
     */
    private volatile static Singleton5 instance = null;

    /**
     * 静态的工厂方法
     * @return
     */
    public static synchronized Singleton5 getInstance(){
        //双重检测机制
        if(instance == null){
            //同步锁
            synchronized (Singleton5.class){
                if(instance == null){
                    /**
                     *  1. memory = allocate() 分配对象的内存空间
                     *  2. 初始化对象
                     *  3. instance = memory 设置instance指向刚分配的内存
                     */
                    instance = new Singleton5();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        Singleton5.getInstance();
    }
}
