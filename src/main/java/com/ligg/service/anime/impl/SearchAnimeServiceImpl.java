package com.ligg.service.anime.impl;

import com.ligg.service.anime.SearchAnimeService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchAnimeServiceImpl implements SearchAnimeService {


    private static final String XFDM_URL = "https://cycani.org";
    private static final String SEARCH_URL = "https://cycani.org/search.html?wd=";

    // HTTP请求参数
    private static final int MAX_RETRIES = 3;               // 最大重试次数
    private static final int CONNECTION_TIMEOUT = 100000;   // 连接超时时间(毫秒)
    private static final int READ_TIMEOUT = 300000;         // 读取超时时间(毫秒)

    @Override
    public List<Map<String, Object>> searchAnimeList(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            log.warn("搜索关键词为空");
            return Collections.emptyList();
        }

        try {
            // 获取搜索页面
            Document searchPage = fetchSearchPage(keyword);
            if (searchPage == null) {
                return Collections.emptyList();
            }

            // 解析搜索结果
            List<Map<String, Object>> animeList = parseSearchResults(searchPage);
            log.info("搜索\"{}\"完成，找到{}个结果", keyword, animeList.size());
            
            return animeList;
        } catch (Exception e) {
            log.error("搜索动漫列表出错: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> searchAnimeList(String keyword, int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        
        List<Map<String, Object>> allResults = searchAnimeList(keyword);
        
        // 计算分页参数
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, allResults.size());
        
        // 如果起始索引超出范围，返回空列表
        if (fromIndex >= allResults.size()) {
            return Collections.emptyList();
        }
        
        return allResults.subList(fromIndex, toIndex);
    }
    
    /**
     * 获取搜索页面
     * @param keyword 搜索关键词
     * @return 搜索结果页面
     */
    private Document fetchSearchPage(String keyword) {
        log.info("开始搜索关键词: {}", keyword);
        
        Document searchPage = null;
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                log.debug("尝试第{}次请求搜索页面", attempt);
                searchPage = Jsoup.connect(SEARCH_URL + keyword)
                        .timeout(CONNECTION_TIMEOUT)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                        .followRedirects(true)
                        .get();

                // 成功获取页面，跳出重试循环
                lastException = null;
                break;
            } catch (Exception e) {
                lastException = e;
                log.warn("第{}次请求失败: {}", attempt, e.getMessage());

                if (attempt < MAX_RETRIES) {
                    // 重试前等待，时间随重试次数增加
                    int waitTime = 2000 * attempt;
                    try {
                        log.debug("等待{}毫秒后重试", waitTime);
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("等待过程中被中断", ie);
                        return null;
                    }
                }
            }
        }

        // 检查是否成功获取页面
        if (searchPage == null) {
            if (lastException != null) {
                log.error("获取搜索页面失败，所有重试均失败", lastException);
            } else {
                log.error("获取搜索页面失败，未知原因");
            }
            return null;
        }

        log.info("搜索页面获取成功");
        return searchPage;
    }
    
    /**
     * 解析搜索结果页面中的动漫列表
     * @param document 搜索结果页面
     * @return 动漫信息列表
     */
    private List<Map<String, Object>> parseSearchResults(Document document) {
        List<Map<String, Object>> animeList = new ArrayList<>();
        
        // 查找搜索结果列表项
        Elements searchItems = document.select(".public-list-box.search-box");
        log.info("解析搜索结果，找到{}个结果项", searchItems.size());
        
        for (Element item : searchItems) {
            Map<String, Object> animeInfo = new HashMap<>();
            
            try {
                // 提取标题
                Element titleElement = item.selectFirst(".thumb-txt");
                String title = titleElement != null ? titleElement.text().trim() : "";
                animeInfo.put("title", title);
                
                // 提取封面图
                Element coverElement = item.selectFirst("img.gen-movie-img");
                String cover = "";
                if (coverElement != null) {
                    cover = coverElement.attr("data-src");
                    if (cover.isEmpty()) {
                        cover = coverElement.attr("src");
                    }
                }
                animeInfo.put("cover", cover);
                
                // 提取ID和详情页URL
                Element detailLinkElement = item.selectFirst("a.public-list-exp");
                String detailUrl = "";
                String animeId = "";
                if (detailLinkElement != null) {
                    detailUrl = detailLinkElement.attr("href");
                    if (!detailUrl.startsWith("http")) {
                        detailUrl = XFDM_URL + detailUrl;
                    }
                    
                    // 从URL中提取ID
                    Pattern pattern = Pattern.compile("/bangumi/(\\d+)\\.html");
                    Matcher matcher = pattern.matcher(detailUrl);
                    if (matcher.find()) {
                        animeId = matcher.group(1);
                    }
                }
                animeInfo.put("id", animeId);
                animeInfo.put("detailUrl", detailUrl);
                
                // 提取年份和地区
                Elements elseElements = item.select(".thumb-else span a");
                String year = "";
                String area = "";
                List<String> categories = new ArrayList<>();
                
                for (int i = 0; i < elseElements.size(); i++) {
                    Element elseElement = elseElements.get(i);
                    String text = elseElement.text().trim();
                    String href = elseElement.attr("href");
                    
                    if (i == 0 && href.contains("/year/")) {
                        year = text;
                    } else if (i == 1 && href.contains("/area/")) {
                        area = text;
                    } else if (href.contains("/class/")) {
                        categories.add(text);
                    }
                }
                
                animeInfo.put("year", year);
                animeInfo.put("area", area);
                animeInfo.put("categories", categories);
                
                // 提取简介
                Element summaryElement = item.selectFirst(".thumb-blurb");
                String summary = summaryElement != null ? summaryElement.text().trim() : "";
                animeInfo.put("summary", summary);
                
                animeList.add(animeInfo);
                log.debug("成功解析动漫: {}", title);
                
            } catch (Exception e) {
                log.error("解析动漫项时出错: {}", e.getMessage());
            }
        }
        
        return animeList;
    }
}
