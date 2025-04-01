package com.ligg.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 视频链接解析服务
 */
@Service
public class VideoUrlParser {

    /**
     * 解析视频页面中的视频链接
     *
     * @param videoPageUrl 视频页面URL
     * @return 解析到的视频链接列表
     * @throws Exception 解析过程中发生的异常
     */
    public List<String> parseVideoUrls(String videoPageUrl) throws Exception {
        Set<String> videoUrls = new HashSet<>();
        
        // 设置HTTP请求参数
        int maxRetries = 3;                // 最大重试次数
        int connectionTimeout = 600000;    // 连接超时时间
        Document searchPage = null;
        Exception lastException = null;
        
        // 重试机制
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("尝试第" + attempt + "次请求...");
                searchPage = Jsoup.connect(videoPageUrl)
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
                throw new RuntimeException("无法获取页面，所有重试均失败");
            }
        }

        // 查找包含player_aaaa变量的script标签
        String scriptContent = searchPage.select("script").stream()
            .filter(script -> script.data().contains("player_aaaa"))
            .findFirst()
            .map(Element::data)
            .orElse("");

        // 提取带有mp4、mkv或m3u8扩展名的URL
        Pattern pattern = Pattern.compile("https?:\\/\\/[^\"]+\\.(mp4|mkv|m3u8)");
        Matcher matcher = pattern.matcher(scriptContent);

        while (matcher.find()) {
            String url = matcher.group();
            // 去除转义字符
            url = url.replaceAll("\\\\", "");
            videoUrls.add(url);
        }

        // 如果以上模式没有找到URL，尝试提取所有url字段
        if (videoUrls.isEmpty()) {
            Pattern urlPattern = Pattern.compile("\\\"url\\\":\\\"(.*?)\\\"");
            Matcher urlMatcher = urlPattern.matcher(scriptContent);

            while (urlMatcher.find()) {
                String url = urlMatcher.group(1);
                // 去除转义字符
                url = url.replaceAll("\\\\", "");
                videoUrls.add(url);
            }
        }

        return new ArrayList<>(videoUrls);
    }
} 