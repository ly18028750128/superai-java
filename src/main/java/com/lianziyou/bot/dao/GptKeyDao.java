package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.model.GptKey;
import com.lianziyou.bot.model.res.sys.admin.GptKeyQueryRes;
import java.util.List;

public interface GptKeyDao extends BaseMapper<GptKey> {

    int insertSelective(GptKey record);

    int updateByPrimaryKeySelective(GptKey record);

    int updateBatch(List<GptKey> list);

    int updateBatchSelective(List<GptKey> list);

    Page<GptKeyQueryRes> queryGptKey(Page<GptKeyQueryRes> page);
}