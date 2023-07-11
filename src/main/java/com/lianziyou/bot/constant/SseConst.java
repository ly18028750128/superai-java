package com.lianziyou.bot.constant;

import com.lianziyou.bot.config.ServerConfig;

/**
 * @author Administrator
 */
public final class SseConst {

    public final static String SSE_CLIENTS_REDIS_KEY = "SSE:CLIENTS";

    public final static String SSE_GPT_TOPIC_MAP_REDIS_KEY = "SSE:GPT:CLIENTS:TOPIC";  // 保留sse连接点，分部式部署发送的topic

    public final static String SSE_GPT_TOPIC_PREFIX = "SSE_GPT_TOPIC_%s";
    public final static String SSE_MID_TOPIC_MAP_REDIS_KEY = "SSE:MID:CLIENTS:TOPIC";
    public final static String SSE_MID_TOPIC_PREFIX = "SSE_MID_TOPIC_%s";
    public final static String SSE_SD_TOPIC_MAP_REDIS_KEY = "SSE:SD:CLIENTS:TOPIC";
    public final static String SSE_SD_TOPIC_PREFIX = "SSE_SD_TOPIC_%s";

    private SseConst() {
    }

    public static String getSseGptTopic() {
        return String.format(SSE_GPT_TOPIC_PREFIX, ServerConfig.getIpPort());
    }

    public static String getSseMidTopic() {
        return String.format(SSE_MID_TOPIC_PREFIX, ServerConfig.getIpPort());
    }

    public static String getSseSdTopic() {
        return String.format(SSE_SD_TOPIC_PREFIX, ServerConfig.getIpPort());
    }
}
