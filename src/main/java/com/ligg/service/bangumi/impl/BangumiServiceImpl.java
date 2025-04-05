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

    private static final String BANGUMI_SEARCH_URL = "https://api.bgm.tv";
    @Autowired
    private WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Map getBangumiSearchList(String keywords) {
        // 构建带查询参数的URL
        String searchUrl = BANGUMI_SEARCH_URL + "/search/subject/" + keywords + "?type=2&responseGroup=medium";

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(searchUrl, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("番剧搜索失败", e);
        }
        return null;
    }
}
