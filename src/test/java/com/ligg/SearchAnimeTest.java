package com.ligg;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@SpringBootTest
public class SearchAnimeTest {

    private static final String KEYWORD = "我独自升级";

    private static final String XFDM_URL = "https://dm1.xfdm.pro";

    private static final String BASE_URL = "https://dm1.xfdm.pro/search.html?wd=";

    @Test
    public void searchAnimeListTest() {
        // 设置HTTP请求参数
        int maxRetries = 3;                // 最大重试次数
        int connectionTimeout = 600000;    // 连接超时时间(毫秒)
        int readTimeout = 300000;          // 读取超时时间(毫秒)

        try {
            Document searchPage = null;
            Exception lastException = null;

            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    System.out.println("尝试第" + attempt + "次请求搜索页面...");
                    searchPage = Jsoup.connect(BASE_URL + KEYWORD)
                            .timeout(connectionTimeout)
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
                    System.out.println("第" + attempt + "次请求失败: " + e.getMessage());

                    if (attempt < maxRetries) {
                        // 重试前等待，时间随重试次数增加
                        int waitTime = 2000 * attempt;
                        System.out.println("等待" + waitTime + "毫秒后重试...");
                        Thread.sleep(waitTime);
                    }
                }
            }

            // 检查是否成功获取页面
            if (searchPage == null) {
                if (lastException != null) {
                    throw lastException;
                } else {
                    throw new RuntimeException("无法获取搜索页面，所有重试均失败");
                }
            }

            System.out.println("\n搜索页面获取成功，开始解析结果...");
            
            // 解析搜索结果列表
            List<Map<String, Object>> animeList = parseSearchResults(searchPage);
            
            // 输出搜索结果
            System.out.println("\n=== 搜索结果 ===");
            System.out.println("找到 " + animeList.size() + " 个匹配动漫");
            
            for (int i = 0; i < animeList.size(); i++) {
                Map<String, Object> anime = animeList.get(i);
                System.out.println("\n--- 动漫 " + (i + 1) + " ---");
                System.out.println("标题: " + anime.get("title"));
                System.out.println("ID: " + anime.get("id"));
                System.out.println("封面图: " + anime.get("cover"));
                System.out.println("年份: " + anime.get("year"));
                System.out.println("地区: " + anime.get("area"));
                System.out.println("简介: " + anime.get("summary"));
                
                @SuppressWarnings("unchecked")
                List<String> categories = (List<String>) anime.get("categories");
                System.out.println("分类: " + String.join(", ", categories));
                
                System.out.println("详情页URL: " + anime.get("detailUrl"));
            }

        } catch (Exception e) {
            System.out.println("\n搜索过程发生错误: " + e.getMessage());
            log.error("搜索过程发生错误: {}", e.getMessage());
            e.printStackTrace();
        }
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
        System.out.println("找到 " + searchItems.size() + " 个搜索结果项");
        
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
                
            } catch (Exception e) {
                log.error("解析动漫项时出错: {}", e.getMessage());
                System.out.println("解析动漫项时出错: " + e.getMessage());
            }
        }
        
        return animeList;
    }
}
