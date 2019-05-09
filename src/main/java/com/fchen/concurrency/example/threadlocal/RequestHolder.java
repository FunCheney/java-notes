package com.fchen.concurrency.example.threadlocal;

/**
 * @Classname RequestHolder
 * @Description TODO
 * @Date 2019/5/9 19:18
 * @Author by Fchen
 */
public class RequestHolder {
    private final static ThreadLocal<Long> requertHolder = new ThreadLocal<>();
    public static void add(Long id){
        requertHolder.set(id);
    }

    public static Long getId(){
        return requertHolder.get();
    }

    public static void remove(){
        requertHolder.remove();
    }
}
