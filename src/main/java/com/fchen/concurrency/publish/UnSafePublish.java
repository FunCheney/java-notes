package com.fchen.concurrency.publish;

import com.fchen.concurrency.annoations.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @Classname UnSafePublish
 * @Description TODO
 * @Date 2019/5/6 19:52
 * @Author by Fchen
 */
@Slf4j
@NotThreadSafe
public class UnSafePublish {
    /**
     * 发布对象：使一个对象能够被当前范围之外的代码所使用
     *
     * 对象溢出：一种错误的发布。当一个对象还没有构造完成时，就使它被其他线程所见
     */
    /**
     * 私有域
     */
    private String[] states = {"a","b","c"};

    /**
     * 由于使用public修饰，使得类的外部线程都可以访问这个域
     * @return
     */
    public String[] getStates(){
        return states;
    }

    public static void main(String[] args) {
        // 通过new 创建实例
        UnSafePublish unSafePublish = new UnSafePublish();
        //通过类里面的public方法 得到类中私有域的引用
        log.info("{}", Arrays.toString(unSafePublish.getStates()));
        //通过这种方式，就可以在其他线程里面修改这些私有域的值，当其他线程使用到时，就不安全
        unSafePublish.getStates()[0] = "d";
        log.info("{}", Arrays.toString(unSafePublish.getStates()));
    }
}
