package com.lianziyou.bot.service.wx;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.crypto.SecureUtil;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.constant.WXMessageEnum;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.User;
import com.lianziyou.bot.service.sys.IUserService;
import com.lianziyou.bot.utils.sys.PasswordUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 */
@Component
public class WXMpUtilService {

    @Resource
    IUserService userService;

    public void unsubscribe(WxMpXmlMessage message) {
        this.saveOrUpdate(message, User.builder().isEvent(0).build());
    }

    public void subscribe(WxMpXmlMessage message) {
        this.saveOrUpdate(message, User.builder().isEvent(1).build());
    }

    public String ticket(WxMpXmlMessage message) {
        User user = this.saveOrUpdate(message, User.builder().isEvent(1).build());
        RedisUtil.setCacheObject(CommonConst.REDIS_KEY_MP_TICKET_USER + message.getTicket(), user, 1L, TimeUnit.MINUTES);
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);

        return WXMessageEnum.MENU + "\n--------------\n" + WXMessageEnum.TICKET_VALIDATE_SUCCESS.getContent(sysConfig == null ? "" : sysConfig.getClientUrl());


    }

    public String bind(WxMpXmlMessage message) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        String[] split = message.getContent().split("-");
        if (split.length == 1) {
            return "输入内容格式不正确，请检查，或输入'菜单'查看格式'";
        } else {
            String mobile = split[1];
            if (!Validator.isMobile(mobile)) {
                return "请输入正确的手机号";
            } else {
                User user = userService.getOne(null, mobile);
                if (user != null && !user.getFromUserName().equals(message.getFromUser())) {
                    return WXMessageEnum.MOBILE_HAD_BIND.getContent();
                }
                user = userService.getOne(message.getFromUser(), null);
                if (StringUtils.hasLength(user.getMobile())) {
                    return WXMessageEnum.HAD_BIND.getContent(user.getMobile(), sysConfig == null ? "" : sysConfig.getClientUrl());
                }
                this.saveOrUpdate(message, User.builder().mobile(mobile).build());

                return WXMessageEnum.BIND_SUCCESS.getContent(mobile, sysConfig == null ? "" : sysConfig.getClientUrl());
            }
        }
    }

    public String updatePass(WxMpXmlMessage message) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        String[] split = message.getContent().split("-");
        if (split.length < 2 || !StringUtils.hasLength(split[1])) {
            return "输入内容格式不正确，请检查，或输入'菜单'查看格式'";
        } else {
            String password = split[1];
            if (password.length() > 16 || password.length() < 6) {
                return "请输入6到16位密码或输入'菜单'查看格式'";
            }
            this.saveOrUpdate(message, User.builder().password(SecureUtil.md5(password)).build());
            return WXMessageEnum.NEW_PASSWORD.getContent(password, sysConfig == null ? "" : sysConfig.getClientUrl());
        }
    }

    public String restPass(WxMpXmlMessage message) {
        message.setContent("修改密码-" + PasswordUtil.getRandomPassword());
        return this.updatePass(message);
    }

    public String modifyUserName(WxMpXmlMessage message) {
        SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        String[] split = message.getContent().split("-");
        if (split.length < 2) {
            return WXMessageEnum.MESSAGE_ERROR.content;
        }
        this.saveOrUpdate(message, User.builder().name(split[1]).build());
        return WXMessageEnum.USER_NAME_MODIFY_SUCCESS.getContent(split[1], sysConfig == null ? "" : sysConfig.getClientUrl());
    }

    public String query(WxMpXmlMessage message) {
        User user = userService.getOne(message.getFromUser(), null);
        String respContent;
        if (null == user) {
            respContent = "当微信用户前暂未绑定账号";
        } else {
            respContent = "剩余次数：" + user.getRemainingTimes();
        }
        return respContent;
    }

    public User saveOrUpdate(WxMpXmlMessage message, User update) {
        User user = userService.getOne(message.getFromUser(), null);
        if (user != null) {
            BeanUtil.copyProperties(update, user, CommonConst.DEFAULT_COPY_OPTIONS);
            userService.updateById(user);
        } else {
            String password = SecureUtil.md5(PasswordUtil.getRandomPassword());
            user = User.builder().type(1).name("未命名用户").password(password).fromUserName(message.getFromUser()).build();
            BeanUtil.copyProperties(update, user, CommonConst.DEFAULT_COPY_OPTIONS);
            userService.save(user);
        }
        return user;
    }


}
