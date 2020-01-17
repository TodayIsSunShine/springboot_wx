package com.xiangzi.springboot_wx.exception;

/**
 * description
 * author:zhangxx
 * Date:2019/9/25
 * Time:12:06
 */
public class MyException extends RuntimeException implements MyCustomerError {

    private MyCustomerError myCustomerError;

    public MyException(String message, MyCustomerError myCustomerError) {
        super(message);
        this.myCustomerError = myCustomerError;
    }

    public MyException(MyCustomerError myCustomerError) {
        super();
        this.myCustomerError = myCustomerError;
    }

    public MyException(MyCustomerError myCustomerError, String errMsg) {
        this.myCustomerError = myCustomerError;
        this.myCustomerError.setErrMsg(errMsg);
    }


    @Override
    public Integer getCode() {
        return this.myCustomerError.getCode();
    }

    @Override
    public String getErrMsg() {
        return this.myCustomerError.getErrMsg();
    }

    @Override
    public MyCustomerError setErrMsg(String errMsg) {
        this.myCustomerError.setErrMsg(errMsg);
        return this;
    }

    @Override
    public String param() {
        return this.myCustomerError.param();
    }

    @Override
    public MyCustomerError setParam(String param) {
        this.myCustomerError.setParam(param);
        return this;
    }
}
