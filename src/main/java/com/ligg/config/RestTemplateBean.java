package com.ligg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateBean {
    
    @Bean
    public RestTemplate RestTemplate() {
        return new RestTemplate();
    }
}
