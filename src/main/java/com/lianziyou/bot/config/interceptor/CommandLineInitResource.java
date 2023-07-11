//package com.lianziyou.bot.config.interceptor;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//
//@Slf4j
//@Component
//@Order(1)
//public class CommandLineInitResource implements CommandLineRunner {
//
//    public static List<String> urlList = new ArrayList<>();
//    /**
//     * 上下文
//     */
//    @Autowired
//    WebApplicationContext applicationContext;
//
//    @Override
//    public void run(String... args) {
//        //获取controller相关bean
//        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
//        //获取method
//        Map<RequestMappingInfo, HandlerMethod> methodMap = mapping.getHandlerMethods();
//        //获取methodMap的key集合
//        for (RequestMappingInfo info : methodMap.keySet()) {
//            //controller url集合
//            Set<String> urlSet = info.getPatternsCondition().getPatterns();
//            urlList.addAll(urlSet);
//            //获取所有方法类型
//            //Set<RequestMethod> methodSet = info.getMethodsCondition().getMethods();
//        }
//        urlList = urlList.stream().filter(s -> !s.equals("/error")).distinct().collect(Collectors.toList());
//    }
//}
