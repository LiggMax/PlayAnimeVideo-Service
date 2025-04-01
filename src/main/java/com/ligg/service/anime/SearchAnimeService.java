package com.ligg.service.anime;

import java.util.List;
import java.util.Map;

/**
 * 动漫搜索服务接口
 */
public interface SearchAnimeService {

    /**
     * 搜索动漫列表
     * @param keyword 搜索关键词
     * @return 搜索结果列表，每个元素包含动漫的详细信息
     */
    List<Map<String, Object>> searchAnimeList(String keyword);
    
    /**
     * 搜索动漫列表（带分页）
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 搜索结果列表，每个元素包含动漫的详细信息
     */
    List<Map<String, Object>> searchAnimeList(String keyword, int page, int size);
}
