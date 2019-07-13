package com.fchen.concurrency.singleton;

/**
 * @Classname Singleton
 * @Description 单例模式
 * @Date 2019/7/13 14:51
 * @Author by Fchen
 */
public class Singleton {

    public static final Singleton INSTANCE = new Singleton();

    private Singleton(){
        if (INSTANCE != null) {
            throw new IllegalStateException("Already initialized.");
        }
    }
}
