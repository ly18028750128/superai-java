package com.lianziyou.bot.controller.sys;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.lianziyou.bot.annotate.AvoidRepeatRequest;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.enums.sys.ResultEnum;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.User;
import com.lianziyou.bot.model.req.sys.EmailRegisterReq;
import com.lianziyou.bot.model.req.sys.TempUserAuthReq;
import com.lianziyou.bot.model.res.sys.RegisterReq;
import com.lianziyou.bot.model.res.sys.UserAuthRes;
import com.lianziyou.bot.model.res.sys.UserLoginReq;
import com.lianziyou.bot.service.sys.IUserService;
import com.lianziyou.bot.utils.sys.JwtUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Log4j2
@Transactional(rollbackFor = BussinessException.class)
public class AuthController {

    private static final String BOT_NAME = "SuperAI";
    @Resource
    IUserService userService;

    public static UserAuthRes createLoginResult(User user) {
        UserAuthRes res = new UserAuthRes();
        //生成token
        String token = JwtUtil.createToken(user);
        res.setToken(token);
        res.setUserId(user.getId());
        res.setName(user.getName());
        res.setUserType(user.getType());
        res.setUserType(user.getType());
        res.setAvatar(user.getAvatar());
        res.setExpirationTime(user.getRemainingTimes());
        res.setFromUserName(user.getFromUserName());
        return res;
    }

    @RequestMapping(value = "/temp", name = "临时用户授权")
    @AvoidRepeatRequest(msg = "授权过于频繁")
    public ApiResult<UserAuthRes> createImage(HttpServletRequest request, @Validated @RequestBody TempUserAuthReq req) {
        if (!SecureUtil.md5(req.getBrowserFingerprint() + BOT_NAME).equals(req.getAuthToken())) {
            throw new BussinessException("鉴权失败");
        }
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        String ipAddr = ServletUtil.getClientIP(request);
        if (ipAddr.contains("127.0.0.1")) {
            throw new BussinessException("ip获取异常，请登录使用本系统");
        }
        Long count = userService.lambdaQuery().eq(User::getIpAddress, ipAddr).count();
        if (count > 1) {
            throw new BussinessException("终端数已达到上限");
        }
        return ApiResult.okBuild(saveUser(request, req, sysConfig, true));
    }

    @RequestMapping(value = "/login", name = "用户登录")
    @AvoidRepeatRequest(msg = "请勿短时间连续登录")
    public ApiResult<UserAuthRes> userLogin(HttpServletRequest request, @Validated @RequestBody UserLoginReq userLogin) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        List<User> list = userService.lambdaQuery()
            .eq(User::getMobile, userLogin.getMobile())
            .eq(User::getPassword, SecureUtil.md5(userLogin.getPassword()))
            .eq(User::getType, 1)
            .list();
        if (list == null || list.size() == 0) {
            throw new BussinessException("用户名或密码错误");
        }
        User user = list.get(0);
        if (sysConfig.getRegistrationMethod() == 3 && (null == user.getFromUserName() || user.getIsEvent() == 0)) {
            throw new BussinessException("请关注公众号'链自由科技'进行绑定账号'");
        }
        return getUserAuthResB(request, sysConfig, user);
    }

    private ApiResult<UserAuthRes> getUserAuthResB(HttpServletRequest request, SysConfig sysConfig, User user) {
        User nweUser = new User();
        nweUser.setId(user.getId());
        nweUser.setLastLoginTime(LocalDateTime.now());
        nweUser.setIpAddress(ServletUtil.getClientIP(request));
        userService.updateById(nweUser);
        if (StringUtils.hasLength(user.getAvatar()) && !user.getAvatar().contains("http")) {
            user.setAvatar(sysConfig.getImgReturnUrl() + user.getAvatar());
        }
        return ApiResult.okBuild(createLoginResult(user));
    }

    @GetMapping(value = "/mp/login", name = "微信扫码登录")
    public ApiResult<UserAuthRes> userMPLogin(HttpServletRequest request, @RequestParam("ticket") String ticket) {
        User user = RedisUtil.getCacheObject(CommonConst.REDIS_KEY_MP_TICKET_USER + ticket);
        if (user == null) {
            return ApiResult.build(ResultEnum.SUCCESS.getCode(), "二维码未扫描或者已经过期！");
        }
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        RedisUtil.deleteObject(CommonConst.REDIS_KEY_MP_TICKET_USER + ticket);
        return this.getUserAuthResB(request, sysConfig, user);
    }

    @RequestMapping(value = "/admin/login", name = "管理员登录")
    @AvoidRepeatRequest(msg = "请勿短时间连续登录")
    public ApiResult<UserAuthRes> adminLogin(@Validated @RequestBody UserLoginReq userLogin) {
        List<User> list = userService.lambdaQuery()
            .eq(User::getMobile, userLogin.getMobile())
            .eq(User::getPassword, SecureUtil.md5(userLogin.getPassword()))
            .eq(User::getType, -1)
            .list();
        if (list == null || list.size() == 0) {
            return ApiResult.finalBuild("用户名或密码错误");
        }
        User user = list.get(0);
        User nweUser = new User();
        nweUser.setId(user.getId());
        nweUser.setLastLoginTime(LocalDateTime.now());
        userService.updateById(nweUser);
        return ApiResult.okBuild(createLoginResult(user));
    }

    @RequestMapping(value = "/register", name = "账号密码注册")
    @AvoidRepeatRequest(msg = "请勿短时间内重复注册")
    public ApiResult<Void> register(HttpServletRequest request, @Validated @RequestBody RegisterReq req) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if (sysConfig.getRegistrationMethod() != 1) {
            throw new BussinessException("暂未开放账号密码注册");
        }
        saveUser(request, req, sysConfig, false);
        return ApiResult.okBuild();
    }

    @RequestMapping(value = "/register/email", name = "邮件验证码注册")
    @AvoidRepeatRequest(msg = "请勿短时间内重复注册")
    public ApiResult<Void> registerEmail(HttpServletRequest request, @Validated @RequestBody EmailRegisterReq req) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if (sysConfig.getRegistrationMethod() != 2) {
            throw new BussinessException("暂未开放账邮箱注册");
        }
        String code = RedisUtil.getCacheObject(CommonConst.SEND_EMAIL_CODE + req.getEmail());
        if (StringUtils.isEmpty(code) || !code.equals(req.getEmailCode())) {
            throw new BussinessException("验证码错误");
        } else {
            RedisUtil.deleteObject(CommonConst.SEND_EMAIL_CODE + req.getEmail());
        }
        saveUser(request, req, sysConfig, false);
        return ApiResult.okBuild();
    }

    public UserAuthRes saveUser(HttpServletRequest request, Object req, SysConfig sysConfig, boolean isTemp) {
        User reqUser = BeanUtil.copyProperties(req, User.class);
        reqUser.setIpAddress(ServletUtil.getClientIP(request));
        //查询是否有符合的临时用户
        User user = userService.checkTempUser(reqUser.getBrowserFingerprint(), reqUser.getIpAddress());
        if (null == user) {
            user = reqUser;
            user.setRemainingTimes(sysConfig.getDefaultTimes());
            user.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
            //判断是否临时用户请求
            if (isTemp) {
                user.setType(2);
                user.setName("用户" + System.currentTimeMillis());
            } else {
                Long count = this.userService.lambdaQuery()
                    .eq(null != reqUser.getName(), User::getName, reqUser.getName())
                    .eq(null != reqUser.getMobile(), User::getMobile, reqUser.getMobile())
                    .ne(User::getType, 2)
                    .count();
                if (count > 0) {
                    throw new BussinessException("用户已存在");
                }
                user.setType(1);
            }
        } else {
            //判断临时用户是否有剩余次数
            if (isTemp) {
                if (user.getRemainingTimes() < 1) {
                    throw new BussinessException("剩余次数为0,请注册后充值使用");
                }
            } else {
                user.setType(1);
                user.setBrowserFingerprint(reqUser.getBrowserFingerprint());
                user.setIpAddress(reqUser.getIpAddress());
                user.setName(reqUser.getName());
                user.setMobile(reqUser.getMobile());
                if (null != reqUser.getEmail()) {
                    user.setEmail(reqUser.getEmail());
                }

            }
        }
        if (null != reqUser.getPassword()) {
            user.setPassword(SecureUtil.md5(reqUser.getPassword()));
        } else {
            user.setPassword(SecureUtil.md5("123456"));
        }
        userService.saveOrUpdate(user);
        return createLoginResult(user);
    }

}
