package com.lianziyou.bot.controller.sys;

import com.lianziyou.bot.annotate.AvoidRepeatRequest;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.User;
import com.lianziyou.bot.model.req.sys.SendEmailReq;
import com.lianziyou.bot.service.sys.IUserService;
import com.lianziyou.bot.service.sys.SendMessageService;
import com.lianziyou.bot.utils.sys.RedisUtil;
import javax.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send")
@Log4j2
@Transactional(rollbackFor = BussinessException.class)
public class SendController {

    @Resource
    IUserService userService;

    @Resource
    SendMessageService sendMessageService;

    @RequestMapping(value = "/email", name = "发送邮件验证码")
    @AvoidRepeatRequest(msg = "请求频繁请稍后再试")
    public ApiResult<Void> sendMessage(@Validated @RequestBody SendEmailReq req) {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        if (cacheObject.getRegistrationMethod() != 2) {
            throw new BussinessException("发送邮件功能已关闭");
        }
        Long count = userService.lambdaQuery().eq(User::getEmail, req.getEmail()).or().eq(User::getMobile, req.getMobile()).count();
        if (count > 0) {
            throw new BussinessException("邮箱或手机号已存在，请更换");
        }
        sendMessageService.sendEmail(req.getEmail());
        return ApiResult.okBuild();
    }
}
