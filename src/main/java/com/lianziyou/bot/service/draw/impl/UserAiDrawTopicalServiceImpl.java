package com.lianziyou.bot.service.draw.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.constant.AiDrawConst.TopicalType;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.dao.UserAiDrawTopicalDao;
import com.lianziyou.bot.enums.mj.TaskStatus;
import com.lianziyou.bot.model.MjTask;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.UserAiDrawTopical;
import com.lianziyou.bot.service.draw.UserAiDrawTopicalService;
import com.lianziyou.bot.service.sys.IMjTaskService;
import com.lianziyou.bot.utils.sys.RedisUtil;
import com.lianziyou.bot.vo.DrawTopicalQueryVO;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserAiDrawTopicalServiceImpl extends ServiceImpl<UserAiDrawTopicalDao, UserAiDrawTopical> implements UserAiDrawTopicalService {

    @Resource
    IMjTaskService mjTaskService;

    @Override
    public int insertSelective(UserAiDrawTopical record) {
        return baseMapper.insertSelective(record);
    }

    @Override
    public int updateByPrimaryKeySelective(UserAiDrawTopical record) {
        return baseMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateBatch(List<UserAiDrawTopical> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    public int updateBatchSelective(List<UserAiDrawTopical> list) {
        return baseMapper.updateBatchSelective(list);
    }


    @Override
    public List<UserAiDrawTopical> queryUserDrawTopical(DrawTopicalQueryVO queryParam) {

        final LambdaQueryChainWrapper<UserAiDrawTopical> query = this.lambdaQuery()
            .select(UserAiDrawTopical::getName, UserAiDrawTopical::getCreateTime, UserAiDrawTopical::getId);

        if (ObjectUtil.isNotEmpty(queryParam.getParam().getId())) {
            query.eq(UserAiDrawTopical::getId, queryParam.getParam().getId());
        }

        if (ObjectUtil.isNotEmpty(queryParam.getParam().getUserId())) {
            query.eq(UserAiDrawTopical::getUserId, queryParam.getParam().getUserId());
        }

        if (ObjectUtil.isNotEmpty(queryParam.getParam().getType())) {
            query.eq(UserAiDrawTopical::getType, queryParam.getParam().getType().value);
        }

        if (ObjectUtil.isNotEmpty(queryParam.getParam().getName())) {
            query.like(UserAiDrawTopical::getName, queryParam.getParam().getName());
        }
        query.last(
            (StringUtils.hasLength(queryParam.getSorts()) ? "order by " + queryParam.getSorts() : "") + String.format(" limit %d,%d", queryParam.getStart(),
                queryParam.getLimit()));
        List<UserAiDrawTopical> result = query.list();

        if (queryParam.getParam().getType().equals(TopicalType.MID)) {
            SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
            result.forEach(item -> {
                MjTask mjTask = mjTaskService.lambdaQuery().eq(MjTask::getStatus, TaskStatus.SUCCESS.name()).eq(MjTask::getTopicalId, item.getId())
                    .isNotNull(MjTask::getImageUrl).select(MjTask::getImageUrl).last(" limit 1").one();
                if (mjTask != null && StringUtils.hasLength(mjTask.getImageUrl())) {
                    item.setImageUrl(sysConfig.getImgReturnUrl() + "?fileId=" + URLUtil.encode(mjTask.getImageUrl()));
                }
            });
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeUserAiDrawTopical(Long id) throws Exception {
        this.lambdaUpdate().eq(UserAiDrawTopical::getId, id).remove();
        mjTaskService.lambdaUpdate().eq(MjTask::getTopicalId, id).remove();
        return true;
    }
}
