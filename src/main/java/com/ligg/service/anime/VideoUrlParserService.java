package com.ligg.service.anime;

import java.util.List;

public interface VideoUrlParserService {

    List<String> parseVideoUrls(String videoPageUrl);
}
