package com.fchen.jdk.runTimeArea.stack.java;

/**
 * 解析调用中非虚方法、虚方法的测试
 *
 * invokestatic指令和invokespecial指令调用的方法称为非虚方法
 */
class Father {
    public Father() {
        System.out.println("father的构造器");
    }

    public static void showStatic(String str) {
        System.out.println("father " + str);
    }

    public final void showFinal() {
        System.out.println("father show final");
    }

    public void showCommon() {
        System.out.println("father 普通方法");
    }
}

public class Son extends Father {
    public Son() {
        //invokespecial
        super();
    }
    public Son(int age) {
        //invokespecial
        this();
    }
    //不是重写的父类的静态方法，因为静态方法不能被重写！
    public static void showStatic(String str) {
        System.out.println("son " + str);
    }
    private void showPrivate(String str) {
        System.out.println("son private" + str);
    }

    public void show() {
        //invokestatic 非虚方法
        showStatic("atguigu.com");
        //invokestatic 非虚方法
        super.showStatic("good!");
        //invokespecial 非虚方法
        showPrivate("hello!");
        //invokespecial 非虚方法 显示调用父类方法
        super.showCommon();

        //invokevirtual
        //因为此方法声明有final，不能被子类重写，所以也认为此方法是非虚方法。
        showFinal();
        //虚方法如下：
        //invokevirtual
        showCommon();
        // info 方法 public 且非 final，编译期间不确定是否有子类，运行期间确定
        info();

        MethodInterface in = null;
        //invokeinterface 虚方法
        in.methodA();
    }

    public void info(){

    }

    public void display(Father f){
        f.showCommon();
    }

    public static void main(String[] args) {
        Son so = new Son();
        so.show();
    }
}

interface MethodInterface{
    void methodA();
}
