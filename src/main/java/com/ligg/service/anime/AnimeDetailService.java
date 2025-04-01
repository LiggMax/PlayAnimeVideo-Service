package com.ligg.service.anime;

import java.util.List;
import java.util.Map;

/**
 * 动漫详情服务接口
 */
public interface AnimeDetailService {

    /**
     * 获取动漫详情
     * @param url 动漫详情页面URL
     * @return 动漫详情信息，包括基本信息和资源列表
     */
    Map<String, Object> getAnimeDetail(String url);
    
    /**
     * 获取动漫基本信息
     * @param url 动漫详情页面URL
     * @return 动漫基本信息，包括标题、封面、简介等
     */
    Map<String, Object> getAnimeInfo(String url);
    
    /**
     * 获取动漫资源列表
     * @param url 动漫详情页面URL
     * @return 动漫资源列表，包括各个资源和剧集信息
     */
    List<Map<String, Object>> getResourceList(String url);
}
