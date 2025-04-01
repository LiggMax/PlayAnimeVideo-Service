package com.ligg.service.anime.impl;

import com.ligg.service.anime.AnimeDetailService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AnimeDetailServiceImpl implements AnimeDetailService {

    @Override
    public Map<String, Object> getAnimeDetail(String url) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> animeInfo = getAnimeInfo(url);
            List<Map<String, Object>> resourceList = getResourceList(url);
            
            result.put("animeInfo", animeInfo);
            result.put("resourceList", resourceList);
        } catch (Exception e) {
            log.error("获取动漫详情失败: {}", url, e);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getAnimeInfo(String url) {
        Map<String, Object> animeInfo = new HashMap<>();
        
        try {
            Document document = fetchDocument(url);
            if (document == null) {
                return animeInfo;
            }
            
            // 提取标题
            Element titleElement = document.selectFirst(".slide-info-title");
            String title = titleElement != null ? titleElement.text().trim() : "";
            animeInfo.put("title", title);
            
            // 提取封面图
            Element coverElement = document.selectFirst(".detail-pic img");
            String cover = "";
            if (coverElement != null) {
                cover = coverElement.attr("data-src");
                if (cover.isEmpty()) {
                    cover = coverElement.attr("src");
                }
                // 确保封面图URL是完整的
                if (!cover.isEmpty() && !cover.startsWith("http")) {
                    cover = resolveUrl(url, cover);
                }
            }
            animeInfo.put("cover", cover);
            
            // 提取简介
            Element summaryElement = document.selectFirst("#height_limit");
            String summary = summaryElement != null ? summaryElement.text().trim() : "";
            animeInfo.put("summary", summary);
            
            // 提取分类信息
            Elements categoryElements = document.select(".slide-info:has(.cor6:contains(类型)) a");
            List<String> categories = new ArrayList<>();
            for (Element element : categoryElements) {
                categories.add(element.text().trim());
            }
            animeInfo.put("categories", categories);
            
            // 提取导演信息
            Elements directorElements = document.select(".slide-info:has(.cor6:contains(导演)) a");
            List<String> directors = new ArrayList<>();
            for (Element element : directorElements) {
                directors.add(element.text().trim());
            }
            animeInfo.put("directors", directors);
            
            // 提取演员信息
            Elements actorElements = document.select(".slide-info:has(.cor6:contains(演员)) a");
            List<String> actors = new ArrayList<>();
            for (Element element : actorElements) {
                actors.add(element.text().trim());
            }
            animeInfo.put("actors", actors);
            
            // 提取年份和地区
            Elements infoRemarks = document.select(".slide-info-remarks a");
            String year = "";
            String area = "";
            
            if (infoRemarks.size() >= 1) {
                Element yearElement = infoRemarks.get(0);
                year = yearElement.text().trim();
            }
            
            if (infoRemarks.size() >= 2) {
                Element areaElement = infoRemarks.get(1);
                area = areaElement.text().trim();
            }
            
            animeInfo.put("year", year);
            animeInfo.put("area", area);
            
            // 获取播放量
            Element viewsElement = document.selectFirst(".slide-info-remarks:first-child");
            String views = viewsElement != null ? viewsElement.text().trim() : "0";
            animeInfo.put("views", views);
            
            // 获取评分
            Element scoreElement = document.selectFirst(".fraction");
            String score = scoreElement != null ? scoreElement.text().trim() : "0.0";
            animeInfo.put("score", score);
            
        } catch (Exception e) {
            log.error("解析动漫基本信息失败: {}", url, e);
        }
        
        return animeInfo;
    }

    @Override
    public List<Map<String, Object>> getResourceList(String url) {
        List<Map<String, Object>> resourceList = new ArrayList<>();
        
        try {
            Document document = fetchDocument(url);
            if (document == null) {
                return resourceList;
            }
            
            // 获取资源列表选项卡
            Elements tabElements = document.select(".anthology-tab .swiper-wrapper a.swiper-slide");
            
            // 获取所有剧集列表容器
            Elements episodeContainers = document.select(".anthology-list-box");
            
            // 确保选项卡和剧集列表容器数量一致
            int count = Math.min(tabElements.size(), episodeContainers.size());
            
            for (int i = 0; i < count; i++) {
                Map<String, Object> resource = new HashMap<>();
                
                // 提取资源名称和剧集数量
                Element tabElement = tabElements.get(i);
                String name = tabElement.ownText().trim();
                Element badgeElement = tabElement.selectFirst(".badge");
                String episodeCount = badgeElement != null ? badgeElement.text().trim() : "0";
                
                resource.put("name", name);
                resource.put("count", episodeCount);
                
                // 提取该资源下的所有剧集
                Element episodeContainer = episodeContainers.get(i);
                Elements episodeElements = episodeContainer.select("ul.anthology-list-play li a");
                
                List<Map<String, String>> episodes = new ArrayList<>();
                
                for (Element episodeElement : episodeElements) {
                    Map<String, String> episode = new HashMap<>();
                    
                    String episodeName = episodeElement.text().trim();
                    String episodeUrl = episodeElement.attr("href");
                    
                    // 确保URL是完整的
                    if (!episodeUrl.isEmpty() && !episodeUrl.startsWith("http")) {
                        episodeUrl = resolveUrl(url, episodeUrl);
                    }
                    
                    episode.put("name", episodeName);
                    episode.put("url", episodeUrl);
                    
                    episodes.add(episode);
                }
                
                resource.put("episodes", episodes);
                resourceList.add(resource);
            }
            
        } catch (Exception e) {
            log.error("解析资源列表和剧集信息失败: {}", url, e);
        }
        
        return resourceList;
    }
    
    /**
     * 获取网页文档
     * @param url 页面URL
     * @return Document对象
     */
    private Document fetchDocument(String url) {
        try {
            log.info("开始获取动漫详情页面: {}", url);
            
            Connection.Response response = Jsoup.connect(url)
                    .timeout(10000)
                    .method(Connection.Method.GET)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                    .followRedirects(true)
                    .execute();
            
            return response.parse();
        } catch (Exception e) {
            log.error("获取动漫详情页面失败: {}", url, e);
            return null;
        }
    }
    
    /**
     * 解析相对URL为完整URL
     * @param baseUrl 基础URL
     * @param relativeUrl 相对URL
     * @return 完整的URL
     */
    private String resolveUrl(String baseUrl, String relativeUrl) {
        try {
            URI baseUri = new URI(baseUrl);
            URI resolvedUri = baseUri.resolve(relativeUrl);
            return resolvedUri.toString();
        } catch (URISyntaxException e) {
            log.error("URL解析失败: baseUrl={}, relativeUrl={}", baseUrl, relativeUrl, e);
            return relativeUrl;
        }
    }
}
