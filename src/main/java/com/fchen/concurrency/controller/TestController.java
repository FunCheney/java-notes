package com.fchen.concurrency.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Classname TestController
 * @Description 测试用Controller
 * @Date 2019/4/28 9:49
 * @Author by Fchen
 */
@Controller
@Slf4j
public class TestController {
    @RequestMapping("/test")
    public @ResponseBody String test(){
        log.info("testController into");
        return "test";
    }
}
