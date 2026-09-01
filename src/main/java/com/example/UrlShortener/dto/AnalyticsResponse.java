package com.example.UrlShortener.dto;

import java.time.Instant;
import java.util.List;

public record AnalyticsResponse(String code,
                                String shortUrl,
                                String url,
                                Instant from,
                                Instant to,
                                String granularity,
                                Totals totals,
                                List<TimeBucket> timeline,
                                List<LabelCount> referrers,
                                List<CountryCount> countries,
                                List<LabelCount> devices,
                                List<RecentClick> recentClicks) {

    public record Totals(long clicks, long visitors, long lifetimeClicks){
    }

    public record TimeBucket(String bucket, long clicks, long visitors){
    }

    public record LabelCount(String label, long clicks, double share){
    }

    public record CountryCount(String countryCode, String country, long clicks, double share){
    }

    public record RecentClick(Instant at,
                              String country,
                              String city,
                              String referrer,
                              String device){
    }
}
