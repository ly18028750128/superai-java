package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.EmailConfig;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.EmailConfigAddReq;
import com.lianziyou.bot.model.req.sys.admin.EmailConfigQueryReq;
import com.lianziyou.bot.model.req.sys.admin.EmailConfigUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.EmailConfigQueryRes;


public interface IEmailService extends IService<EmailConfig> {


    ApiResult<Page<EmailConfigQueryRes>> queryPage(EmailConfigQueryReq req);


    ApiResult<Void> add(EmailConfigAddReq req);


    ApiResult<Void> update(EmailConfigUpdateReq req);


    ApiResult<Void> delete(BaseDeleteEntity req);


}
