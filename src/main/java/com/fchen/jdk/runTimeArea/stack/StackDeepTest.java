package com.fchen.jdk.runTimeArea.stack;

public class StackDeepTest {
    private static int count=0;
    public static void recursion(){
        count++;
        recursion();
    }

    /**
     *
     * 置栈的大小:-Xss256K
     * @param args
     */
    public static void main(String args[]){
        try{
            recursion();
        } catch (Throwable e){
            System.out.println("deep of calling="+count);
            e.printStackTrace();
        }
    }
}