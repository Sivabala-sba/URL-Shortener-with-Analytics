package com.example.UrlShortener.ratelimit;

import java.time.Duration;
import java.util.function.LongSupplier;

public final class TokenBucket {

    private final RateLimitPolicy policy;
    private final LongSupplier nanoClock;

    private long tokens;
    private long lastRefillNanos;
    private long lastAccessNanos;

    public TokenBucket(RateLimitPolicy policy, LongSupplier nanoClock){
        this.policy = policy;
        this.nanoClock = nanoClock;
        long now = nanoClock.getAsLong();
        this.tokens = policy.capacity();
        this.lastRefillNanos = now;
        this.lastAccessNanos = now;
    }

    public synchronized RateLimitDecision tryConsume(long requested){
        if(requested < 1){
            throw new IllegalArgumentException("requested must be at least 1");
        }
        if(requested > policy.capacity()){
            throw new IllegalArgumentException("requested " + " exceeds the capacity of policy " + policy.name());
        }

        long now = nanoClock.getAsLong();
        lastAccessNanos = now;
        refill(now);

        if(tokens >= requested){
            tokens -= requested;
            return RateLimitDecision.allowed(policy.capacity(), tokens, resetAfter(now));
        }

        long deficit = requested - tokens;
        long waitNanos = deficit * policy.nanosPerToken() - sinceLastRefill(now);
        return RateLimitDecision.denied(policy.capacity(), Duration.ofNanos(Math.max(0L, waitNanos)), resetAfter(now));
    }

    private void refill(long now){
        long elapsed = sinceLastRefill(now);
        if(elapsed <= 0){
            if(elapsed < 0){
                lastRefillNanos = now;
            }
            return;
        }

        long nanosPerToken = policy.nanosPerToken();
        long accrued = elapsed / nanosPerToken;
        if(accrued <= 0){
            return;
        }

        long headroom = policy.capacity() - tokens;
        if(accrued >= headroom){
            tokens = policy.capacity();
            lastRefillNanos = now;
            return;
        }

        tokens += accrued;
        lastRefillNanos += accrued * nanosPerToken;
    }

    private Duration resetAfter(long now){
        long headroom = policy.capacity() - tokens;
        if(headroom <= 0){
            return Duration.ZERO;
        }
        long nanos = headroom * policy.nanosPerToken() - sinceLastRefill(now);
        return Duration.ofNanos(Math.max(0L, nanos));
    }

    private long sinceLastRefill(long now){
        return now - lastRefillNanos;
    }

    public synchronized boolean isEvictable(long now, long idleNanos){
        refill(now);
        return tokens >= policy.capacity() && (now - lastAccessNanos) >= idleNanos;
    }

    public synchronized long availableTokens(){
        refill(nanoClock.getAsLong());
        return tokens;
    }

    public RateLimitPolicy policy(){
        return policy;
    }
}
