package com.lianziyou.bot.controller.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.UserQueryPageReq;
import com.lianziyou.bot.model.req.sys.admin.UserUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.AdminHomeRes;
import com.lianziyou.bot.model.res.sys.admin.UserQueryPageRes;
import com.lianziyou.bot.service.sys.IUserService;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/sys/user")
public class UserController {


    @Resource
    IUserService userService;


    @RequestMapping(value = "/queryPage", name = "分页查询User", method = RequestMethod.POST)
    public ApiResult<Page<UserQueryPageRes>> queryPage(@Validated @RequestBody UserQueryPageReq req) {
        return userService.queryPage(req);
    }

    @RequestMapping(value = "/update", name = "修改用户", method = RequestMethod.POST)
    public ApiResult<Void> update(@Validated @RequestBody UserUpdateReq req) {
        return userService.update(req);
    }

    @RequestMapping(value = "/delete", name = "删除User", method = RequestMethod.POST)
    public ApiResult<Void> delete(@Validated @RequestBody BaseDeleteEntity params) {
        return userService.delete(params);
    }


    @RequestMapping(value = "/admin/home", name = "管理端首页信息", method = RequestMethod.POST)
    public ApiResult<AdminHomeRes> adminHome() {
        return userService.adminHome();
    }
}
