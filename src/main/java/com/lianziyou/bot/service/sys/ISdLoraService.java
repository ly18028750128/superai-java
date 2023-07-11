package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.SdLora;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.SdLoraAddReq;
import com.lianziyou.bot.model.req.sys.admin.SdLoraQueryReq;
import com.lianziyou.bot.model.req.sys.admin.SdLoraUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.SdLoraQueryRes;


public interface ISdLoraService extends IService<SdLora> {

    ApiResult<Page<SdLoraQueryRes>> queryLoraPage(SdLoraQueryReq req);

    ApiResult<Void> addLora(SdLoraAddReq req);

    ApiResult<Void> updateLora(SdLoraUpdateReq req);

    ApiResult<Void> deleteLora(BaseDeleteEntity req);

}
