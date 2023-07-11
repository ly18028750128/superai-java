package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.User;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.UserQueryPageReq;
import com.lianziyou.bot.model.req.sys.admin.UserUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.AdminHomeRes;
import com.lianziyou.bot.model.res.sys.admin.UserQueryPageRes;


public interface IUserService extends IService<User> {

    ApiResult<Page<UserQueryPageRes>> queryPage(UserQueryPageReq req);

    ApiResult<Void> update(UserUpdateReq req);

    ApiResult<Void> delete(BaseDeleteEntity req);

    User checkTempUser(String browserFingerprint, String ipaddress);

    ApiResult<AdminHomeRes> adminHome();

    User getOne(String fromUser, String mobile);


}
