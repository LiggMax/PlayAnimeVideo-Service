package com.ligg.service.bangumi.impl;

import com.ligg.service.bangumi.BangumiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BangumiServiceImpl implements BangumiService {

    private static final String BANGUMI_URL = "https://api.bgm.tv";
    private static final String SEARCH_URL = "/search/subject/";
    private static final String DETAIL_URL = "/v0/subjects/";
    private static final String CHARACTER_URL = "/v0/subjects/{subject_id}/characters";

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
    public Map getBangumiDetail(Integer id) {
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

    /**
     * 获取番剧角色
     */
    @Override
    public List<Map<String,Object>> getBangumiCharacter(Integer id) {
        String characterUrl = BANGUMI_URL + CHARACTER_URL.replace("{subject_id}", id.toString());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "PlayAnimeVideo/1.0 (ligg@example.com)");

            HttpEntity<Object> entity = new HttpEntity<>(headers);
            ResponseEntity<List<Map<String,Object>>> response = restTemplate.exchange(
                    characterUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });
            return response.getBody();
        }catch (Exception e){
            log.error("番剧角色获取失败", e);
        }
        return null;
    }
}
