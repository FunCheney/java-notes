package com.fchen.concurrency.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Classname HelloController
 * @Description TODO
 * @Date 2019/4/26 11:46
 * @Author by Fchen
 */
@Controller
public class HelloController {

    @RequestMapping("/springboot/hello")
    public @ResponseBody   String sayhello(){
        return "hello";
    }
}
