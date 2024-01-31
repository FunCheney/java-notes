package com.fchen.concurrency.thread;

/**
 * @author: Fchen
 * @date: 2021/7/21 11:05 上午
 * @desc: TODO
 */
public class MyThreadLocal {
    public static void main(String[] args) {

        ThreadLocal<String> localName = new ThreadLocal();
        localName.set("张三");
        String name = localName.get();
        System.out.println(name);
        localName.remove();

//        ThreadLocal.ThreadLocalMap threadLocalMap = new ThreadLocal.ThreadLocalMap(localName, "111");
    }
}
