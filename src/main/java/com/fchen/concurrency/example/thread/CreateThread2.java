package com.fchen.concurrency.example.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @Classname CreateThread2
 * @Description 实现Callable接口
 * @Date 2019/5/19 21:06
 * @Author by Chen
 */
public class CreateThread2 implements Callable<Integer>{
    @Override
    public Integer call() throws Exception {
        System.out.println("实现Callable接口");
        return 1;
    }

    public static void main(String[] args) throws Exception{
        CreateThread2 t = new CreateThread2();
        FutureTask<Integer> task = new FutureTask<>(t);
        Thread thread = new Thread(task);
        thread.start();
        Integer target = task.get();
        System.out.println("执行结果："+target);
    }
}
