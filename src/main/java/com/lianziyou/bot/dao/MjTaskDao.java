package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianziyou.bot.model.MjTask;
import com.lianziyou.bot.model.res.sys.MjTaskRes;
import com.lianziyou.bot.model.res.sys.MjTaskTransformRes;
import com.lianziyou.bot.vo.CommonParam;
import com.lianziyou.bot.vo.HomeQueryVO.HomeQueryParam;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MjTaskDao extends BaseMapper<MjTask> {

    int insertSelective(MjTask record);

    int updateByPrimaryKeySelective(MjTask record);

    int updateBatch(List<MjTask> list);

    int updateBatchSelective(List<MjTask> list);

    List<MjTaskRes> selectUserMjTask(@Param("userId") Long userId,@Param("pageParam") CommonParam<HomeQueryParam> pageParam);

    List<MjTaskTransformRes> selectTransform(@Param("relatedTaskId") Long id);

    int batchDeleteByUserId(@Param("userId") Long userId);

    int deleteByKeyId(@Param("id") Long id);
}