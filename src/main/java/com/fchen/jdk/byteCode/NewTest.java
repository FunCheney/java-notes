package com.fchen.jdk.byteCode;

import java.io.File;

/**
 * 对象的创建指令
 */
public class NewTest {

    public void newInstance() {
        Object object = new Object();

        File file = new File("xx");
    }

    public void newArray() {
        int[] intArray = new int[10];
        Object[] objArr = new Object[10];
        int[][] mintArr = new int[10][10];
        String[][] strArr = new String[10][];
    }

    public void sayHello() {
        System.out.println("hello");
    }

    public void setArray(){
        int[] intArray = new int[10];
        intArray[3] = 20;
        System.out.println(intArray[1]);

        boolean[] arr = new boolean[10];
        arr[1] = true;
    }

    public void arrLength(){
        double[] arr = new double[10];
        System.out.println(arr.length);
    }

    // 类型检查指令
    public String checkCast(Object o){
        if(o instanceof  String){
            return (String) o;
        }
        return null;
    }

}
