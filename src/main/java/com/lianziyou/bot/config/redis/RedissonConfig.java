package com.lianziyou.bot.config.redis;

import lombok.Getter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author Administrator
 */
//@ConditionalOnProperty(prefix = "spring.redis",name = "")
@Configuration
@Primary
public class RedissonConfig extends RedisProperties {

    // 支持如下配置，优先级为，1：主从配置 2：集群配置
//    MasterSlaveServersConfig masterSlaveConfig; // 主从配置
//    ClusterServersConfig clusterConfig; // 集群配置

    @Getter
    private static RedissonClient client;

    @Bean
    public RedissonClient redissonClient() {
        int timeOut = Long.valueOf(this.getTimeout().getSeconds() * 1000).intValue();
        Config config = new Config();
        if (this.getSentinel() != null) {
            String[] nodes = this.getSentinel().getNodes().toArray(new String[]{});
            config.useSentinelServers().addSentinelAddress(nodes).setPassword(this.getSentinel().getPassword()).setMasterName(this.getSentinel().getMaster())
                .setDatabase(this.getDatabase()).setTimeout(timeOut);
        } else if (this.getCluster() != null) {
            String[] nodes = this.getCluster().getNodes().toArray(new String[]{});
            config.useClusterServers().addNodeAddress(nodes).setPassword(this.getPassword()).setTimeout(timeOut);
        } else {
            config.useSingleServer().setAddress("redis://" + this.getHost() + ":" + this.getPort()).setPassword(this.getPassword()).setTimeout(timeOut)
                .setDatabase(this.getDatabase());
        }
//        config.setCodec(JsonJacksonCodec.INSTANCE);
        client = Redisson.create(config);
        return client;
    }

}
