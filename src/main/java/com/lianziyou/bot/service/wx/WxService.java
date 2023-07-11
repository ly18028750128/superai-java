package com.lianziyou.bot.service.wx;

import javax.servlet.http.HttpServletRequest;


public interface WxService {

    String callbackEvent(HttpServletRequest request) throws Exception;
}
