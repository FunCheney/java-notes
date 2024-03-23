package com.fchen.jdk.byteCode;

/**
 * 方法调用补充说明
 */
public class InterfaceMethodTest {

    public static void main(String[] args) {
        AA aa = new NN();

        aa.method2();
        AA.method1();
    }

}

interface AA {
    public static void method1(){

    }

    public default void method2(){

    }
}

class NN implements AA {

}
