package com.ligg.service.anime;

import java.util.Map;

/**
 * 动漫服务接口
 */
public interface AnimeService {

    /**
     * 搜索动漫并返回详情信息，包含所有剧集
     * @param keyword 搜索关键词
     * @return 动漫详细信息的Map，包含标题、简介、封面图片和所有线路的所有剧集信息
     */
    Map<String, Object> searchAnime(String keyword);

    /**
     * 获取播放视频的URL
     * @param url 播放视频的URL
     * @return 播放视频的URL
     */
    String getPlayVideoUrl(String url);
}
