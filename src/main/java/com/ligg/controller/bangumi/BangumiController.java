package com.ligg.controller.bangumi;

import com.ligg.service.bangumi.BangumiService;
import com.ligg.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bangumi")
public class BangumiController {

    @Autowired
    private BangumiService bangumiService;
    /**
     *番剧搜索
     */
    @GetMapping("/search")
    public Result<?> SerchBangumi(String keyword){

        Map<String, Object> bangumiSearchList = bangumiService.getBangumiSearchList(keyword);
        if (bangumiSearchList != null){
            return Result.success(200,bangumiSearchList);
        }
        return Result.success(200, "搜索的内容不存在");
    }
}
