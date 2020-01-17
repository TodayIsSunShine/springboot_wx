package com.xiangzi.springboot_wx.exception;

/**
 * description
 * author:zhangxx
 * Date:2019/9/25
 * Time:12:02
 */
public enum EmMyCustomerBusinessError implements MyCustomerError {

    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),
    NOT_LOGGED_IN(10003, "您暂未登录,请先登录"),

    //咪咕佳能电视提示
    LIMIT_TWO_BY_MSISDN(20001, "您的购买份数已达上限，无法继续购买。"),
    INTERNAL_PURCHASE(20002, "很抱歉，本次活动目前仅面向咪咕自有员工，后期将会逐步开放购买，敬请期待！"),
    NOT_EXIST_OF_ORDER(20003, "订单不存在"),
    NOT_CAN_CANCEL_ORDER(20004, "抱歉! 仅为已预约的订单才可取消"),
    OUTBOUND_FAILURE(20005, "出库失败"),
    ORDER_RECEIPT_FAILURE(20006, "订单签收失败"),

    PLEASE_LIGHT_UP_FIRST(40001, "奖品点亮时才可以打开哦"),
    CHINA_MOBILE_NUMBER_ONLY(40002, "该活动仅限中国移动用户参加"),

    //购物车
    ITEM_ADD_CART_FAILURE(50001, "商品添加购物车失败"),
    OPERATING_FAILURE(50002, "查询商品信息失败"),
    NOT_CAN_REDUCE_NUMBER(50003, "宝贝不能在减少了哦"),
    IMAGE_NOT_EXIST(50004, "图片不存在"),
    PARAMS_MISSING(50005, "参数缺失"),
    PRODUCT_RULES_REMOVED(50006, "当前商品规格不存在"),

    //人人推
    NOT_WHITE_USER(30001, "非白名单用户不可登录"),
    MSISDN_ERROR(30002, "请输入正确的手机号码"),
    PASSWORD_ERROR(30003, "手机号或密码输入错误"),
    USER_NOT_EXIST(30004, "用户不存在"),
    ACTIVITY_NOT_AUTHORITY(30005, "对不起，该活动你暂无权限！"),
    NICKNAME_TOO_LENGTH(30006, "昵称不能超过10位哦~"),
    NICKNAME_ALREADY_USED(30007, "这个昵称已经使用过了,请换一个吧~"),
    ORIGINAL_PASSWORD_ERROR(30008, "原密码输入错误"),
    NEWPASSWORD_LENGTH_TOO_SHORT(30009, "密码不能少于6位哦！"),
    PLEASE_INPUT_ORIGINAL_PASSWORD(30010, "请输入原始密码！"),
    CAN_NOT_AGREE(30011, "新密码不能和原始密码一致！"),
    USERNAME_ILLEGAL(30012, "昵称不合法!"),

    //5G
    OUT_OF_STOCK(40001, "对不起，奖品已经发完啦~"),
    ALREADY_CLAIMED_THE_PRIZE(40002, "对不起，你已经领过了！"),


    STORAGE_ERROR(5001, "入库失败"),
    SYSTEM_ERROR(5002, "系统繁忙，请稍后再试！");


    private Integer code;
    private String errMsg;
    private String param;

    EmMyCustomerBusinessError(Integer code, String errMsg) {
        this.code = code;
        this.errMsg = errMsg;
    }

    EmMyCustomerBusinessError(Integer code, String errMsg, String param) {
        this.code = code;
        this.errMsg = errMsg;
        this.param = param;
    }


    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public MyCustomerError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }

    @Override
    public String param() {
        return this.param;
    }

    @Override
    public MyCustomerError setParam(String param) {
        this.param = param;
        return this;
    }
}
