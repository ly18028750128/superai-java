package com.lianziyou.bot.controller.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.EmailConfigAddReq;
import com.lianziyou.bot.model.req.sys.admin.EmailConfigQueryReq;
import com.lianziyou.bot.model.req.sys.admin.EmailConfigUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.EmailConfigQueryRes;
import com.lianziyou.bot.service.sys.IEmailService;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/sys/email/config")
public class EmailConfigController {


    @Resource
    IEmailService emailService;

    @RequestMapping(value = "/queryPage", name = "查询邮件", method = RequestMethod.POST)
    public ApiResult<Page<EmailConfigQueryRes>> queryPage(@Validated @RequestBody EmailConfigQueryReq req) {
        return emailService.queryPage(req);
    }

    @RequestMapping(value = "/add", name = "新增邮件", method = RequestMethod.POST)
    public ApiResult<Void> add(@Validated @RequestBody EmailConfigAddReq req) {
        return emailService.add(req);
    }


    @RequestMapping(value = "/update", name = "编辑邮件", method = RequestMethod.POST)
    public ApiResult<Void> update(@Validated @RequestBody EmailConfigUpdateReq req) {
        return emailService.update(req);
    }


    @RequestMapping(value = "/delete", name = "删除邮件", method = RequestMethod.POST)
    public ApiResult<Void> delete(@Validated @RequestBody BaseDeleteEntity params) {
        return emailService.delete(params);
    }

}
