package com.ligg.service.bangumi;

import org.jsoup.nodes.Element;
import org.springframework.http.ResponseEntity;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BangumiService {

    /**
     * bangumi动漫搜索
     */
    Map<String,Object> getBangumiSearchList(String keywords);

    /**
     * bangumi动漫详情
     */
    Map<String,Object> getBangumiDetail(Integer id);

    /**
     * 角色信息
     */
   List< Map<String,Object>> getBangumiCharacter(Integer id);

    /**
     * 剧集信息
     */
    Map<String,Object> getBangumiEpisode(Integer id);
}
