package com.ligg.service.anime.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ligg.service.anime.AnimeService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnimeServiceImpl implements AnimeService {
    private static final Logger log = LoggerFactory.getLogger(AnimeServiceImpl.class);

    private static final String XFDM_URL = "https://dm1.xfdm.pro";
    private static final String XFDM_SEARCH_URL = "https://dm1.xfdm.pro/search.html?wd=";
    
    // HTTP请求参数
    private static final int MAX_RETRIES = 3;               // 最大重试次数
    private static final int CONNECTION_TIMEOUT = 100000;   // 连接超时时间(毫秒)
    private static final int READ_TIMEOUT = 300000;         // 读取超时时间(毫秒)
    
    @Override
    public Map<String, Object> searchAnime(String keyword) {
        log.info("开始搜索动漫: {}", keyword);
        System.out.println("开始搜索动漫: " + keyword);
        
        try {
            // 搜索动漫并获取详情页URL
            String detailUrl = searchAnimeAndGetDetailUrl(keyword);
            if (detailUrl == null) {
                log.warn("未找到动漫: {}", keyword);
                return Collections.emptyMap();
            }
            
            // 获取详情页内容
            Document detailPage = fetchDetailPage(detailUrl, keyword);
            if (detailPage == null) {
                log.warn("无法获取详情页面: {}", detailUrl);
                return Collections.emptyMap();
            }
            
            // 解析详情页并返回结构化数据
            Map<String, Object> result = parseDetailPage(detailPage);
            
            // 打印信息
            System.out.println("\n========== 动漫详情 ==========");
            System.out.println("标题: " + result.get("title"));
            System.out.println("简介: " + result.get("summary"));
            
            return result;
            
        } catch (Exception e) {
            log.error("搜索动漫过程中发生错误", e);
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    /**
     * 搜索动漫并返回详情页URL
     */
    private String searchAnimeAndGetDetailUrl(String keyword) throws Exception {
        // 使用Jsoup的connect方法进行搜索
        Document searchPage = null;
        Exception lastException = null;
        
        // 实现重试逻辑
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.println("尝试第" + attempt + "次请求搜索页面...");
                searchPage = Jsoup.connect(XFDM_SEARCH_URL + keyword)
                        .timeout(CONNECTION_TIMEOUT)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                        .followRedirects(true)
                        .get();
                
                // 成功获取页面，跳出重试循环
                break;
            } catch (Exception e) {
                lastException = e;
                System.out.println("第" + attempt + "次请求失败: " + e.getMessage());
                
                if (attempt < MAX_RETRIES) {
                    int waitTime = 2000 * attempt;
                    System.out.println("等待" + waitTime + "毫秒后重试...");
                    Thread.sleep(waitTime);
                } else {
                    throw e; // 重试耗尽，抛出异常
                }
            }
        }
        
        if (searchPage == null) {
            if (lastException != null) {
                throw lastException;
            }
            return null;
        }

        // 查找播放按钮
        Elements playButtons = searchPage.select("a.button.cr3");
        if (playButtons.isEmpty()) {
            System.out.println("没有找到播放按钮，可能搜索结果为空或页面结构已变化");
            return null;
        }
        
        // 查找"播放正片"链接
        for (Element element : playButtons) {
            String href = element.attr("href");
            String text = element.text();
            
            // 筛选播放正片文本的链接
            if (text.contains("正片")) {
                System.out.println("找到播放链接: " + href);
                // 返回详情页的完整URL
                return XFDM_URL + href;
            }
        }
        
        return null;
    }
    
    /**
     * 获取详情页面
     */
    private Document fetchDetailPage(String detailUrl, String referer) throws Exception {
        Document detailPage = null;
        
        // 对详情页面的请求实现重试
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.println("尝试第" + attempt + "次请求详情页面: " + detailUrl);
                
                // 构建连接请求
                var connection = Jsoup.connect(detailUrl)
                        .timeout(READ_TIMEOUT)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                        .followRedirects(true);
                
                // 如果有referer，添加referer头
                if (referer != null) {
                    connection.header("Referer", XFDM_SEARCH_URL + referer);
                }
                
                detailPage = connection.get();
                
                // 成功获取详情页面
                break;
            } catch (Exception e) {
                System.out.println("第" + attempt + "次获取详情页失败: " + e.getMessage());
                
                if (attempt < MAX_RETRIES) {
                    int waitTime = 3000 * attempt;
                    System.out.println("等待" + waitTime + "毫秒后重试...");
                    Thread.sleep(waitTime);
                } else {
                    throw e;
                }
            }
        }
        
        return detailPage;
    }
    
    /**
     * 解析详情页内容，提取结构化数据
     */
    private Map<String, Object> parseDetailPage(Document detailPage) {
        Map<String, Object> result = new HashMap<>();
        Element body = detailPage.body();
        
        // 1. 提取基本信息
        String title = body.select(".slide-info-title").text();
        String summary = body.select("#height_limit").text();
        String coverImg = body.select(".detail-pic img").attr("data-src");
        
        result.put("title", title);
        result.put("summary", summary);
        result.put("cover", coverImg);
        
        // 2. 提取分类信息
        Elements categoryElements = body.select(".slide-info:has(.cor6:contains(类型)) a");
        List<String> categories = new ArrayList<>();
        for (Element element : categoryElements) {
            categories.add(element.text());
        }
        result.put("categories", categories);
        
        // 3. 解析线路和剧集
        List<Map<String, Object>> routes = parseEpisodes(detailPage);
        result.put("episodes", routes);
        
        // 打印线路和剧集信息
        System.out.println("\n========== 剧集信息 ==========");
        System.out.println("找到" + routes.size() + "条线路");
        
        for (int i = 0; i < routes.size(); i++) {
            Map<String, Object> route = routes.get(i);
            String routeName = (String) route.get("name");
            String episodeCount = (String) route.get("count");
            
            System.out.println("\n线路" + (i+1) + ": " + routeName + " (共" + episodeCount + "集)");
            
            List<Map<String, String>> routeEpisodes = (List<Map<String, String>>) route.get("episodes");
            System.out.println("该线路包含" + routeEpisodes.size() + "个剧集");
            
            // 只打印前5个剧集，避免输出太多
            int displayLimit = Math.min(5, routeEpisodes.size());
            for (int j = 0; j < displayLimit; j++) {
                Map<String, String> episode = routeEpisodes.get(j);
                System.out.println("  - " + episode.get("name") + ": " + episode.get("url"));
            }
            
            if (routeEpisodes.size() > displayLimit) {
                System.out.println("  ... 省略" + (routeEpisodes.size() - displayLimit) + "个剧集 ...");
            }
        }
        
        return result;
    }
    
    /**
     * 解析剧集信息
     */
    private List<Map<String, Object>> parseEpisodes(Document detailPage) {
        List<Map<String, Object>> routes = new ArrayList<>();
        Element body = detailPage.body();
        
        // 获取所有线路和剧集信息
        Elements routeTabs = body.select(".anthology-tab .swiper-wrapper a.swiper-slide");
        Elements episodeLists = body.select(".anthology-list-box");
        
        // 遍历所有线路及其剧集
        for (int i = 0; i < routeTabs.size(); i++) {
            Element routeTab = routeTabs.get(i);
            String routeName = routeTab.ownText().trim();
            String episodeCount = routeTab.select(".badge").text();
            
            Map<String, Object> route = new HashMap<>();
            route.put("name", routeName);
            route.put("count", episodeCount);
            
            // 获取对应线路的剧集列表
            List<Map<String, String>> episodesList = new ArrayList<>();
            if (i < episodeLists.size()) {
                Element episodeList = episodeLists.get(i);
                Elements episodes = episodeList.select("ul.anthology-list-play li a");
                
                // 解析所有剧集
                for (Element episode : episodes) {
                    String episodeName = episode.text();
                    String episodeUrl = episode.attr("href");
                    
                    // 确保URL是完整的
                    if (!episodeUrl.startsWith("http")) {
                        episodeUrl = XFDM_URL + episodeUrl;
                    }
                    
                    Map<String, String> episodeMap = new HashMap<>();
                    episodeMap.put("name", episodeName);
                    episodeMap.put("url", episodeUrl);
                    
                    episodesList.add(episodeMap);
                }
            }
            
            route.put("episodes", episodesList);
            routes.add(route);
        }
        
        return routes;
    }

    @Override
    public String getPlayVideoUrl(String url) {
        log.info("开始获取视频播放地址: {}", url);

        try {
            // 设置连接选项
            Document document = Jsoup.connect(url)
                    .timeout(READ_TIMEOUT)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                    .followRedirects(true)
                    .get();
            
            // 查找所有script标签
            Elements scriptElements = document.select("script");
            String videoUrl = null;
            
            // 遍历所有script标签，查找包含播放信息的脚本
            for (Element script : scriptElements) {
                String scriptContent = script.html();
                // 查找播放器配置的脚本，包含var player_aaaa的脚本
                if (scriptContent.contains("var player_aaaa")) {
                    System.out.println("找到播放器配置脚本");
                    
                    // 提取播放地址 - 方法1：使用正则表达式
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"url\":\"(.*?)\"");
                    java.util.regex.Matcher matcher = pattern.matcher(scriptContent);
                    if (matcher.find()) {
                        videoUrl = matcher.group(1);
                        // 处理转义字符
                        videoUrl = videoUrl.replace("\\/", "/");
                    }
                    
                    // 方法2：提取JSON对象 (备用方法)
                    if (videoUrl == null || videoUrl.isEmpty()) {
                        int startIndex = scriptContent.indexOf("var player_aaaa=") + "var player_aaaa=".length();
                        int endIndex = scriptContent.indexOf("</script>", startIndex);
                        if (endIndex == -1) {
                            endIndex = scriptContent.length();
                        }
                        
                        String jsonStr = scriptContent.substring(startIndex, endIndex).trim();
                        
                        try {
                            // 尝试解析JSON以获取更多信息
                            // 需要处理JSON字符串，移除可能的尾部分号
                            if (jsonStr.endsWith(";")) {
                                jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
                            }
                            
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode rootNode = mapper.readTree(jsonStr);
                            
                            // 获取视频URL
                            videoUrl = rootNode.path("url").asText();
                        } catch (Exception e) {
                            log.error("JSON解析失败: {}", e.getMessage());
                        }
                    }
                    
                    // 找到后退出循环
                    break;
                }
            }
            
            if (videoUrl != null && !videoUrl.isEmpty()) {
                log.info("成功提取视频播放地址: {}", videoUrl);
                return videoUrl;
            } else {
                log.warn("未能提取到视频播放地址");
                return "";
            }
            
        } catch (Exception e) {
            log.error("获取视频播放地址失败: {}", e.getMessage());
            System.out.println("获取视频播放地址失败: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}
