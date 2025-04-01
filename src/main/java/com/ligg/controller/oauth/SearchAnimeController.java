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
    private SearchAnimeService animeService;


}
