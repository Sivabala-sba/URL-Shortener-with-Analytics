package com.example.UrlShortener.dto;

import java.util.List;

public record OverviewResponse(long totalLinks,
                               long activeLinks,
                               long expiredLinks,
                               long disabledLinks,
                               long totalClicks,
                               long clicksLast24h,
                               List<TopLink> topLinks) {

    public record TopLink(String code, String shortUrl, String url, long clicks, String status){
    }
}
