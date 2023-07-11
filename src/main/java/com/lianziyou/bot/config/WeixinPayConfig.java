package com.lianziyou.bot.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信公众号配置
 *
 * @author Administrator
 */
@Configuration
@ConfigurationProperties(prefix = "wx.pay")
public class WeixinPayConfig {

    @Getter
    private static String payPage;
    @Getter
    private static List<String> listMchIds = new ArrayList<>();
    @Setter
    private String callBack;

    @Setter
    private List<WxPayConfig> configs;

    public void setPayPage(String payPage) {
        WeixinPayConfig.payPage = payPage;
    }

    @Bean
    public WxPayService wxPayService() {
        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        Map<String, WxPayConfig> configMap = configs.stream().collect(Collectors.toMap(WxPayConfig::getMchId, value -> {
            listMchIds.add(value.getMchId());
            value.setNotifyUrl(callBack + value.getMchId());
            return value;
        }));

        wxPayService.setMultiConfig(configMap, configs.get(0).getMchId());
        return wxPayService;
    }


}
