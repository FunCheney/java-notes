package com.fchen.concurrency.example.atomic;

import com.fchen.concurrency.annoations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @Classname AtomicReferenceCase
 * @Description AtomicReference 测试类
 * @Date 2019/4/29 19:37
 * @Author by Fchen
 */
@Slf4j
@ThreadSafe
public class AtomicReferenceCase {

    private static AtomicReference<Integer> count = new AtomicReference<>(0);

    public static void main(String[] args) {
        //2
        count.compareAndSet(0,2);
        //no
        count.compareAndSet(0,1);
        //no
        count.compareAndSet(1,3);
        //4
        count.compareAndSet(2,4);
        //no
        count.compareAndSet(3,5);
        log.info("count:{}",count);
    }
}
