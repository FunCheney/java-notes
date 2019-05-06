package com.fchen.concurrency.singleton;

import com.fchen.concurrency.annoations.NotRecommend;
import com.fchen.concurrency.annoations.ThreadSafe;

/**
 * @Classname Singleton1
 * @Description 单例实现方式 懒汉模式
 * @Date 2019/5/6 20:17
 * @Author by Fchen
 */
@ThreadSafe
@NotRecommend
public class Singleton3 {
    /**
     * 构造方法私有化
     */
    private Singleton3() {
    }

    /**
     * 单例对象
     */
    private static Singleton3 instance = null;

    /**
     * 静态的工厂方法
     * @return
     */
    public static synchronized Singleton3 getInstance(){
        if(instance == null){
            instance = new Singleton3();
        }
        return instance;
    }
}
