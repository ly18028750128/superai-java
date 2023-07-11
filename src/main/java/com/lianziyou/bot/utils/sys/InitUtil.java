package com.lianziyou.bot.utils.sys;


import static com.lianziyou.bot.constant.CommonConst.GPT_CONFIG;

import com.lianziyou.bot.base.exception.BussinessException;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.model.EmailConfig;
import com.lianziyou.bot.model.GptKey;
import com.lianziyou.bot.model.PayConfig;
import com.lianziyou.bot.model.SysConfig;
import com.lianziyou.bot.service.sys.AsyncService;
import com.lianziyou.bot.service.sys.IEmailService;
import com.lianziyou.bot.service.sys.IGptKeyService;
import com.lianziyou.bot.service.sys.IPayConfigService;
import com.lianziyou.bot.service.sys.ISysConfigService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@Log4j2
public class InitUtil {

    private static InitUtil initUtil;
    @Resource
    IGptKeyService gptKeyService;
    @Resource
    AsyncService asyncService;
    @Resource
    ISysConfigService sysConfigService;
    @Resource
    IPayConfigService payConfigService;
    @Resource
    IEmailService emailService;

    /**
     * key缓存池
     */
//    private static final ConcurrentHashMap<String, GptKey> CACHE = new ConcurrentHashMap<>();

    /**
     * 添加key到缓存
     *
     * @param str
     */
    public static void add(String str, GptKey gptKey) {
        RedisUtil.setCacheMapValue(GPT_CONFIG, str, gptKey);
    }

    /**
     * 获取全部的key
     *
     * @return
     */
    public static Collection<GptKey> getAllKey() {
        Map<String, GptKey> cacheMap = RedisUtil.getCacheMap(GPT_CONFIG);
        return cacheMap.values();
    }

    public static synchronized String getRandomKey(Integer type) {
        final Collection<GptKey> allKey = getAllKey();
        if (CollectionUtils.isEmpty(allKey)) {
            throw new BussinessException("缓存池中已无可用的Key 请联系管理员");
        }
        final List<String> list = new ArrayList<>();
        allKey.forEach(key -> {
            if (key.getType().equals(type)) {
                list.add(key.getKey());
            }
        });
        if (CollectionUtils.isEmpty(list)) {
            throw new BussinessException("缓存池中已无可用的Key 请联系管理员");
        }
        Collections.shuffle(list);
        String key = list.get(0);
        initUtil.asyncService.updateKeyNumber(key);
        return key;
    }

    public static void removeKey(final String key) {
        RedisUtil.deleteMapValueByKey(GPT_CONFIG, key);
    }

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        //获取启用的key
        initGpt();
        initUtil = this;
        initUtil.gptKeyService = this.gptKeyService;
        initUtil.asyncService = this.asyncService;
        SysConfig sysConfig = sysConfigService.getBaseMapper().selectOne(null);
        PayConfig payConfig = payConfigService.getBaseMapper().selectOne(null);
        RedisUtil.setCacheObject(CommonConst.SYS_CONFIG, sysConfig);
        RedisUtil.setCacheObject(CommonConst.PAY_CONFIG, payConfig);
        if (sysConfig.getRegistrationMethod() == 2) {
            List<EmailConfig> emailConfigList = emailService.list();
            if (null != emailConfigList && emailConfigList.size() > 0) {
                RedisUtil.setCacheObject(CommonConst.EMAIL_LIST, emailConfigList);
            }
        }
    }

    public void initGpt() {
        List<GptKey> gptKeyList = gptKeyService.lambdaQuery().eq(GptKey::getState, 0).orderByDesc(GptKey::getSort).list();
        gptKeyList.forEach(g -> {
            InitUtil.add(g.getKey(), g);
        });
    }

}
