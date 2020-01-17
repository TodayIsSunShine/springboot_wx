package com.xiangzi.springboot_wx.controller;


import com.xiangzi.springboot_wx.model.AccessToken;
import com.xiangzi.springboot_wx.utils.Ticket;
import com.xiangzi.springboot_wx.utils.WeChatUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * description 单例模式,这样做的目的是防止微信分享一直刷新accesstoken,做全局缓存
 * author:zhangxx
 * Date:2019/12/19
 * Time:17:17
 */
@Slf4j
public class Singleton {

    //缓存accessToken 的Map,map中包含 一个accessToken 和 缓存的时间戳
    private Map<String, String> map = new HashMap<>();

    private Singleton() {

    }

    private static Singleton single = null;

    // 静态工厂方法
    public static Singleton getInstance() {
        if (single == null) {
            single = new Singleton();
        }
        return single;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public static Singleton getSingle() {
        return single;
    }

    public static void setSingle(Singleton single) {
        Singleton.single = single;
    }

    /**
     * 获取 accessToken
     * Jsapi_ticket 已加入缓存机制
     *
     * @param appid
     * @param appsecret
     * @return
     */
    public Map<String, Object> getAccessTokenAndJsapiTicket(String appid, String appsecret) {
        Map<String, Object> result = new HashMap<>();
        Singleton singleton = Singleton.getInstance();
        Map<String, String> map = singleton.getMap();
        String time = map.get("time");//从缓存中拿数据
        log.info("缓存里面的时间:{}", time);
        String accessToken = map.get("access_token");//从缓存中拿数据
        String jsapiticket = map.get("jsapiticket");//从缓存中拿数据
        Long nowDate = new Date().getTime();
        log.info("当前时间戳:{}", nowDate.toString());
        //这里设置过期时间 3000*1000就好了
        if (accessToken != null && time != null && nowDate - Long.parseLong(time) < 3000 * 1000) {
            log.info("-----从缓存读取access_token：{}", accessToken);
            //从缓存中拿数据为返回结果赋值
            result.put("access_token", accessToken);
            result.put("jsapiticket", jsapiticket);
        } else {
            //获取accesstoken
            AccessToken token = WeChatUtil.getAccessToken(appid, appsecret);
            log.info("请求微信接口获取accesstoken:{}", token.getToken());
            Ticket ticket = WeChatUtil.getTicket(token.getToken());
            log.info("根据accesstoken获取jsapiticket:{}", jsapiticket);
            //将信息放置缓存中
            map.put("time", nowDate + "");
            map.put("access_token", token.getToken());
            map.put("jsapiticket", ticket.getTicket());
            //为返回结果赋值
            result.put("access_token", token.getToken());
            result.put("jsapiticket", ticket.getTicket());
        }
        return result;
    }
}
