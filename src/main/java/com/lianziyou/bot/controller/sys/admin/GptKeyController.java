package com.lianziyou.bot.controller.sys.admin;

import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.model.base.BaseDeleteEntity;
import com.lianziyou.bot.model.base.BasePageHelper;
import com.lianziyou.bot.model.req.gpt.GptBillingReq;
import com.lianziyou.bot.model.req.sys.admin.GptKeyAddReq;
import com.lianziyou.bot.model.req.sys.admin.GptKeyUpdateStateReq;
import com.lianziyou.bot.model.res.gpt.GptBillingRes;
import com.lianziyou.bot.model.res.sys.admin.GptKeyQueryRes;
import com.lianziyou.bot.service.sys.IGptKeyService;
import com.lianziyou.bot.utils.gpt.Proxys;
import com.lianziyou.bot.utils.sys.DateUtil;
import com.lianziyou.bot.utils.sys.RedisUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Proxy;
import java.time.LocalDate;
import javax.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/sys/gpt/key")
@Log4j2
public class GptKeyController {


    @Resource
    IGptKeyService gptKeyService;


    @RequestMapping(value = "/queryPage", name = "查询key", method = RequestMethod.POST)
    public ApiResult<Page<GptKeyQueryRes>> queryPage(@Validated @RequestBody BasePageHelper basePageHelper) {
        return gptKeyService.queryPage(basePageHelper);
    }


    @RequestMapping(value = "/add", name = "新增key", method = RequestMethod.POST)
    public ApiResult<Void> add(@Validated @RequestBody GptKeyAddReq req) {
        return gptKeyService.add(req);
    }


    @RequestMapping(value = "/delete", name = "删除key", method = RequestMethod.POST)
    public ApiResult<Void> delete(@Validated @RequestBody BaseDeleteEntity req) {
        return gptKeyService.delete(req);
    }

    @RequestMapping(value = "/updateState", name = "修改key状态", method = RequestMethod.POST)
    public ApiResult updateState(@Validated @RequestBody GptKeyUpdateStateReq req) {
        return gptKeyService.updateState(req);
    }

    @PostMapping(value = "/billing", name = "余额查询")
    public ApiResult<GptBillingRes> billing(@Validated @RequestBody GptBillingReq req) {
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        Proxy proxy = null;
        if (null != cacheObject.getIsOpenProxy() && cacheObject.getIsOpenProxy() == 1) {
            proxy = Proxys.http(cacheObject.getProxyIp(), cacheObject.getProxyPort());
        }
        String resultBody = HttpUtil.createGet(cacheObject.getGptUrl() + CommonConst.CPT_SUBSCRIPTION_URL)
            .header(Header.AUTHORIZATION, "Bearer " + req.getKey())
            .setProxy(proxy)
            .execute()
            .body();
        if (!resultBody.contains("hard_limit_usd")) {
            throw new BussinessException("余额查询失败");
        }
        GptBillingRes res = new GptBillingRes();
        res.setHardLimitUsd(JSONObject.parseObject(resultBody).getBigDecimal("hard_limit_usd"));
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(1);
        resultBody = HttpUtil.createGet(cacheObject.getGptUrl() + String.format(CommonConst.CPT_BILLING_USAGE_URL, DateUtil.DFT.format(startDate),
                DateUtil.DFT.format(endDate)))
            .header(Header.AUTHORIZATION, "Bearer " + req.getKey())
            .setProxy(proxy)
            .execute()
            .body();
        if (!resultBody.contains("total_usage")) {
            throw new BussinessException("余额查询失败");
        }
        JSONObject bodyJson = JSONObject.parseObject(resultBody);
        res.setTotalUsage(bodyJson.getBigDecimal("total_usage").divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
        JSONArray dailyCosts = bodyJson.getJSONArray("daily_costs");
        log.info("dailyCosts:{}", dailyCosts);
        return ApiResult.okBuild(res);
    }

}
