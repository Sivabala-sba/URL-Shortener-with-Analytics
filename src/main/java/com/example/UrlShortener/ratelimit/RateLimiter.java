package com.example.UrlShortener.ratelimit;

public interface RateLimiter {

    RateLimitDecision tryConsume(String key, RateLimitPolicy policy, long tokens);

    default RateLimitDecision tryConsume(String key, RateLimitPolicy policy){
        return tryConsume(key, policy, 1L);
    }
}
