package com.ligg.Anime;

import com.ligg.service.anime.AnimeDetailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class AnimeDetailTest {

    private static final String DETAIL_URL = "https://dm1.xfdm.pro/bangumi/54.html";
    
    @Autowired
    private AnimeDetailService animeDetailService;


    @Test
    //获取动漫详情
    public void testGetAnimeDetail() {
        try {
            log.info("开始测试获取动漫详情: {}", DETAIL_URL);
            
            // 调用业务服务获取动漫详情信息
            Map<String, Object> result = animeDetailService.getAnimeDetail(DETAIL_URL);
            
            if (result != null && !result.isEmpty()) {
                // 从结果中提取动漫基本信息
                @SuppressWarnings("unchecked")
                Map<String, Object> animeInfo = (Map<String, Object>) result.get("animeInfo");
                
                // 从结果中提取资源列表
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> resourceList = (List<Map<String, Object>>) result.get("resourceList");
                
                // 打印详情
                printAnimeDetail(animeInfo, resourceList);
            } else {
                log.error("获取动漫详情失败，返回结果为空");
            }
        } catch (Exception e) {
            log.error("测试获取动漫详情失败", e);
        }
    }
    
    @Test
    //只获取动漫基本信息
    public void testGetAnimeInfo() {
        try {
            log.info("开始测试获取动漫基本信息: {}", DETAIL_URL);
            
            // 调用业务服务获取动漫基本信息
            Map<String, Object> animeInfo = animeDetailService.getAnimeInfo(DETAIL_URL);
            
            if (animeInfo != null && !animeInfo.isEmpty()) {
                System.out.println("\n========== 动漫基本信息 ==========");
                System.out.println("标题: " + animeInfo.get("title"));
                System.out.println("封面: " + animeInfo.get("cover"));
                System.out.println("年份: " + animeInfo.get("year"));
                System.out.println("地区: " + animeInfo.get("area"));
                System.out.println("播放量: " + animeInfo.get("views"));
                System.out.println("评分: " + animeInfo.get("score"));
                
                @SuppressWarnings("unchecked")
                List<String> categories = (List<String>) animeInfo.get("categories");
                System.out.println("分类: " + String.join(", ", categories));
                
                @SuppressWarnings("unchecked")
                List<String> directors = (List<String>) animeInfo.get("directors");
                System.out.println("导演: " + String.join(", ", directors));
                
                @SuppressWarnings("unchecked")
                List<String> actors = (List<String>) animeInfo.get("actors");
                System.out.println("演员: " + String.join(", ", actors));
                
                System.out.println("\n简介: " + animeInfo.get("summary"));
            } else {
                log.error("获取动漫基本信息失败，返回结果为空");
            }
        } catch (Exception e) {
            log.error("测试获取动漫基本信息失败", e);
        }
    }
    
    @Test
    //获取资源列表
    public void testGetResourceList() {
        try {
            log.info("开始测试获取动漫资源列表: {}", DETAIL_URL);
            
            // 调用业务服务获取动漫资源列表
            List<Map<String, Object>> resourceList = animeDetailService.getResourceList(DETAIL_URL);
            
            if (resourceList != null && !resourceList.isEmpty()) {
                System.out.println("\n========== 资源列表 ==========");
                
                for (int i = 0; i < resourceList.size(); i++) {
                    Map<String, Object> resource = resourceList.get(i);
                    
                    System.out.println("\n--- 资源 " + (i+1) + ": " + resource.get("name") + " (共" + resource.get("count") + "集) ---");
                    
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> episodes = (List<Map<String, String>>) resource.get("episodes");
                    
                    for (int j = 0; j < episodes.size(); j++) {
                        Map<String, String> episode = episodes.get(j);
                        System.out.println((j+1) + ". " + episode.get("name") + ": " + episode.get("url"));
                    }
                }
            } else {
                log.error("获取动漫资源列表失败，返回结果为空");
            }
        } catch (Exception e) {
            log.error("测试获取动漫资源列表失败", e);
        }
    }
    
    /**
     * 打印动漫详情信息
     * @param animeInfo 动漫基本信息
     * @param resourceList 资源列表
     */
    private void printAnimeDetail(Map<String, Object> animeInfo, List<Map<String, Object>> resourceList) {
        System.out.println("\n========== 动漫详情 ==========");
        System.out.println("标题: " + animeInfo.get("title"));
        System.out.println("封面: " + animeInfo.get("cover"));
        System.out.println("年份: " + animeInfo.get("year"));
        System.out.println("地区: " + animeInfo.get("area"));
        System.out.println("播放量: " + animeInfo.get("views"));
        System.out.println("评分: " + animeInfo.get("score"));
        
        @SuppressWarnings("unchecked")
        List<String> categories = (List<String>) animeInfo.get("categories");
        if (categories != null && !categories.isEmpty()) {
            System.out.println("分类: " + String.join(", ", categories));
        } else {
            System.out.println("分类: 暂无");
        }
        
        @SuppressWarnings("unchecked")
        List<String> directors = (List<String>) animeInfo.get("directors");
        if (directors != null && !directors.isEmpty()) {
            System.out.println("导演: " + String.join(", ", directors));
        } else {
            System.out.println("导演: 暂无");
        }
        
        @SuppressWarnings("unchecked")
        List<String> actors = (List<String>) animeInfo.get("actors");
        if (actors != null && !actors.isEmpty()) {
            System.out.println("演员: " + String.join(", ", actors));
        } else {
            System.out.println("演员: 暂无");
        }
        
        System.out.println("\n简介: " + animeInfo.get("summary"));
        
        System.out.println("\n========== 资源列表 ==========");
        if (resourceList != null && !resourceList.isEmpty()) {
            for (int i = 0; i < resourceList.size(); i++) {
                Map<String, Object> resource = resourceList.get(i);
                
                System.out.println("\n--- 资源 " + (i+1) + ": " + resource.get("name") + " (共" + resource.get("count") + "集) ---");
                
                @SuppressWarnings("unchecked")
                List<Map<String, String>> episodes = (List<Map<String, String>>) resource.get("episodes");
                
                for (int j = 0; j < episodes.size(); j++) {
                    Map<String, String> episode = episodes.get(j);
                    System.out.println((j+1) + ". " + episode.get("name") + ": " + episode.get("url"));
                }
            }
        } else {
            System.out.println("暂无资源");
        }
    }
}
