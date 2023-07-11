package com.lianziyou.bot;

import com.github.pagehelper.PageHelper;
import com.lianziyou.bot.utils.sys.IDUtil;
import java.util.Properties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author Administrator
 */
@SpringBootApplication
@MapperScan("com.lianziyou.bot.dao")
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class LianziyouBotApplication {

    /**
     * 初始化ID获取方法，共全局使用
     */
    public static IDUtil idUtil = new IDUtil(1, 0);

    public static void main(String[] args) {
        SpringApplication.run(LianziyouBotApplication.class, args);
    }


    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        /**
         *该参数默认为false
         *设置为true时，会将RowBounds第一个参数offset当成pageNum页码使用
         *和startPage中的pageNum效果一样
         */
        p.setProperty("offsetAsPageNum", "true");
        /**
         *该参数默认为false
         *设置为true时，使用RowBounds分页会进行count查询
         */
        p.setProperty("rowBoundsWithCount", "true");
        /**
         *3.3.0版本可用 - 分页参数合理化，默认false禁用
         *启用合理化时，如果pageNum<1会查询第一页，如果pageNum>pages会查询最后一页
         *禁用合理化时，如果pageNum<1或pageNum>pages会返回空数据
         */
        p.setProperty("reasonable", "true");
        pageHelper.setProperties(p);
        return pageHelper;
    }

}
