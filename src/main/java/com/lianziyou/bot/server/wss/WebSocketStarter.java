package com.lianziyou.bot.server.wss;

import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.utils.sys.RedisUtil;
import com.neovisionaries.ws.client.WebSocketFactory;

public interface WebSocketStarter {

    void start() throws Exception;

    default WebSocketFactory createWebSocketFactory() {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        WebSocketFactory webSocketFactory = new WebSocketFactory().setConnectionTimeout(10000);
        if (cacheObject.getIsOpenProxy() == 1) {
            webSocketFactory.getProxySettings().setHost(cacheObject.getProxyIp()).setPort(cacheObject.getProxyPort());
        }
        return webSocketFactory;
    }
}
