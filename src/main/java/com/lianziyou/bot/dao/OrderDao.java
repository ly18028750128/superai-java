package com.lianziyou.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.model.Order;
import com.lianziyou.bot.model.req.sys.admin.OrderQueryReq;
import com.lianziyou.bot.model.res.sys.ClientOrderRes;
import com.lianziyou.bot.model.res.sys.admin.AdminHomeOrder;
import com.lianziyou.bot.model.res.sys.admin.AdminHomeOrderPrice;
import com.lianziyou.bot.model.res.sys.admin.OrderQueryRes;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderDao extends BaseMapper<Order> {

    int insertSelective(Order record);

    int updateByPrimaryKeySelective(Order record);

    int updateBatch(List<Order> list);

    int updateBatchSelective(List<Order> list);

    List<ClientOrderRes> userOrderList(@Param("userId") Long userId);

    Page<OrderQueryRes> queryOrder(Page<OrderQueryRes> page, @Param("req") OrderQueryReq req);

    List<AdminHomeOrder> queryHomeOrder();

    List<AdminHomeOrderPrice> queryHomeOrderPrice();
}