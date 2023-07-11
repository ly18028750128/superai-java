package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.req.sys.admin.SysConfigUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.SysConfigQueryRes;


public interface ISysConfigService extends IService<SysConfig> {

    ApiResult<SysConfigQueryRes> queryPage();

    ApiResult<Void> savaOrUpdate(SysConfigUpdateReq req);

}
