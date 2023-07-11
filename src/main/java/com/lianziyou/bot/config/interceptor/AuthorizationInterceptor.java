package com.lianziyou.bot.config.interceptor;


import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.enums.sys.ResultEnum;
import com.lianziyou.bot.utils.sys.JwtUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object handler) throws IOException {
        //判断token是否存在
        String servletPath = request.getServletPath();
        log.info("请求接口地址：{}", servletPath);
        if (JwtUtil.getType() != -1 && servletPath.contains("sys")) {
            returnResult(httpServletResponse, ResultEnum.ERROR.getCode(), "暂无访问权限");
            return false;
        }
        Long userId = JwtUtil.getUserId();
        if (userId == null || userId < 0) {
            returnResult(httpServletResponse, ResultEnum.ERROR.getCode(), "身份校验失败，请重新登录");
            return false;
        }

        String redisToken = RedisUtil.getCacheObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + userId);
        String handlerToken = JwtUtil.getRequestToken();
        if (!StringUtils.hasLength(redisToken) || !redisToken.equals(SecureUtil.md5(handlerToken))) {
            returnResult(httpServletResponse, ResultEnum.ERROR.getCode(), "身份校验失败，请重新登录");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

    private void returnResult(HttpServletResponse response, Integer code, String msg) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(JSONObject.toJSONString(ApiResult.build(code, msg), SerializerFeature.WriteMapNullValue));
        writer.flush();
        writer.close();
    }
}
