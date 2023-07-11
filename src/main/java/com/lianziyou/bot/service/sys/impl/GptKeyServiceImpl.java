package com.lianziyou.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.dao.GptKeyDao;
import com.lianziyou.bot.enums.sys.ResultEnum;
import com.lianziyou.bot.model.GptKey;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.base.BasePageHelper;
import com.lianziyou.bot.model.req.sys.admin.GptKeyAddReq;
import com.lianziyou.bot.model.req.sys.admin.GptKeyUpdateStateReq;
import com.lianziyou.bot.model.res.sys.admin.GptKeyQueryRes;
import com.lianziyou.bot.service.sys.IGptKeyService;
import com.lianziyou.bot.utils.sys.InitUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("GptKeyService")
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class GptKeyServiceImpl extends ServiceImpl<GptKeyDao, GptKey> implements IGptKeyService {


    @Override
    public ApiResult<Page<GptKeyQueryRes>> queryPage(BasePageHelper basePageHelper) {
        Page<GptKeyQueryRes> page = new Page<>(basePageHelper.getPageNumber(), basePageHelper.getPageSize());
        return ApiResult.okBuild(this.baseMapper.queryGptKey(page));
    }

    @Override
    public ApiResult<Void> add(GptKeyAddReq req) {
        GptKey gptKey = BeanUtil.copyProperties(req, GptKey.class);
        Long count = this.lambdaQuery()
            .eq(null != gptKey.getKey(), GptKey::getKey, gptKey.getKey())
            .count();
        if (count > 0) {
            return ApiResult.finalBuild("key已存在");
        }
        gptKey.setCreateTime(LocalDateTime.now());
        gptKey.setOperateTime(LocalDateTime.now());
        InitUtil.add(gptKey.getKey(), gptKey);
        log.error("新增key：{}======缓存key信息：{}", gptKey.getKey(), InitUtil.getAllKey());
        this.save(gptKey);
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> delete(BaseDeleteEntity req) {
        List<GptKey> list = this.lambdaQuery().in(GptKey::getId, req.getIds()).list();
        this.removeByIds(req.getIds());
        list.forEach(l -> {
            InitUtil.removeKey(l.getKey());
        });
        log.error("删除：{}======缓存key信息：{}", list, InitUtil.getAllKey());
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> updateState(GptKeyUpdateStateReq req) {
        GptKey gptKey = this.getById(req.getId());
        if (null == gptKey) {
            throw new BussinessException("key不存在");
        }
        gptKey.setState(req.getState());
        InitUtil.add(gptKey.getKey(), gptKey);
        this.saveOrUpdate(gptKey);
        return ApiResult.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }
}
