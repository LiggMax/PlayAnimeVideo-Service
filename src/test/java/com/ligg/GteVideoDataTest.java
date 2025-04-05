package com.ligg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest
public class GteVideoDataTest {

    private static final String KEYWORD = "败犬女主";

    private static final String XFDM_URL = "https://dm1.xfdm.pro";

    private static final String BASE_URL = "https://dm1.xfdm.pro/search.html?wd=";

//    @Test
//    public void DDOS() throws InterruptedException {
//        int requestsPerSecond = 100000; // 每秒请求数
//        int durationSeconds = 30; // 持续时间（秒）
//        ExecutorService executorService = Executors.newFixedThreadPool(50); // 创建线程池
//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 创建调度器
//        AtomicInteger successCount = new AtomicInteger(0); // 成功计数
//        AtomicInteger failureCount = new AtomicInteger(0); // 失败计数
//        AtomicInteger validResponseCount = new AtomicInteger(0); // 有效响应计数
//        AtomicLong totalResponseTime = new AtomicLong(0); // 总响应时间
//
//        System.out.println("开始发送请求，每秒" + requestsPerSecond + "个请求，持续" + durationSeconds + "秒");
//
//        // 创建定时任务，每秒执行一次
//        ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
//            for (int i = 0; i < requestsPerSecond; i++) {
//                CompletableFuture.runAsync(() -> {
//                    long startTime = System.currentTimeMillis();
//                    try {
//                        Document document = Jsoup.connect("\n" +
//                                        "https://dm1.xfdm.pro/search.html?wd=" + KEYWORD)
//                                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
//                                .timeout(5000)
//                                .get();
//
//                        long responseTime = System.currentTimeMillis() - startTime;
//                        totalResponseTime.addAndGet(responseTime);
//
//                        // 检查响应内容
//                        String title = document.title();
//                        String content = document.body().text();
//
//                        // 验证响应内容是否有效（这里可以根据实际需求修改验证条件）
//                        boolean isValidResponse = title != null && !title.isEmpty() &&
//                                                content != null && !content.isEmpty() &&
//                                                content.length() > 100; // 确保内容不为空且有一定长度
//
//                        if (isValidResponse) {
//                            validResponseCount.incrementAndGet();
//                            successCount.incrementAndGet();
//                            System.out.println("请求成功: " + Thread.currentThread().getName() +
//                                             ", 响应时间: " + responseTime + "ms" +
//                                             ", 成功数: " + successCount.get() +
//                                             ", 有效响应数: " + validResponseCount.get() +
//                                             ", 失败数: " + failureCount.get());
//                        } else {
//                            failureCount.incrementAndGet();
//                            System.out.println("无效响应: " + Thread.currentThread().getName() +
//                                             ", 响应时间: " + responseTime + "ms" +
//                                             ", 成功数: " + successCount.get() +
//                                             ", 有效响应数: " + validResponseCount.get() +
//                                             ", 失败数: " + failureCount.get());
//                        }
//                    } catch (Exception e) {
//                        long responseTime = System.currentTimeMillis() - startTime;
//                        failureCount.incrementAndGet();
//                        System.out.println("请求失败: " + Thread.currentThread().getName() +
//                                         ", 响应时间: " + responseTime + "ms" +
//                                         ", 错误: " + e.getMessage() +
//                                         ", 成功数: " + successCount.get() +
//                                         ", 有效响应数: " + validResponseCount.get() +
//                                         ", 失败数: " + failureCount.get());
//                    }
//                }, executorService);
//            }
//        }, 0, 1, TimeUnit.SECONDS);
//
//        // 等待指定时间后停止
//        Thread.sleep(durationSeconds * 1000L);
//        scheduledFuture.cancel(false);
//        scheduler.shutdown();
//        executorService.shutdown();
//
//        // 等待所有任务完成
//        scheduler.awaitTermination(5, TimeUnit.SECONDS);
//        executorService.awaitTermination(5, TimeUnit.SECONDS);
//
//        // 计算统计数据
//        int totalRequests = successCount.get() + failureCount.get();
//        double avgResponseTime = totalRequests > 0 ?
//            (double) totalResponseTime.get() / totalRequests : 0;
//        double successRate = totalRequests > 0 ?
//            (double) successCount.get() / totalRequests * 100 : 0;
//        double validResponseRate = totalRequests > 0 ?
//            (double) validResponseCount.get() / totalRequests * 100 : 0;
//
//        System.out.println("\n========== 测试统计 ==========");
//        System.out.println("总请求数: " + totalRequests);
//        System.out.println("成功请求数: " + successCount.get());
//        System.out.println("有效响应数: " + validResponseCount.get());
//        System.out.println("失败请求数: " + failureCount.get());
//        System.out.println("平均响应时间: " + String.format("%.2f", avgResponseTime) + "ms");
//        System.out.println("成功率: " + String.format("%.2f", successRate) + "%");
//        System.out.println("有效响应率: " + String.format("%.2f", validResponseRate) + "%");
//        System.out.println("==============================\n");
//    }

    @Test
    public void searchVideoTest() {
        // 设置HTTP请求参数
        int maxRetries = 3;                // 最大重试次数
        int connectionTimeout = 600000;    // 连接超时时间(毫秒)
        int readTimeout = 300000;          // 读取超时时间(毫秒)

        try {
            System.out.println("开始搜索动漫: " + KEYWORD + "超时时间：" + connectionTimeout);

            // 使用Jsoup的connect方法代替parse(URL)，提供更多配置选项
            Document searchPage = null;
            Exception lastException = null;

            // 实现重试逻辑
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

            System.out.println("成功获取搜索页面，开始解析结果");
            Elements playButtons = searchPage.select("a.button.cr3");
            if (playButtons.isEmpty()) {
                System.out.println("没有找到播放按钮，可能搜索结果为空或页面结构已变化");
                return;
            }

            // 查找"播放正片"链接并获取详情页
            for (Element element : playButtons) {
                String href = element.attr("href");
                String text = element.text();

                // 筛选播放正片文本的链接
                if (text.contains("正片")) {
                    System.out.println("找到播放链接: " + href);
                    String detailUrl = XFDM_URL + href;

                    // 获取并解析详情页面
                    Document detailPage = null;

                    // 对详情页面的请求也实现重试
                    for (int attempt = 1; attempt <= maxRetries; attempt++) {
                        try {
                            System.out.println("尝试第" + attempt + "次请求详情页面: " + detailUrl);
                            detailPage = Jsoup.connect(detailUrl)
                                    .timeout(readTimeout)  // 详情页可能较大，使用更长的超时时间
                                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                                    .header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                                    .header("Referer", BASE_URL + KEYWORD) // 添加Referer头，模拟正常浏览行为
                                    .followRedirects(true)
                                    .get();

                            // 成功获取详情页面
                            break;
                        } catch (Exception e) {
                            System.out.println("第" + attempt + "次获取详情页失败: " + e.getMessage());

                            if (attempt < maxRetries) {
                                int waitTime = 3000 * attempt;
                                System.out.println("等待" + waitTime + "毫秒后重试...");
                                Thread.sleep(waitTime);
                            } else {
                                System.out.println("获取详情页面失败，已达最大重试次数");
                                throw e;
                            }
                        }
                    }

                    if (detailPage == null) {
                        System.out.println("无法获取详情页面，跳过解析");
                        continue;
                    }

                    // 解析详情页内容
                    Element body = detailPage.body();

                    // 1. 提取基本信息
                    String title = body.select(".slide-info-title").text();
                    String summary = body.select("#height_limit").text();

                    System.out.println("\n========== 动漫详情 ==========");
                    System.out.println("动漫标题: " + title);
                    System.out.println("简介: " + summary);
                    System.out.println("==============================\n");

                    // 2. 获取所有线路和剧集信息
                    Elements routeTabs = body.select(".anthology-tab .swiper-wrapper a.swiper-slide");
                    Elements episodeLists = body.select(".anthology-list-box");

                    System.out.println("找到" + routeTabs.size() + "条线路");

                    // 遍历所有线路及其剧集
                    for (int i = 0; i < routeTabs.size(); i++) {
                        Element routeTab = routeTabs.get(i);
                        String routeName = routeTab.ownText().trim();
                        String episodeCount = routeTab.select(".badge").text();

                        System.out.println("\n线路" + (i + 1) + ": " + routeName + " (共" + episodeCount + "集)");

                        // 获取对应线路的剧集列表
                        if (i < episodeLists.size()) {
                            Element episodeList = episodeLists.get(i);
                            Elements episodes = episodeList.select("ul.anthology-list-play li a");

                            System.out.println("该线路包含" + episodes.size() + "个剧集");

                            // 输出所有剧集
                            for (int j = 0; j < episodes.size(); j++) {
                                Element episode = episodes.get(j);
                                String episodeName = episode.text();
                                String episodeUrl = episode.attr("href");

                                // 确保URL是完整的
                                if (!episodeUrl.startsWith("http")) {
                                    episodeUrl = XFDM_URL + episodeUrl;
                                }

                                System.out.println("  - " + episodeName + ": " + episodeUrl);
                            }
                        }
                    }

                    // 已找到并处理完一个结果，结束搜索
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("\n搜索过程发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
