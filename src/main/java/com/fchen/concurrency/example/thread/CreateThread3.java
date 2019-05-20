package com.fchen.concurrency.example.thread;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @Classname CreateThread2
 * @Description Lambda表达式
 * @Date 2019/5/19 21:06
 * @Author by Chen
 */
public class CreateThread3 {
    public static void main(String[] args) {
        List<Integer> valuse = Arrays.asList(10,20,30,40);
        int res = new CreateThread3().add(valuse);
        System.out.println("result:"+ res);
    }

    public int add(List<Integer> values){
        /**
         * 并行执行 打印无序
         */
        values.parallelStream().forEach(System.out::println);
        /**
         * 打印有序
         */
        values.stream().forEach(System.out::println);

        return values.parallelStream().mapToInt(a -> a).sum();
    }

}
