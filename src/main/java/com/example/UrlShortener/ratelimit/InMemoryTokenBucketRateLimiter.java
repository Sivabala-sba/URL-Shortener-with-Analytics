package com.example.UrlShortener.ratelimit;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

public class InMemoryTokenBucketRateLimiter implements RateLimiter{
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final LongSupplier nanoClock;
    private final int maxBuckets;

    public InMemoryTokenBucketRateLimiter(){
        this(System::nanoTime, 500_000);
    }

    public InMemoryTokenBucketRateLimiter(LongSupplier nanoClock, int maxBuckets){
        this.nanoClock = nanoClock;
        this.maxBuckets = maxBuckets;
    }

    @Override
    public RateLimitDecision tryConsume(String key, RateLimitPolicy policy, long tokens){
        String bucketKey = policy.name() + '|' + key;
        TokenBucket bucket = buckets.get(bucketKey);
        if(bucket == null){
            if(buckets.size() >= maxBuckets){
                return RateLimitDecision.allowed(policy.capacity(), policy.capacity() - tokens, Duration.ZERO);
            }
            bucket = buckets.computeIfAbsent(bucketKey, k -> new TokenBucket(policy, nanoClock));
        }
        return bucket.tryConsume(tokens);
    }

    public int evictIdle(){
        long now = nanoClock.getAsLong();
        int removed = 0;
        for(Iterator<Map.Entry<String, TokenBucket>> it = buckets.entrySet().iterator(); it.hasNext(); ){
            Map.Entry<String, TokenBucket> entry = it.next();
            TokenBucket bucket = entry.getValue();
            if(bucket.isEvictable(now, bucket.policy().idleLifetime().toNanos())){
                it.remove();
                removed++;
            }
        }
        return removed;
    }

    public int trackedBuckets(){
        return buckets.size();
    }
}
