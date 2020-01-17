package com.xiangzi.springboot_wx.utils;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ticket {
    private String ticket;//获取到的凭证
    private int expiresIn;//凭证有效时间,单位：秒

}
