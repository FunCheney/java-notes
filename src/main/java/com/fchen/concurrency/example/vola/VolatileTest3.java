package com.fchen.concurrency.example.vola;

/**
 * @Classname VolatileTest3
 * @Description TODO
 * @Date 2019/6/29 17:50
 * @Author by Fchen
 */
public class VolatileTest3 {
    private static volatile int count = 0;

    public static void main(String[] args) {
        System.out.println(count);
    }
}
