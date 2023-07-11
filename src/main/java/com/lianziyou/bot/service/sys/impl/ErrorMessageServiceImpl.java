package com.lianziyou.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.dao.ErrorMessageDao;
import com.lianziyou.bot.model.ErrorMessage;
import com.lianziyou.bot.model.base.BasePageHelper;
import com.lianziyou.bot.model.res.sys.admin.ErrorMessageQueryRes;
import com.lianziyou.bot.service.sys.IErrorMessageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("ErrorMessageService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class ErrorMessageServiceImpl extends ServiceImpl<ErrorMessageDao, ErrorMessage> implements IErrorMessageService {


    @Override
    public ApiResult<Page<ErrorMessageQueryRes>> queryPage(BasePageHelper req) {
        Page<ErrorMessageQueryRes> page = new Page<>(req.getPageNumber(), req.getPageSize());
        return ApiResult.okBuild(this.baseMapper.queryErrorMessage(page));
    }
}
