package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.PayConfig;
import com.lianziyou.bot.model.req.sys.admin.PayConfigUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.PayConfigQueryRes;


public interface IPayConfigService extends IService<PayConfig> {


    ApiResult<PayConfigQueryRes> queryPayConfig();


    ApiResult<Void> saveOrUpdate(PayConfigUpdateReq req);


}
