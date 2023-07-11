package com.lianziyou.bot.server;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Log4j2
public class SseEmitterServer {

    /**
     * 当前连接数
     */
    private static final AtomicInteger COUNT = new AtomicInteger(0);

    /**
     * 发送心跳线程池
     */

    /**
     * 使用map对象，便于根据userId来获取对应的SseEmitter，或者放redis里面
     */
    private static final Map<Long, SseEmitter> SSE_EMITTER_MAP = new ConcurrentHashMap<>();

    /**
     * 创建用户连接并返回 SseEmitter
     *
     * @param userId 用户ID
     * @return SseEmitter
     */
    public static SseEmitter connect(Long userId) {
        // 设置超时时间，0表示不过期。默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 注册回调
        sseEmitter.onCompletion(completionCallBack(userId));
        sseEmitter.onError(errorCallBack(userId));
        sseEmitter.onTimeout(timeoutCallBack(userId));
        SSE_EMITTER_MAP.put(userId, sseEmitter);
        // 数量+1
        COUNT.getAndIncrement();
        log.info("创建新的sse连接，当前用户Id：{}", userId);
        return sseEmitter;
    }

    /**
     * 给指定用户发送信息
     */
    public static void sendMessage(Long userId, Object message) {
        if (SSE_EMITTER_MAP.containsKey(userId)) {
            try {
                // sseEmitterMap.get(userId).send(message, MediaType.APPLICATION_JSON);
                SSE_EMITTER_MAP.get(userId).send(message);
            } catch (IOException e) {
                log.error("用户[{}]推送异常:{}", userId, e.getMessage());
                removeUser(userId);
            }
        }
    }


    /**
     * 群发所有人
     */
    public static void batchSendMessage(String wsInfo) {
        SSE_EMITTER_MAP.forEach((k, v) -> {
            try {
                v.send(wsInfo, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                log.error("用户[{}]推送异常:{}", k, e.getMessage());
                removeUser(k);
            }
        });
    }

    /**
     * 移除用户连接
     */
    public static void removeUser(Long userId) {
        SSE_EMITTER_MAP.remove(userId);
        // 数量-1
        COUNT.getAndDecrement();
        log.info("移除用户：{}", userId);
    }

    /**
     * 获取当前连接信息
     */
    public static List<Long> getIds() {
        return new ArrayList<>(SSE_EMITTER_MAP.keySet());
    }

    /**
     * 获取当前连接数量
     */
    public static int getUserCount() {
        return COUNT.intValue();
    }

    private static Runnable completionCallBack(Long userId) {
        return () -> {
            log.info("结束连接：{}", userId);
            removeUser(userId);
        };
    }

    private static Runnable timeoutCallBack(Long userId) {
        return () -> {
            log.info("连接超时：{}", userId);
            removeUser(userId);
        };
    }

    private static Consumer<Throwable> errorCallBack(Long userId) {
        return throwable -> {
            log.info("连接异常：{}", userId);
            removeUser(userId);
        };
    }

}
