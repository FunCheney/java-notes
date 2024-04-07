package com.fchen.jdk.runTimeArea.stack;

/**
 * 动态链接
 **/
public class DynamicLikingTest {

    int num = 10;

    public void methodA(){
        System.out.println("methodA()....");
    }
    public void methodB(){
        System.out.println("methodB()....");
        methodA();
        num++;
    }
}
