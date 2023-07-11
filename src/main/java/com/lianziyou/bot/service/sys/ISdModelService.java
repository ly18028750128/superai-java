package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.SdModel;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.SdModelAddReq;
import com.lianziyou.bot.model.req.sys.admin.SdModelQueryReq;
import com.lianziyou.bot.model.req.sys.admin.SdModelUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.SdModelQueryRes;


public interface ISdModelService extends IService<SdModel> {

    ApiResult<Page<SdModelQueryRes>> queryModelPage(SdModelQueryReq req);

    ApiResult<Void> addModel(SdModelAddReq req);

    ApiResult<Void> updateModel(SdModelUpdateReq req);

    ApiResult<Void> deleteModel(BaseDeleteEntity req);


}
