package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.model.SdLora;
import com.lianziyou.bot.model.req.sys.admin.SdLoraQueryReq;
import com.lianziyou.bot.model.res.sys.admin.SdLoraQueryRes;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SdLoraDao extends BaseMapper<SdLora> {

    int insertSelective(SdLora record);

    int updateByPrimaryKeySelective(SdLora record);

    int updateBatch(List<SdLora> list);

    int updateBatchSelective(List<SdLora> list);

    Page<SdLoraQueryRes> queryLoraPage(Page<SdLoraQueryRes> page, @Param("req") SdLoraQueryReq req);
}