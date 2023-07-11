package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.GptKey;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.base.BasePageHelper;
import com.lianziyou.bot.model.req.sys.admin.GptKeyAddReq;
import com.lianziyou.bot.model.req.sys.admin.GptKeyUpdateStateReq;
import com.lianziyou.bot.model.res.sys.admin.GptKeyQueryRes;

public interface IGptKeyService extends IService<GptKey> {


    ApiResult<Page<GptKeyQueryRes>> queryPage(BasePageHelper basePageHelper);

    ApiResult<Void> add(GptKeyAddReq req);

    ApiResult<Void> delete(BaseDeleteEntity req);

    ApiResult<Void> updateState(GptKeyUpdateStateReq req);


}
