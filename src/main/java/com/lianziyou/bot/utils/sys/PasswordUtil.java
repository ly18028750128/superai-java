package com.lianziyou.bot.utils.sys;


import cn.hutool.core.util.RandomUtil;
import com.lianziyou.bot.constant.CommonConst;

public class PasswordUtil {

    public static String getRandomPassword() {
        return RandomUtil.randomString(CommonConst.CAPITAL + CommonConst.LOWERCASE_LETTERS + CommonConst.DIGIT, RandomUtil.randomInt(6, 10));

    }
}
