package com.lianziyou.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.dao.ProductDao;
import com.lianziyou.bot.enums.sys.ResultEnum;
import com.lianziyou.bot.model.Product;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.req.sys.admin.ProductAddReq;
import com.lianziyou.bot.model.req.sys.admin.ProductQueryReq;
import com.lianziyou.bot.model.req.sys.admin.ProductUpdateReq;
import com.lianziyou.bot.model.res.sys.ClientProductRes;
import com.lianziyou.bot.model.res.sys.admin.ProductQueryRes;
import com.lianziyou.bot.service.sys.IProductService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("ProductService")
@Transactional(rollbackFor = Exception.class)
public class ProductServiceImpl extends ServiceImpl<ProductDao, Product> implements IProductService {

    @Override
    public ApiResult<Page<ProductQueryRes>> queryPage(ProductQueryReq req) {
        Page<ProductQueryRes> page = new Page<>(req.getPageNumber(), req.getPageSize());
        return ApiResult.okBuild(this.baseMapper.queryPage(page, req));
    }

    @Override
    public ApiResult<Void> add(ProductAddReq req) {
        Product product = BeanUtil.copyProperties(req, Product.class);
        Long count = this.lambdaQuery()
            .eq(null != product.getName(), Product::getName, product.getName())
            .count();
        if (count > 0) {
            return ApiResult.finalBuild("商品已存在");

        }
        this.save(product);
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> update(ProductUpdateReq req) {
        Product product = BeanUtil.copyProperties(req, Product.class);
        Long count = this.lambdaQuery()
            .eq(null != product.getName(), Product::getName, product.getName())
            .ne(Product::getId, product.getId())
            .count();
        if (count > 0) {
            return ApiResult.build(ResultEnum.FAIL.getCode(), "商品已存在");
        }
        this.saveOrUpdate(product);
        return ApiResult.okBuild();
    }

    @Override
    public ApiResult<Void> delete(BaseDeleteEntity params) {
        this.removeByIds(params.getIds());
        return ApiResult.okBuild();
    }

    @Override
    public List<ClientProductRes> getProductList() {
        return this.baseMapper.getProductList();
    }
}
