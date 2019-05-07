package com.fchen.concurrency.immutable;

import com.fchen.concurrency.annoations.ThreadSafe;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

/**
 * @Classname Immutable1
 * @Description 不可变对象
 *        java： Collections.unmodifiableXXX
 *        Guava：ImmutableXXX
 * @Date 2019/5/7 20:47
 * @Author by Fchen
 */
@Slf4j
@ThreadSafe
public class Immutable3 {
    private final static ImmutableList list = ImmutableList.of(1,2,3);

    private final static ImmutableSet set = ImmutableSet.copyOf(list);

    private final static ImmutableMap<Integer,Integer> map = ImmutableMap.of(1,2,3,4);

    private final static ImmutableMap<Integer,Integer> map2 = ImmutableMap.<Integer,Integer>builder().put(1,2).put(3,4).build();
    public static void main(String[] args) {
        map2.put(1,4);
        map.put(1,4);
        set.add(4);
        //java.lang.UnsupportedOperationException
        list.add(4);
    }

}
