package com.fchen.concurrency.jvm;

/**
 * @Classname Demo_1
 * @Description 内存分配策略
 * @Date 2019/11/1 23:26
 * @Author by Chen
 */
public class Demo_1 {
    public static void main(String[] args) {
        byte[] b = new byte[4 * 1024 * 1024];
    }
}
