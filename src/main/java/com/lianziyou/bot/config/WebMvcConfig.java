package com.lianziyou.bot.config;

import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Administrator
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("redirect:doc.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        final CacheControl oneYear = CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic();
        registry.addResourceHandler("/files/**").addResourceLocations("file:/gpt/file/").setCacheControl(oneYear);
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//            .allowedOriginPatterns("*") //浏览器允许所有的域访问 / 注意 * 不能满足带有cookie的访问,Origin 必须是全匹配
//            .allowedMethods("*")
//            .allowCredentials(true)  // 允许带cookie访问
//            .allowedHeaders("*")
////            .allowedOriginPatterns()
//            .maxAge(3600);
//    }
}
