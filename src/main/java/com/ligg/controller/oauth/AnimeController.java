package com.ligg.controller.oauth;

import com.ligg.service.anime.AnimeService;
import com.ligg.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/anime")
public class AnimeController {

    @Autowired
    private AnimeService animeService;


    //搜索
    @GetMapping("/Search")
    public Result<Map<String, Object>> getSearchList(String keyword) {
        Map<String, Object> stringObjectMap = animeService.searchAnime(keyword);
        return Result.success(200, stringObjectMap);
    }

    //播放视频
    @GetMapping("/PlayVideo")
    public Result<String> getPlayVideo(String url) {
        String playVideoUrl = animeService.getPlayVideoUrl(url);
        return Result.success(200, playVideoUrl);
    }
}
