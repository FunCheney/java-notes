package com.fchen.jdk.byteCode;

import org.junit.Test;

/**
 * 类型转换指令
 */
public class ClassCastTest {

    /**
     * 宽化类型转化
     *
     * 基本测试
     */
    public void upCast() {
        int i = 10;
        long l = i;
        float f = l;
        double d = f;

        float f1 = l;
        double d1 = l;

        double d2 = f1;
    }

    /**
     * 举例精度损失的问题
     */
    @Test
    public void upCast2(){
        int i = 123123123;
        float f = i;
        System.out.println(f);
        long l = 123123123123L;
        double d = l;
        System.out.println(d);

        long l1 = 123123123123123123L;
        double d1 = l1;
        System.out.println(d1);
        float f1 = l1;
        System.out.println(f1);

        float f2 = 10.2F;
        long l3 = (long) f2;
        System.out.println(l3);
    }

    /**
     * 针对byte/short 等转换为容量大的类型，
     * 将此类型看做 int 类型处理
     */
    public void upCast3(byte b){
        int i = b; // 没有 to i，说明 byte 类型当做 int 类型
        long l = b;
        double d = b;
    }

    /**
     * char 类型
     *
     * @param s
     */
    public void upCast4(short s){
        int i = s;
        long l = s;
        double d = s;
    }

    /**
     * 窄化类型处理
     */
    public void downCast1(){
        int i = 10;
        byte b = (byte) i;
        short s = (short) i;
        char c = (char) i;

        long l = 10L;
        int i1 = (int) l;
        byte b1 = (byte) l;
        float f =  l;
    }


    /**
     *
     */
    @Test
    public void downCast2(){
        short s = 10;
        byte b = (byte) s;

        int i = 128;
        byte b1 = (byte) i;
        System.out.println(b1);
    }
}
