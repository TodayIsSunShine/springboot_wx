package com.xiangzi.springboot_wx.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {


    @RequestMapping("/getHello")
    public String testHello() {
        return "hello test！";
    }
}
