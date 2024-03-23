package com.fchen.jdk.byteCode;

public class StackOperateTest {
    public void print(){
        Object obj = new Object();
        obj.toString();
    }

    public void foo(){
        bar();
    }

    public long bar(){
        return 0;
    }

    public long nextIndex(){
        return index++;
    }
    private long index = 0;
}
