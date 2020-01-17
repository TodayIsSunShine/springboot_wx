package com.xiangzi.springboot_wx.response;

import lombok.Getter;
import lombok.Setter;

/**
 * description 统一返回格式
 * Date:2019/5/7
 * Time:16:47
 */
@Getter
@Setter
public class CommonReturnType {

    private Integer code;
    private String status;
    private Object data;
    private String errMsg;

    /**
     * @param result
     * @return
     * @description success 返回成功,fail返回失败
     */
    public static CommonReturnType create(Object result) {
        return CommonReturnType.create(result, 0, "success");
    }

    public static CommonReturnType create(Object result, int code, String status) {
        CommonReturnType type = new CommonReturnType();
        type.setData(result);
        type.setCode(code);
        type.setStatus(status);
        return type;
    }

}
