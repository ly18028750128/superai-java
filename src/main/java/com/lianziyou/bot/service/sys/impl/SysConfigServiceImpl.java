package com.lianziyou.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.dao.SysConfigDao;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.req.sys.admin.SysConfigUpdateReq;
import com.lianziyou.bot.model.res.sys.admin.SysConfigQueryRes;
import com.lianziyou.bot.service.sys.ISysConfigService;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("SysConfigService")
@Transactional(rollbackFor = Exception.class)
public class SysConfigServiceImpl extends ServiceImpl<SysConfigDao, SysConfig> implements ISysConfigService {

    @Override
    public ApiResult<SysConfigQueryRes> queryPage() {
        SysConfig sysConfig = this.lambdaQuery().eq(SysConfig::getId, 1).one();
        return ApiResult.okBuild(BeanUtil.copyProperties(sysConfig, SysConfigQueryRes.class));
    }

    @Override
    public ApiResult<Void> savaOrUpdate(SysConfigUpdateReq req) {

        SysConfig sysConfig = this.baseMapper.selectOne(null);
        if (sysConfig == null) {
            sysConfig = new SysConfig();
        }
        BeanUtil.copyProperties(req, sysConfig, CommonConst.DEFAULT_COPY_OPTIONS);
        sysConfig.setOperateTime(LocalDateTime.now());
        super.saveOrUpdate(sysConfig);
        RedisUtil.setCacheObject(CommonConst.SYS_CONFIG, this.getById(sysConfig.getId()));
        return ApiResult.okBuild();
    }
}
