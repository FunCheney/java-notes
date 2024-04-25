package com.fchen.jdk.stringTable.java;

import org.junit.Test;

/**
 * String的基本使用:体现String的不可变性
 *
 */
public class StringTest1 {
    @Test
    public void test1() {
        //字面量定义的方式，"abc"存储在字符串常量池中
        String s1 = "abc";
        String s2 = "abc";
        s1 = "hello";

        //判断地址：true  --> false
        System.out.println(s1 == s2);

        System.out.println(s1);
        //abc
        System.out.println(s2);

    }

    @Test
    public void test2() {
        String s1 = "abc";
        String s2 = "abc";
        s2 += "def";
        //abcdef
        System.out.println(s2);
        //abc
        System.out.println(s1);
    }

    @Test
    public void test3() {
        String s1 = "abc";
        String s2 = s1.replace('a', 'm');
        //abc
        System.out.println(s1);
        //mbc
        System.out.println(s2);
    }
}
