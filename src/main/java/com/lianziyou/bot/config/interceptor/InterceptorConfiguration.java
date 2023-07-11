package com.lianziyou.bot.config.interceptor;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截的管理器
        //拦截的对象会进入这个类中进行判断
        InterceptorRegistration registration = registry.addInterceptor(new AuthorizationInterceptor());
        //所有路径都被拦截
        registration.addPathPatterns("/**");
        registration.excludePathPatterns(WhiteApiList.list);

    }
}
