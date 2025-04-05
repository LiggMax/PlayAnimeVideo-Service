package com.ligg.service.bangumi.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ligg.service.bangumi.BangumiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class BangumiServiceImpl implements BangumiService {

    private static final String BANGUMI_URL = "https://api.bgm.tv";
    private static final String SEARCH_URL = "/search/subject/";
    private static final String DETAIL_URL = "/v0/subjects/";

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public Map getBangumiSearchList(String keywords) {
        // 构建带查询参数的URL
        String searchUrl = BANGUMI_URL + SEARCH_URL + keywords + "?type=2&responseGroup=medium";

        try {
            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "PlayAnimeVideo/1.0 (ligg@example.com)");
            
            // 创建HttpEntity，包含请求头
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 使用exchange方法发送带请求头的请求
            ResponseEntity<Map> response = restTemplate.exchange(
                searchUrl, 
                HttpMethod.GET, 
                entity, 
                Map.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("请求链接 {} ", searchUrl);
            log.error("番剧搜索失败", e);
        }
        return null;
    }

    @Override
    public Map<String, Object> getBangumiDetail(Integer id) {
        // 构建带查询参数的URL
        String animeUrl = BANGUMI_URL + DETAIL_URL + id;

        try {
            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "PlayAnimeVideo/1.0 (ligg@example.com)");
            
            // 创建HttpEntity，包含请求头
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 使用exchange方法发送带请求头的请求
            ResponseEntity<Map> response = restTemplate.exchange(
                animeUrl, 
                HttpMethod.GET, 
                entity, 
                Map.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("请求链接 {} ", animeUrl);
            log.error("番剧详情获取失败", e);
        }
        return null;
    }
}
