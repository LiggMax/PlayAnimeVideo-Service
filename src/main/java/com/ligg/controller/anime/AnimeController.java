package com.ligg.controller.anime;

import com.ligg.service.anime.AnimeDetailService;
import com.ligg.service.anime.SearchAnimeService;
import com.ligg.service.anime.VideoUrlParserService;
import com.ligg.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/anime")
public class AnimeController {

    @Autowired
    private SearchAnimeService searchAnimeService;

    @Autowired
    private AnimeDetailService animeDetailService;

    @Autowired
    private VideoUrlParserService videoUrlParserService;
    /**
     * 搜索动漫
     * @param keyword 关键词
     * @return 搜索列表
     */
    @GetMapping("/search")
    public Result<List<Map<String, Object>>> getSearchList(String keyword) {
        List<Map<String, Object>> stringObjectMap = searchAnimeService.searchAnimeList(keyword);
        return Result.success(200,stringObjectMap);
    }

    /**
     * 获取视频详情
     * @param url 动漫详情页url
     * @return 动漫详情列表
     */
    @GetMapping("/videoDate")
    public Result<Map<String, Object>> getVideoDetail(String url) {
        Map<String, Object> stringObjectMap = animeDetailService.getAnimeDetail(url);
        return Result.success(200,stringObjectMap);
    }

    /**
     * 获取视频播放地址
     * @param url 剧集链接
     * @return 视频播放地址
     */
    @GetMapping("/videoUrl")
    public Result<List<String>> getVideoUrl(String url) {
        List<String> videoUrls = videoUrlParserService.parseVideoUrls(url);
        return Result.success(200,videoUrls);
    }
}
