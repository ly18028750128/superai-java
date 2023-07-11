package com.lianziyou.bot.config.interceptor;

import java.util.ArrayList;
import java.util.List;


public class WhiteApiList {

    public static List<String> list = new ArrayList<>();

    static {
        //授权
        list.add("/auth/temp");
        list.add("/auth/login");

        list.add("/auth/admin/login");
        //注册
        list.add("/auth/register");
        list.add("/auth/register/email");
        //发送消息
        list.add("/send/email");
        //易支付回调
        list.add("/order/yi/callback");
        //sse
        list.add("/sse/**");
        //获取注册方式
        list.add("/client/register/method");
        //mj回调
        list.add("/mj/callBack");
        //wx
        list.add("/wx/callBack");
        //获取客户端配置
        list.add("/client/client/conf");

        list.add("/files/**");

        list.add("/wx/getTicket");

        list.add("/auth/mp/login");

        list.add("/v2/api-docs/**");


        list.add("/wx/pay/page/");
        list.add("/doc.html");
        list.add("/webjars/**");
        list.add("/swagger-resources/**");
        list.add("/v2/controller-docs/**");

        list.add("/wx/pay/callBack/**");
        list.add("/favicon.ico");
    }
}
