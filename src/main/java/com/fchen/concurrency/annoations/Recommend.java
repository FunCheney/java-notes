package com.fchen.concurrency.annoations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname Recommend
 * @Description 用来标记推荐的写法
 * @Date 2019/4/26 11:53
 * @Author by Fchen
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Recommand {

    String value() default "";
}
