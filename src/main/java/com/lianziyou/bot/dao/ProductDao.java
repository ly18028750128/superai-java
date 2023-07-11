package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.model.Product;
import com.lianziyou.bot.model.req.sys.admin.ProductQueryReq;
import com.lianziyou.bot.model.res.sys.ClientProductRes;
import com.lianziyou.bot.model.res.sys.admin.ProductQueryRes;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductDao extends BaseMapper<Product> {

    int insertSelective(Product record);

    int updateByPrimaryKeySelective(Product record);

    int updateBatch(List<Product> list);

    int updateBatchSelective(List<Product> list);

    Page<ProductQueryRes> queryPage(@Param("page") Page<ProductQueryRes> page, @Param("req") ProductQueryReq req);

    List<ClientProductRes> getProductList();
}