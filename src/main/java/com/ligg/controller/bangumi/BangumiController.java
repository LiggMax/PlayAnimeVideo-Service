package com.ligg.controller.bangumi;

import com.ligg.service.bangumi.BangumiService;
import com.ligg.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public Result<?> SearchBangumi(String keyword){
        Map<String, Object> bangumiSearchList = bangumiService.getBangumiSearchList(keyword);
        return Result.success(200, Objects.requireNonNullElse(bangumiSearchList, "搜索的内容不存在"));
    }

    /**
     * 番剧详情
     */
    @GetMapping("/detail")
    public Result<?> getBangumiDetail(Integer id){
        Map<String, Object> bangumiDetail = bangumiService.getBangumiDetail(id);
        return Result.success(200, Objects.requireNonNullElse(bangumiDetail, "内容不存在"));
    }

    /**
     * 番剧角色
     */
    @GetMapping("/character")
    public Result<List<Map<String,Object>>> getBangumiCharacter(Integer id){
        List<Map<String, Object>> bangumiCharacter = bangumiService.getBangumiCharacter(id);
        if (bangumiCharacter == null){
            return Result.error(400,"内容不存在");
        }
        return Result.success(200,bangumiCharacter);
    }

    /**
     * 番剧剧集
     */
    @GetMapping("/episode")
    public Result<Map<String,Object>> getBangumiEpisode(Integer id){
        Map<String,Object> bangumiEpisode = bangumiService.getBangumiEpisode(id);
        if (bangumiEpisode == null){
            return Result.error(400,"内容不存在");
        }
        return Result.success(200,bangumiEpisode);
    }
}
