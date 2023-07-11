package com.lianziyou.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.model.Product;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.ProductAddReq;
import com.lianziyou.bot.model.req.sys.admin.ProductQueryReq;
import com.lianziyou.bot.model.req.sys.admin.ProductUpdateReq;
import com.lianziyou.bot.model.res.sys.ClientProductRes;
import com.lianziyou.bot.model.res.sys.admin.ProductQueryRes;
import java.util.List;


public interface IProductService extends IService<Product> {


    ApiResult<Page<ProductQueryRes>> queryPage(ProductQueryReq req);


    ApiResult<Void> add(ProductAddReq req);


    ApiResult<Void> update(ProductUpdateReq req);


    ApiResult<Void> delete(BaseDeleteEntity params);


    List<ClientProductRes> getProductList();

}
