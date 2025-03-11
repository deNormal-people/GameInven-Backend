package com.blackcow.blackcowgameinven.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        //문서용 설정
        registry.addResourceHandler("/docs/**")
                .addResourceLocations("classpath:/static/docs");
    }
}
