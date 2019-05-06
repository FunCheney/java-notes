package com.fchen.concurrency.singleton;

import com.fchen.concurrency.annoations.Recommend;
import com.fchen.concurrency.annoations.ThreadSafe;

/**
 * @Classname Singleton7
 * @Description 使用枚举实现单例模式: 最安全
 * @Date 2019/5/6 21:06
 * @Author by Fchen
 */
@ThreadSafe
@Recommend
public class Singleton7 {

    /**
     * 私有构造函数
     */
    private Singleton7() {
    }
    public static Singleton7 getInstance(){
        return Singleton.INSTANCE.getInstance();
    }
    private enum Singleton{
        INSTANCE;

        private Singleton7 singleton;

        /**
         *  JVM 保证这个方法绝对只调用一次
         */
        Singleton(){
            singleton = new Singleton7();
        }

        public Singleton7 getInstance(){
            return singleton;
        }
    }
}
