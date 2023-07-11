package com.lianziyou.bot.config;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 */
@Configuration
@Slf4j
public class ServerConfig implements ApplicationListener<WebServerInitializedEvent> {

    @Getter
    private static int serverPort;

    @Getter
    private static String ipPort;

    // 获取项目IP
    public static String getIpAndPort() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
            if (serverPort == 0) { // 获取外置tomcat端口号
                serverPort = getTomcatPort();
            }
            return address.getHostAddress() + ":" + serverPort;
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
        return null;

    }

    public static int getTomcatPort() {
        MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            QueryExp protocol = Query.match(Query.attr("protocol"), Query.value("HTTP/1.1"));
            ObjectName name = new ObjectName("*:type=Connector,*");
            Set<ObjectName> objectNames = beanServer.queryNames(name, protocol);
            for (ObjectName objectName : objectNames) {
                String catalina = objectName.getDomain();
                if ("Catalina".equals(catalina)) {
                    return Integer.parseInt(objectName.getKeyProperty("port"));
                }
            }
        } catch (MalformedObjectNameException e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    //获取内置tomcat端口号
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        ServerConfig.serverPort = event.getWebServer().getPort();
        ipPort = getIpAndPort();
    }
}
