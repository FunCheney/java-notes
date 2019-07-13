package com.fchen.concurrency.singleton;

/**
 * @Classname Singleton01
 * @Description 单例模式
 * @Date 2019/7/13 15:09
 * @Author by Fchen
 */
public class Singleton01 {
    private static final Singleton01 INSTANCE = new Singleton01();

    private Singleton01(){}

    public static Singleton01 getInstance(){
        return INSTANCE;
    }
}
