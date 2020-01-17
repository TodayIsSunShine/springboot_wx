package com.xiangzi.springboot_wx.model;

import lombok.Getter;
import lombok.Setter;

/**
 * description
 * author:zhangxx
 * Date:2019/12/19
 * Time:15:34
 */
@Getter
@Setter
public class AccessToken {

    private String token;
    private int expiresIn;
}
