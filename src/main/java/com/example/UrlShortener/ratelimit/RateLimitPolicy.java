package com.example.UrlShortener.ratelimit;

import java.time.Duration;

public record RateLimitPolicy(String name, long capacity, long refillTokens, Duration refillPeriod) {

    private static final long MAX_CAPACITY = 1_000_000_000L;

    public RateLimitPolicy{
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("policy name is required");
        }
        if(capacity < 1 || capacity > MAX_CAPACITY){
            throw new IllegalArgumentException("capacity must be within 1.." + MAX_CAPACITY);
        }
        if(refillTokens < 1){
            throw new IllegalArgumentException("refillTokens must be at least 1");
        }
        if(refillPeriod == null || refillPeriod.isZero() || refillPeriod.isNegative()){
            throw new IllegalArgumentException("refillPeriod must be positive");
        }
    }

    public long nanosPerToken(){
        return Math.max(1L, refillPeriod.toNanos() / refillTokens);
    }

    public long microsPerToken(){
        return Math.max(1L, refillPeriod.toNanos() / 1_000L / refillTokens);
    }

    public Duration timeToFill(){
        return Duration.ofNanos(capacity * nanosPerToken());
    }

    public Duration idleLifetime(){
        return timeToFill().plusSeconds(60);
    }
}
