package com.ligg.controller.oauth;

import com.ligg.service.anime.SearchAnimeService;
import com.ligg.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/anime/search")
public class SearchAnimeController {

    @Autowired
    private SearchAnimeService searchAnimeService;

    @GetMapping
    public Result <List<Map<String, Object>>> getSearchList(String keyword) {
        List<Map<String, Object>> stringObjectMap = searchAnimeService.searchAnimeList(keyword);
        return Result.success(200,stringObjectMap);
    }
}
