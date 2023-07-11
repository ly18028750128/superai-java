package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.ErrorMessage;
import com.lianziyou.bot.model.base.BasePageHelper;
import com.lianziyou.bot.model.res.sys.admin.ErrorMessageQueryRes;


public interface IErrorMessageService extends IService<ErrorMessage> {

    ApiResult<Page<ErrorMessageQueryRes>> queryPage(BasePageHelper req);


}
