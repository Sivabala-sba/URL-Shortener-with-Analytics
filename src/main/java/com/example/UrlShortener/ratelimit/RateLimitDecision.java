package com.example.UrlShortener.ratelimit;

import java.time.Duration;

public record RateLimitDecision(boolean allowed,
                                long limit,
                                long remaining,
                                Duration retryAfter,
                                Duration resetAfter) {

    public static RateLimitDecision allowed(long limit, long remaining, Duration resetAfter){
        return new RateLimitDecision(true, limit, remaining, Duration.ZERO, resetAfter);
    }

    public static RateLimitDecision denied(long limit, Duration retryAfter, Duration resetAfter){
        return new RateLimitDecision(false, limit, 0, retryAfter, resetAfter);
    }

    public long retryAfterSeconds(){
        if(retryAfter.isZero() || retryAfter.isNegative()){
            return 0;
        }
        return Math.max(1L, (retryAfter.toMillis() + 999) / 1000);
    }

    public long resetAfterSeconds(){
        return Math.max(0L, (resetAfter.toMillis() + 999) / 1000);
    }
}
