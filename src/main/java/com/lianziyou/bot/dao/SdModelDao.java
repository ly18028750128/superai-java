package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.model.SdModel;
import com.lianziyou.bot.model.req.sys.admin.SdModelQueryReq;
import com.lianziyou.bot.model.res.sys.admin.SdModelQueryRes;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SdModelDao extends BaseMapper<SdModel> {

    int insertSelective(SdModel record);

    int updateByPrimaryKeySelective(SdModel record);

    int updateBatch(List<SdModel> list);

    int updateBatchSelective(List<SdModel> list);

    Page<SdModelQueryRes> queryModelPage(Page<SdModelQueryRes> page, @Param("req") SdModelQueryReq req);
}