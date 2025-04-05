package com.ligg.Banguni;

import com.ligg.service.bangumi.BangumiService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class SearchTest {

    @Autowired
    private BangumiService bangumiService;

    @Test
    public void testSearch() {
        // 定义要搜索的关键词
        String keyword = "败犬女主";
        try {
            // 创建一个URL对象，用于构建搜索请求的URL
            URL url = new URL("https://api.bgm.tv/search/subject/" + keyword);
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为GET
            connection.setRequestMethod("GET");

            // 使用BufferedReader读取连接的输入流
            BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
            String output;
            StringBuilder response = new StringBuilder();

            // 读取并拼接响应数据
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            // 断开连接
            connection.disconnect();

            // 处理返回的JSON数据
            System.out.println(response);

        } catch (IOException e) {
            // 记录搜索过程中发生的错误
            log.error("搜索动漫过程中发生错误", e);
        }
    }

    @Test
    public void testSearch2() {
        // 测试使用不同的关键词
        String keyword = "败犬女主";
        Map result = bangumiService.getBangumiSearchList(keyword);
        System.out.println(result);
    }
}
