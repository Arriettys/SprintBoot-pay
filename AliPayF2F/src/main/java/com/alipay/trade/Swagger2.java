package com.alipay.trade;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2
{
	@Bean
    public Docket createRestApi() 
	{
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.alipay.trade.controller"))
                .paths(PathSelectors.any())
                .build();
    }
	
	private ApiInfo apiInfo() 
	{
        return new ApiInfoBuilder()
                .title("AliPayF2FRESTful APIs")
                .description("支付宝当面付接口测试，沙盒模拟，沙盒网址：https://openhome.alipay.com/platform/appDaily.htm?tab=account")
                .termsOfServiceUrl("http://arrietty.top/")
                .contact("Arrietty")
                .version("1.0")
                .build();
    }
}
