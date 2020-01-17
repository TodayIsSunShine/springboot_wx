package com.xiangzi.springboot_wx.exception;

/**
 * description
 * author:zhangxx
 * Date:2019/9/25
 * Time:12:00
 */
public interface MyCustomerError {

    public Integer getCode();

    public String getErrMsg();

    public MyCustomerError setErrMsg(String errMsg);

    public String param();

    public MyCustomerError setParam(String param);
}
