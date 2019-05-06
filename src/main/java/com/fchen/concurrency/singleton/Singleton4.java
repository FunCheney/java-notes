package com.fchen.concurrency.singleton;

import com.fchen.concurrency.annoations.NotRecommend;
import com.fchen.concurrency.annoations.NotThreadSafe;
import com.fchen.concurrency.annoations.ThreadSafe;

/**
 * @Classname Singleton1
 * @Description 单例实现方式 懒汉模式 双重同步锁
 * @Date 2019/5/6 20:17
 * @Author by Fchen
 */
@NotThreadSafe
public class Singleton4 {
    /**
     * 构造方法私有化
     */
    private Singleton4() {
    }

    /**
     * 单例对象
     */
    private static Singleton4 instance = null;

    /**
     * 静态的工厂方法
     * @return
     */
    public static synchronized Singleton4 getInstance(){
        //双重检测机制
        if(instance == null){
            //同步锁
            synchronized (Singleton4.class){
                if(instance == null){
                    /**
                     *  1. memory = allocate() 分配对象的内存空间
                     *  2. 初始化对象
                     *  3. instance = memory 设置instance指向刚分配的内存
                     *
                     *  JVM 和cpu优化，发生了指令重排
                     *  1. memory = allocate() 分配对象的内存空间
                     *  3. instance = memory 设置instance指向刚分配的内存
                     *  2. ctoInstance() 初始化对象
                     */
                    instance = new Singleton4();
                }
            }
        }
        return instance;
    }
}
