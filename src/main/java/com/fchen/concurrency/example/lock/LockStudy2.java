package com.fchen.concurrency.example.lock;

import com.fchen.concurrency.annoations.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Classname LockStudy1
 * @Description lock
 * @Date 2019/4/28 12:49
 * @Author by Fchen
 */
@Slf4j
public class LockStudy2 {

    private final Map<String,Data> map = new TreeMap<>();
    private final static Lock lock= new ReentrantLock();

    class Data{

    }

}
