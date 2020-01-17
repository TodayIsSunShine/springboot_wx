package com.xiangzi.springboot_wx.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiangzi.springboot_wx.exception.EmMyCustomerBusinessError;
import com.xiangzi.springboot_wx.exception.MyException;
import com.xiangzi.springboot_wx.response.CommonReturnType;
import com.xiangzi.springboot_wx.utils.FastJsonConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/wxChat")
@Slf4j(topic = "textWx")
public class TestWxController {


    // 公众号appid
    private static final String APPID = "appid";
    // 公众号开发者秘钥
    private static final String APPSECRET = "appsecret";

    /**
     * 验证微信服务器
     *
     * @param request
     * @param response
     */
    @RequestMapping("/chat")
    public void weChatService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("------------------进入chat-------------------");
        boolean isGet = request.getMethod().toLowerCase().equals("get");

        if (isGet) {
            // 微信加密签名
            String signature = request.getParameter("signature");
            System.out.println("微信加密签名signature：-----------------------" + signature);
            // 时间戳
            String timestamp = request.getParameter("timestamp");
            System.out.println("时间戳timestamp：-----------------------" + timestamp);
            // 随机数
            String nonce = request.getParameter("nonce");
            System.out.println("随机数nonce：-----------------------" + nonce);
            // 随机字符串
            String echostr = request.getParameter("echostr");
            System.out.println("随机字符串echostr：-----------------------" + echostr);

            PrintWriter out = response.getWriter();
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (com.aspirecn.activity.util.SignUtil.checkSignature("123", signature, timestamp, nonce)) {
                out.print(echostr);
            }
            out.close();
        }
    }

    /**
     * @param data
     * @return com.aspirecn.activity.response.CommonReturnType
     * @description 微信分享
     * @author zhangxx
     * @createDate: 2019/12/19 17:01
     */
    @PostMapping("/getWxData")
    @ResponseBody
    public CommonReturnType getWxData(@RequestBody String data) {
        JSONObject jsonObject = FastJsonConvert.convertJSONToObject(data, JSONObject.class);
        String url = jsonObject.getString("url");
        log.info("微信分享传递的url:{}", url);
        if (StringUtils.isEmpty(url)) {
            throw new MyException(EmMyCustomerBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        Map<String, Object> tokenAndJsapiTicket = Singleton.getInstance().getAccessTokenAndJsapiTicket(APPID, APPSECRET);
        String ticketTicket = tokenAndJsapiTicket.get("jsapiticket").toString();
        //签名验证
        Map<String, String> stringMap = sign(ticketTicket, url);
        stringMap.put("appId", APPID);
        log.info("微信分享返回给前端的参数:{}", stringMap.toString());

        return CommonReturnType.create(stringMap);
    }

    /**
     * @param jsapi_ticket
     * @param url
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @description 微信签名验证
     * @author zhangxx
     * @createDate: 2019/12/19 17:33
     */
    public static Map<String, String> sign(String jsapi_ticket, String url) {
        Map<String, String> ret = new HashMap<>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                "&noncestr=" + nonce_str +
                "&timestamp=" + timestamp +
                "&url=" + url;
        log.info(string1);

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
