package com.ligg.Banguni;

import com.ligg.service.bangumi.BangumiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class SearchTest {

    @Autowired
    private BangumiService bangumiService;


    /**
     * 测试搜索功能
     */
    @Test
    public void testSearch() {
        // 测试使用不同的关键词
        String keyword = "败犬女主";
        Map<String, Object> result = bangumiService.getBangumiSearchList(keyword);
        System.out.println(result);
    }

    /**
     * 测试详情功能
     */
    @Test
    public void testDetail() {
        // 测试使用不同的关键词
        Integer id = 464376;
        Map<String, Object> result = bangumiService.getBangumiDetail(id);
        System.out.println(result);
    }

    @Test
    public void testCharacter() {
        // 测试使用不同的关键词
        Integer id = 464376;
        List<Map<String, Object>> result = bangumiService.getBangumiCharacter(id);
        System.out.println(result);
    }
}
