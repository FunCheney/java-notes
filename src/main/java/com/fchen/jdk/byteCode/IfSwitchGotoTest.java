package com.fchen.jdk.byteCode;

public class IfSwitchGotoTest {

    // 1. 条件跳转指令
    public void compare1() {
        int a = 0;
        if (a == 0) {
            a = 10;
        } else {
            a = 20;
        }
    }

    public boolean compareNull(String str) {
        if (str == null) {
            return true;
        } else {
            return false;
        }
    }

    // 结合比较指令
    public void compare2() {
        float f1 = 9;
        float f2 = 10;
        System.out.println(f1 < f2);
    }

    public void compare3() {
        int i1 = 10;
        long l1 = 20;
        System.out.println(i1 < l1);
    }

    public int compare4(double d) {
        if (d > 50) {
            return 1;
        } else {
            return -1;
        }
    }

    // 2. 比较条件跳转指令
    public void ifCompare1() {
        int i = 10;
        int j = 30;
        System.out.println(i < j);
    }

    public void ifCompare2() {
        short s1 = 8;
        byte s2 = 10;
        System.out.println(s1 < s2);
    }

    public void ifCompare3() {
        Object o1 = new Object();
        Object o2 = new Object();
        System.out.println(o1 == o2);
        System.out.println(o1 != o2);
    }

    // 多条件分支跳转
    public void switch1(int select) {
        int num;
        switch (select) {
            case 1:
                num = 10;
                break;
            case 2:
                num = 20;
                break;
            case 3:
                num = 30;
                break;
            default:
                num = 40;
        }
    }

    public void switch2(int select) {
        int num;
        switch (select) {
            case 10:
                num = 10;
                break;
            case 40:
                num = 20;
                break;
            case 30:
                num = 30;
                break;
            default:
                num = 40;
        }
    }
    // jdk7 引入 String
    public void switch3(String select) {
        switch (select) {
            case "SPRING":
                break;
            case "SUMMER":
                break;
            case "AUTUMN":
                break;
            default:
        }
    }
}
