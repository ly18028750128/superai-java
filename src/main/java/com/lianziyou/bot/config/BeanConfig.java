package com.lianziyou.bot.config;

import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.server.wss.WebSocketStarter;
import com.lianziyou.bot.server.wss.bot.BotMessageListener;
import com.lianziyou.bot.server.wss.handle.MessageHandler;
import com.lianziyou.bot.server.wss.user.UserMessageListener;
import com.lianziyou.bot.server.wss.user.UserWebSocketStarter;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

;

@Configuration
public class BeanConfig implements BeanFactoryPostProcessor {


    @Getter
    private static ConfigurableListableBeanFactory configurableListableBeanFactory;

    public static RestTemplate getRestTemplate() {
        RestTemplate restTemplate = configurableListableBeanFactory.getBean(RestTemplate.class);

        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if (cacheObject.getIsOpenProxy() == 1) {
            SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
            simpleClientHttpRequestFactory.setProxy(
                new Proxy(Proxy.Type.HTTP, new InetSocketAddress(cacheObject.getProxyIp(), cacheObject.getProxyPort()))); // 添加代理 ip 和 port 即可
            restTemplate.setRequestFactory(simpleClientHttpRequestFactory);
        }
        return restTemplate;
    }

    @Bean
    WebSocketStarter webSocketStarter() {
        return new UserWebSocketStarter();
    }

    @Bean
    UserMessageListener userMessageListener(ObjectProvider<List<MessageHandler>> objectProvider) {
        return new UserMessageListener(objectProvider);
    }

    @Bean
    BotMessageListener botMessageListener() {
        return new BotMessageListener();
    }

    @Bean
    @Lazy
    ApplicationRunner enableMetaChangeReceiverInitializer(WebSocketStarter webSocketStarter) {
        return args -> webSocketStarter.start();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        BeanConfig.configurableListableBeanFactory = configurableListableBeanFactory;
    }
}
