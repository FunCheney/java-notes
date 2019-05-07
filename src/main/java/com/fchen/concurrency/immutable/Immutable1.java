package com.fchen.concurrency.immutable;

import com.fchen.concurrency.annoations.NotThreadSafe;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Classname Immutable1
 * @Description TODO
 * @Date 2019/5/7 20:47
 * @Author by Fchen
 */
@Slf4j
@NotThreadSafe
public class Immutable1 {
    private final static Integer a = 1;
    private final static String b = "a";
    private final static Map<Integer,Integer> map = Maps.newHashMap();
    static {
        map.put(1,2);
        map.put(3,4);
        map.put(5,6);
    }

    public static void main(String[] args) {
        /**
         * 基础数据类型不可修改
         */
//        a = 2;
//        b = "3";
        /**
         * 引用类型 不允许指向其他对象，但是里面的值可以修改
         */
//        map = Maps.newHashMap();

        map.put(1,3);
        log.info("{}",map.get(1));
    }

    private void test(final int a){
        /**
         * 基本类型的变量 以final修饰 不可再修改
         */
//        a = 1;
    }
}
