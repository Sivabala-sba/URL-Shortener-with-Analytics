package com.example.UrlShortener.dto;

import com.example.UrlShortener.domain.LinkStatus;
import com.example.UrlShortener.domain.ShortUrl;

import java.time.Instant;

public record LinkResponse(String code,
                           String shortUrl,
                           String url,
                           LinkStatus status,
                           boolean customAlias,
                           long clicks,
                           Instant createdAt,
                           Instant expiresAt,
                           Instant lastAccessedAt) {

    public static LinkResponse of(ShortUrl link, String baseUrl) {
        return new LinkResponse();
    }
}
