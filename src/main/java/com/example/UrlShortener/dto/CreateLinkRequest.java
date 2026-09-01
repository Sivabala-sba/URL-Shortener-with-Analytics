package com.example.UrlShortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateLinkRequest(

        @NotBlank(message = "url is required")
        @Size(max = 2048, message = "url must be at most 2048 characters")
        String url,

        @Pattern(regexp = "^[A-Za-z0-9_-]{4,32}$",
                message = "alias may contain letters, digits, hyphen and underscore, 4 to 32 characters")
        String alias,

        Instant expiresAt,

        @Positive(message = "ttlSeconds must be positive")
        Long ttlSeconds,

        Boolean neverExpires) {

    public boolean isNeverExpires(){
        return Boolean.TRUE.equals(neverExpires);
    }

    public boolean hasAlias(){
        return alias != null && !alias.isBlank();
    }
}
