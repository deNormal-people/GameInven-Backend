package com.blackcow.blackcowgameinven.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    //싱글톤 관리를 위한 추가
    @Bean
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }
}
