package com.fchen.jdk.byteCode;

import java.util.Date;
import java.util.Objects;

public class LoadAndStoreTest {

    // 1. 局部变量压栈指令
    public void load(int num, Object o, long count, boolean flag, short[] arr) {
        System.out.println(num);
        System.out.println(o);
        System.out.println(count);
        System.out.println(flag);
        System.out.println(arr);
    }
    public void pushConstLdc() {
        int i = -1;
        int a = 5;
        int b = 6;
        int c = 127;
        int d = 128;
        int e = 32767;
        int f = 32768;
    }
    public void constLdc() {
        long a1 = 1;
        long a2 = 2;
        float b1 = 2;
        float b2 = 3;
        double c1 = 1;
        double c2 = 2;
        Date d = null;
    }

    // 3. 出栈装入局部变量表指令
    public void store (int k, double c) {
        int m = k + 2;
        long l = 12;
        String str = "hello";
        float f = 10.0F;
        c = 10;
    }

    public void foo(long l, float f) {
        {
            int i = 0;
        }
        {
            String s = "hello";
        }
    }






}
