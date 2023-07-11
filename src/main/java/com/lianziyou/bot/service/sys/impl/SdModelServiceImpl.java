package com.lianziyou.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.dao.SdModelDao;
import com.lianziyou.bot.model.SdModel;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.SdModelAddReq;
import com.lianziyou.bot.model.req.sys.admin.SdModelQueryReq;
import com.lianziyou.bot.model.req.sys.admin.SdModelUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.SdModelQueryRes;
import com.lianziyou.bot.service.sys.ISdModelService;
import com.lianziyou.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("SdModelService")
@Log4j2
@Transactional(rollbackFor = BussinessException.class)
public class SdModelServiceImpl extends ServiceImpl<SdModelDao, SdModel> implements ISdModelService {

    @Override
    public ApiResult<Page<SdModelQueryRes>> queryModelPage(SdModelQueryReq req) {
        Page<SdModelQueryRes> page = new Page<>(req.getPageNumber(), req.getPageSize());
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        Page<SdModelQueryRes> sdModelQueryResPage = this.baseMapper.queryModelPage(page, req);
        sdModelQueryResPage.getRecords().forEach(s -> {
            s.setImgReturnUrl(cacheObject.getImgReturnUrl());
        });
        return ApiResult.okBuild(sdModelQueryResPage);
    }

    @Override
    public ApiResult<Void> addModel(SdModelAddReq req) {
        Long count = this.lambdaQuery().eq(SdModel::getModelName, req.getModelName()).count();
        if (count > 0) {
            throw new BussinessException("模型已存在");
        }
        SdModel sdModel = BeanUtil.copyProperties(req, SdModel.class);
        this.save(sdModel);
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> updateModel(SdModelUpdateReq req) {
        Long count = this.lambdaQuery()
            .eq(SdModel::getModelName, req.getModelName())
            .ne(SdModel::getId, req.getId())
            .count();
        if (count > 0) {
            throw new BussinessException("模型已存在");
        }
        SdModel sdModel = BeanUtil.copyProperties(req, SdModel.class);
        this.updateById(sdModel);
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> deleteModel(BaseDeleteEntity req) {
        this.removeByIds(req.getIds());
        return ApiResult.okBuild();
    }
}
