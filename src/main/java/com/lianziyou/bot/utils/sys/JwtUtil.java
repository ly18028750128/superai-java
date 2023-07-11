package com.lianziyou.bot.utils.sys;

import static com.lianziyou.bot.constant.CommonConst.REDIS_KEY_PREFIX_TOKEN;
import static com.lianziyou.bot.constant.CommonConst.TOKEN_EXPIRE_TIME;

import cn.hutool.crypto.SecureUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lianziyou.bot.model.User;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Slf4j
public class JwtUtil {

    private static final String ISSUSER = "SYSTEM";

    private static final String SUBJECT = "BEAN";

    private static final String AUDIENCE = "SSP";

    private static final String TOKEN_KEY = "RdecmaE4As3HhQ51vz6OZgtkMW7FfCb0";


    /**
     * 创建Token
     *
     * @return
     */
    public static String createToken(User user) {
        return getTokenString(user);
    }

    /**
     * 校验Token有效性
     *
     * @return
     */
    public static boolean verifierToken() {
        String token = getRequestToken();
        if (Strings.isBlank(token)) {
            return false;
        }
        JWTVerifier verifier = getJWTVerifier();
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            log.error(exception.getMessage());
            return false;
        }
    }

    /**
     * 获取Token剩余有效时间（毫秒）
     *
     * @return
     */
    public static long getRemainingTime() {
        String token = getRequestToken();
        if (Strings.isBlank(token)) {
            return 0L;
        }
        JWTVerifier verifier = getJWTVerifier();
        try {
            /** 校验Token有效性 **/
            DecodedJWT decoded = verifier.verify(token);
            /** 获取当前时间 **/
            Date date = new Date();
            /** 获取过期时间 **/
            Date expireTime = decoded.getExpiresAt();
            long remainingTime = expireTime.getTime() - date.getTime();
            if (remainingTime < 0) {
                return 0L;
            } else {
                return remainingTime;
            }
        } catch (JWTVerificationException exception) {
            log.error(exception.getMessage());
            return 0L;
        }
    }


    /**
     * 根据Token获取用户编号
     *
     * @return
     */
    public static Long getUserId() {
        JWTVerifier verifier = getJWTVerifier();
        String token = getRequestToken();
        if (Strings.isBlank(token)) {
            return -1L;
        }
        try {
            /** 校验Token有效性 **/
            DecodedJWT decoded = verifier.verify(token);
            /** 获取用户编号 **/
            return decoded.getClaim("userId").asLong();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return -1L;
        }
    }

    public static Integer getType() {
        JWTVerifier verifier = getJWTVerifier();
        String token = getRequestToken();
        if (Strings.isBlank(token)) {
            return 0;
        }
        try {
            /** 校验Token有效性 **/
            DecodedJWT decoded = verifier.verify(token);
            /** 用户类型 **/
            return decoded.getClaim("type").asInt();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return 0;
        }
    }


    public static String getTokenString(User user) {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_KEY);
        Date date = new Date();
        String token = JWT.create().withIssuer(ISSUSER).withSubject(SUBJECT).withAudience(AUDIENCE).withIssuedAt(date)
            .withExpiresAt(new Date(System.currentTimeMillis() + (24 * 3600 * 1000L) * TOKEN_EXPIRE_TIME)).withClaim("userId", user.getId())
            .withClaim("name", user.getName()).withClaim("type", user.getType()).sign(algorithm);
        RedisUtil.setCacheObject(REDIS_KEY_PREFIX_TOKEN + user.getId(), SecureUtil.md5(token), TOKEN_EXPIRE_TIME, TimeUnit.DAYS);
        return token;
    }


    private static JWTVerifier getJWTVerifier() {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_KEY);
        return JWT.require(algorithm).withIssuer(ISSUSER).withAudience(AUDIENCE).build();
    }

    public static String getRequestToken() {
        if (null == RequestContextHolder.getRequestAttributes()) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if (Strings.isBlank(token)) {
            token = request.getParameter("token");
        }
        if (Strings.isBlank(token)) {
            return null;
        }
        return token;
    }
}
