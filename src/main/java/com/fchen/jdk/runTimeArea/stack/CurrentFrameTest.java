package com.fchen.jdk.runTimeArea.stack;

public class CurrentFrameTest {
    public void methodA(){
        System.out.println("当前栈帧对应的方法->methodA");
        methodB();
        System.out.println("当前栈帧对应的方法->methodA");
    }
    public void methodB(){
        System.out.println("当前栈帧对应的方法->methodB");
    }
}