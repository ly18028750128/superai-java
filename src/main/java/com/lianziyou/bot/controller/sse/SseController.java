package com.lianziyou.bot.controller.sse;

import static com.lianziyou.bot.constant.SseConst.SSE_GPT_TOPIC_MAP_REDIS_KEY;
import static com.lianziyou.bot.constant.SseConst.SSE_MID_TOPIC_MAP_REDIS_KEY;
import static com.lianziyou.bot.constant.SseConst.SSE_SD_TOPIC_MAP_REDIS_KEY;

import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.constant.SseConst;
import com.lianziyou.bot.server.SseEmitterServer;
import com.lianziyou.bot.utils.sys.JwtUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@Log4j2
public class SseController {

    /**
     * 用于创建连接
     */
    @GetMapping(value = "/connect/{userId}")
    public SseEmitter connect(@PathVariable Long userId) throws IOException {
        String redisToken = RedisUtil.getCacheObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + userId);
        if (!StringUtils.hasLength(redisToken)) {
            return null;
        }
        RedisUtil.setCacheMapValue(SSE_GPT_TOPIC_MAP_REDIS_KEY, JwtUtil.getUserId(), SseConst.getSseGptTopic());
        RedisUtil.setCacheMapValue(SSE_MID_TOPIC_MAP_REDIS_KEY, JwtUtil.getUserId(), SseConst.getSseMidTopic());
        RedisUtil.setCacheMapValue(SSE_SD_TOPIC_MAP_REDIS_KEY, JwtUtil.getUserId(), SseConst.getSseSdTopic());
        return SseEmitterServer.connect(userId);
    }

    /**
     * 关闭连接
     */
    @GetMapping("/close/{userId}")
    public ApiResult<String> close(@PathVariable("userId") Long userId) {
        SseEmitterServer.removeUser(userId);
        return ApiResult.okBuild("连接关闭");
    }
}
