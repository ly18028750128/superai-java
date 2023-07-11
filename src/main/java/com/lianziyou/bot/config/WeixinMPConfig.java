package com.lianziyou.bot.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 微信公众号配置
 */
@Configuration
@ConfigurationProperties(prefix = "wx.mp")
public class WeixinMPConfig {

    @Getter
    private static List<String> listAppIds = new ArrayList<>();
    @Setter
    private List<WxMpDefaultConfigImpl> configs;

    @Bean
    public RedisTemplateWxRedisOps redisTemplateWxRedisOps(StringRedisTemplate redisTemplate) {
        return new RedisTemplateWxRedisOps(redisTemplate);
    }

//    @Bean
//    public WxMpRedisConfigImpl wxMpRedisConfig(RedisTemplateWxRedisOps redisTemplateWxRedisOps) {
//        WxMpRedisConfigImpl wxMpRedisConfig = new WxMpRedisConfigImpl(redisTemplateWxRedisOps, "easyAi");
//        return wxMpRedisConfig;
//    }

    @Bean
    public WxMpService wxMpService(RedisTemplateWxRedisOps redisTemplateWxRedisOps) {
        WxMpServiceImpl wxMpService = new WxMpServiceImpl();
        Map<String, WxMpConfigStorage> configMap = configs.stream().collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, value -> {
            listAppIds.add(value.getAppId());
            WxMpRedisConfigImpl redisConfig = new WxMpRedisConfigImpl(redisTemplateWxRedisOps, value.getAppId());
            BeanUtils.copyProperties(value, redisConfig);
            return redisConfig;
        }));

        wxMpService.setMultiConfigStorages(configMap, configs.get(0).getAppId());

        return wxMpService;
    }


}
