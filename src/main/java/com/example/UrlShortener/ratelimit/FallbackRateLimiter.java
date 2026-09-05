package com.example.UrlShortener.ratelimit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public class FallbackRateLimiter implements RateLimiter {

    private static final Logger log = LoggerFactory.getLogger(FallbackRateLimiter.class);
    private static final long LOG_INTERVAL_MILLIS = 30_000L;

    private final RateLimiter primary;
    private final InMemoryTokenBucketRateLimiter fallback;
    private final boolean failOpen;

    private final AtomicLong degradedCalls = new AtomicLong();
    private final AtomicLong lastLoggedAt = new AtomicLong();

    public FallbackRateLimiter(RateLimiter primary,
                               InMemoryTokenBucketRateLimiter fallback,
                               boolean failOpen){
        this.primary = primary;
        this.fallback = fallback;
        this.failOpen = failOpen;
    }

    @Override
    public RateLimitDecision tryConsume(String key, RateLimitPolicy policy, long tokens){
        try{
            return primary.tryConsume(key, policy, tokens);
        }catch (IllegalArgumentException e){
            throw e;
        }catch (RuntimeException e){
            noteDegraded(e);
            if(failOpen){
                return fallback.tryConsume(key, policy, tokens);
            }
            return RateLimitDecision.denied(policy.capacity(), Duration.ofSeconds(1), policy.timeToFill());
        }
    }

    private void noteDegraded(RuntimeException cause){
        long total = degradedCalls.incrementAndGet();
        long now = System.currentTimeMillis();
        long last = lastLoggedAt.get();
        if(now - last >= LOG_INTERVAL_MILLIS && lastLoggedAt.compareAndSet(last, now)){
            log.warn("Rate limiter falling back to in-memory buckets ({} calls degraded so far): {}", total, cause.toString());
        }
    }

    public long degradedCalls(){
        return degradedCalls.get();
    }

    public int fallbackBuckets(){
        return fallback.trackedBuckets();
    }
}
