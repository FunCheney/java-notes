package com.fchen.jdk.byteCode;

import java.util.Date;

public class MethodInvokeReturnTest {
    // 方法调用：invokespecial
    public void invoke1() {
        // 类实例构造器方法：<init>()
        Date date = new Date();

        Thread t1 = new Thread();
        // 父类的方法
        super.toString();
        // 私有方法
        methodPrivate();
    }

    private void methodPrivate() {
    }

    // 方法调用指令：invokestatic
    public void invoke2() {
        methodStatic();
    }

    private static void methodStatic() {
    }

    public void invoke3() {
        Thread t1 = new Thread();
        ((Runnable) t1).run();
        Comparable<Integer> comparable = null;
        comparable.compareTo(123);
    }

    public int returnInt(){
        return 300;
    }

    public byte returnByte(){
        return 0;
    }



}
