package com.ligg;

import com.ligg.service.anime.VideoUrlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PlayVideoTest {

    private static final String VIDEO_DATA_URL = "https://dm1.xfdm.pro/watch/2660/1/1.html";
    
    @Autowired
    private VideoUrlParserService videoUrlParserService;
    
    @Test
    public void GetPlayVideoUrlTest() {
        try {
            List<String> videoUrls = videoUrlParserService.parseVideoUrls(VIDEO_DATA_URL);
            
            System.out.println("找到视频链接数量: " + videoUrls.size());
            for (String url : videoUrls) {
                System.out.println("视频链接: " + url);
            }
            
            if (videoUrls.isEmpty()) {
                System.out.println("未找到视频链接");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
