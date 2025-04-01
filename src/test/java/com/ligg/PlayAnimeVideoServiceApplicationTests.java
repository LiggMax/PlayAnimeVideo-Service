package com.ligg;

import com.ligg.service.anime.SearchAnimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class PlayAnimeVideoServiceApplicationTests {

    @Autowired
    private SearchAnimeService searchAnimeService;

    @Test
    void contextLoads() {
    }
    
    @Test
    void testSearchAnimeService() {
        // 测试搜索动漫列表
        String keyword = "我独自升级";
        List<Map<String, Object>> results = searchAnimeService.searchAnimeList(keyword);
        
        System.out.println("=== 搜索结果 ===");
        System.out.println("关键词: " + keyword);
        System.out.println("结果数量: " + results.size());
        
        // 输出结果详情
        for (int i = 0; i < results.size(); i++) {
            Map<String, Object> anime = results.get(i);
            System.out.println("\n--- 动漫 " + (i + 1) + " ---");
            System.out.println("标题: " + anime.get("title"));
            System.out.println("ID: " + anime.get("id"));
            System.out.println("封面图: " + anime.get("cover"));
            System.out.println("年份: " + anime.get("year"));
            System.out.println("地区: " + anime.get("area"));
            System.out.println("简介: " + anime.get("summary"));
            System.out.println("详情页: " + anime.get("detailUrl"));

            @SuppressWarnings("unchecked")
            List<String> categories = (List<String>) anime.get("categories");
            if (categories != null) {
                System.out.println("分类: " + String.join(", ", categories));
            }
        }
        
        // 测试带分页的搜索
        int page = 1;
        int size = 1;
        List<Map<String, Object>> pagedResults = searchAnimeService.searchAnimeList(keyword, page, size);
        
        System.out.println("\n=== 分页搜索结果 ===");
        System.out.println("页码: " + page + ", 每页大小: " + size);
        System.out.println("结果数量: " + pagedResults.size());
        
        if (!pagedResults.isEmpty()) {
            Map<String, Object> firstAnime = pagedResults.get(0);
            System.out.println("第一个结果标题: " + firstAnime.get("title"));
        }
    }
}
