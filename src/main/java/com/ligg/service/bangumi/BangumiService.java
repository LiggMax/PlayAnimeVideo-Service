package com.ligg.service.bangumi;

import org.jsoup.nodes.Element;
import org.springframework.http.ResponseEntity;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BangumiService {


    Map<String,Object> getBangumiSearchList(String keywords);
}
