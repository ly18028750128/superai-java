package com.lianziyou.bot.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * swagger
 *
 * @author tom long
 * @version 1.0
 */
@Configuration
@EnableOpenApi
@EnableSwagger2WebMvc
@ConditionalOnProperty(prefix = "swagger", name = "enabled", matchIfMissing = true)
public class Swagger2Config {

    // 默认打开，生产环境需要关闭
    @Value("${swagger.enabled:true}")
    private Boolean enabled;

    @Value("${swagger.title:链自由 Restful Api}")
    private String title;

    @Value("${swagger.description:链自由接口描述}")
    private String description;


    @Value("${swagger.version:1.0}")
    private String version;

    @Value("${swagger.serverUrl:http://127.0.0.1}")
    private String serverUrl;

    @Value("${swagger.contact.name:链自由}")
    private String contactName;

    @Value("${swagger.contact.url:http://127.0.0.1}")
    private String contactUrl;

    @Value("${swagger.contact.email:test.qq.com}")
    private String contactEmail;


    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .enable(enabled)
            .apiInfo(this.apiInfo())
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title(title)
            .description(description)
            .termsOfServiceUrl(serverUrl)
            .contact(new Contact(contactName, contactUrl, contactEmail))
            .version(version)
            .build();
    }


}
