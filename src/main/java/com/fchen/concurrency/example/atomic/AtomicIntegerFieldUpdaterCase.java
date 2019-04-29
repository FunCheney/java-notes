package com.fchen.concurrency.example.atomic;

import com.fchen.concurrency.annoations.ThreadSafe;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @Classname AtomicIntegerFieldUpdaterCase
 * @Description AtomicIntegerFieldUpdater测试用例
 * @Date 2019/4/29 19:43
 * @Author by Fchen
 */
@Slf4j
@ThreadSafe
public class AtomicIntegerFieldUpdaterCase {
    private static AtomicIntegerFieldUpdater<AtomicIntegerFieldUpdaterCase> updater = AtomicIntegerFieldUpdater.newUpdater(
         AtomicIntegerFieldUpdaterCase.class,"count"
    );
    @Getter
    private volatile int count = 100;

    private static AtomicIntegerFieldUpdaterCase example = new AtomicIntegerFieldUpdaterCase();

    public static void main(String[] args) {
        if(updater.compareAndSet(example,100,120)){
            log.info("update success====1=== count:{}",example.getCount());
        }
        if(updater.compareAndSet(example,100,120)){
            log.info("update success count:{}",example.getCount());
        }else{
            log.info("update failed count:{}",example.getCount());
        }
    }
}
