package com.fchen.concurrency.example.threadlocal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Classname ThreadLocalController
 * @Description ThreadLocal测试类
 * @Date 2019/5/9 19:34
 * @Author by Fchen
 */
@Controller
@RequestMapping("/threadLocal")
public class ThreadLocalController {
    @RequestMapping("/test")
    @ResponseBody
    public Long test(){
        return RequestHolder.getId();
    }
}
