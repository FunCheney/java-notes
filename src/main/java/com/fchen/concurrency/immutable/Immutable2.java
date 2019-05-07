package com.fchen.concurrency.immutable;

import com.fchen.concurrency.annoations.ThreadSafe;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

/**
 * @Classname Immutable1
 * @Description 不可变对象
 * @Date 2019/5/7 20:47
 * @Author by Fchen
 */
@Slf4j
@ThreadSafe
public class Immutable2 {

    private static Map<Integer,Integer> map = Maps.newHashMap();
    static {
        map.put(1,2);
        map.put(3,4);
        map.put(5,6);
        map = Collections.unmodifiableMap(map);
    }

    public static void main(String[] args) {
        //java.lang.UnsupportedOperationException
        map.put(1,3);
        log.info("{}",map.get(1));
    }


}
