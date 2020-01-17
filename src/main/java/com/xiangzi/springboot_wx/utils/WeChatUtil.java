package com.xiangzi.springboot_wx.utils;


import com.xiangzi.springboot_wx.model.AccessToken;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * description
 * author:zhangxx
 * Date:2019/12/19
 * Time:15:31
 */
@Slf4j(topic = "微信请求")
public class WeChatUtil {

    // 获取access_token的接口地址(GET) 限200次/天
    public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    // 获取jsapi_ticket的接口地址(GET)
    public static String jsapi_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";

    /**
     * 获取access_token (7200秒有效期)
     *
     * @param appid     凭证
     * @param appsecret 密钥
     * @return
     */
    public static AccessToken getAccessToken(String appid, String appsecret) {
        AccessToken accessToken = null;

        String requestUrl = access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
        log.info("获取access_token请求的url地址:{}", requestUrl);
        String returnData = null;
        try {
            returnData = OkHttpHelper.getInstance().get(requestUrl);
            log.info("获取access_token微信返回的数据:{}", returnData);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (OkHttpHelper.NotSupportedException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = FastJsonConvert.convertJSONToObject(returnData, JSONObject.class);
        // 如果请求成功
        if (null != jsonObject) {
            log.info("获取access_token:{},expires_in:{}", jsonObject.getString("access_token"), jsonObject.getInt("expires_in"));
            try {
                accessToken = new AccessToken();
                accessToken.setToken(jsonObject.getString("access_token"));
                accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
            } catch (Exception e) {
                accessToken = null;
                // 获取token失败
                log.info("获取ticket失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
            }
        }
        return accessToken;
    }


    /**
     * 获取jsapi_ticket（jsapi_ticket的有效期为7200秒）
     *
     * @param token
     * @return
     */
    public static Ticket getTicket(String token) {
        Ticket ticket = null;
        String requestUrl = jsapi_ticket_url.replace("ACCESS_TOKEN", token);
        JSONObject jsonObject = null;
        try {
            jsonObject = FastJsonConvert.convertJSONToObject(OkHttpHelper.getInstance().get(requestUrl), JSONObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OkHttpHelper.NotSupportedException e) {
            e.printStackTrace();
        }
        // 如果请求成功
        if (null != jsonObject) {
            log.info("ticket:{},expires_in:{}", jsonObject.getString("ticket"), jsonObject.getInt("expires_in"));
            try {
                ticket = new Ticket();
                ticket.setTicket(jsonObject.getString("ticket"));
                ticket.setExpiresIn(jsonObject.getInt("expires_in"));
            } catch (Exception e) {
                ticket = null;
                // 获取token失败
                log.info("获取ticket失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
            }
        }
        return ticket;
    }
}
