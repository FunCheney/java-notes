package com.fchen.jdk.runTimeArea;

public class StackTest {
    public static void main(String[] args) {
        StackTest test = new StackTest();
        test.methodA();
    }
    public void methodA(){
        int i = 20;
        int j = 30;
        methodB();
    }

    public void methodB(){
        int k = 30;
        int m = 40;
    }
}
