package com.fchen.concurrency.example.threadlocal;

/**
 * @Classname RequestHolder
 * @Description 请求处理
 * @Date 2019/5/9 19:18
 * @Author by Fchen
 */
public class RequestHolder {
    private final static ThreadLocal<Long> requestHolder = new ThreadLocal<>();
    public static void add(Long id){
        requestHolder.set(id);
    }

    public static Long getId(){
        return requestHolder.get();
    }

    public static void remove(){
        requestHolder.remove();
    }
}
