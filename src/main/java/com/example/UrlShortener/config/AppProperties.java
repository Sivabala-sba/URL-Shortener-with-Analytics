package com.example.UrlShortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String baseUrl = "http://localhost:8080";

    private boolean trustProxyHeaders = true;

    private final Shortener shortener = new Shortener();
    private final Cache cache = new Cache();
    private final RateLimit rateLimit = new RateLimit();
    private final Geo geo = new Geo();
    private final Privacy privacy = new Privacy();
    private final Cleanup cleanup = new Cleanup();
    private final Analytics analytics = new Analytics();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl == null ? null : baseUrl.replaceAll("/+$", "");
    }

    public boolean isTrustProxyHeaders() {
        return trustProxyHeaders;
    }

    public void setTrustProxyHeaders(boolean trustProxyHeaders) {
        this.trustProxyHeaders = trustProxyHeaders;
    }

    public Shortener getShortener() {
        return shortener;
    }

    public Cache getCache() {
        return cache;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public Geo getGeo() {
        return geo;
    }

    public Privacy getPrivacy() {
        return privacy;
    }

    public Cleanup getCleanup() {
        return cleanup;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    // -----------------------------------------------------------------------

    public static class Shortener {

        private int codeLength = 7;

        private int idBlockSize = 50;

        private Duration defaultTtl = Duration.ofDays(30);

        private Duration maxTtl = Duration.ofDays(365);

        private int maxUrlLength = 2048;
        private int minAliasLength = 4;
        private int maxAliasLength = 32;

        private List<String> reservedCodes = new ArrayList<>(List.of(
                "api", "dashboard", "actuator", "health", "metrics", "static",
                "assets", "login", "admin", "favicon.ico", "robots.txt"));

        private List<String> allowedSchemes = new ArrayList<>(List.of("http", "https"));

        private boolean blockPrivateDestinations = true;

        public int getCodeLength() {
            return codeLength;
        }

        public void setCodeLength(int codeLength) {
            this.codeLength = codeLength;
        }

        public int getIdBlockSize() {
            return idBlockSize;
        }

        public void setIdBlockSize(int idBlockSize) {
            this.idBlockSize = idBlockSize;
        }

        public Duration getDefaultTtl() {
            return defaultTtl;
        }

        public void setDefaultTtl(Duration defaultTtl) {
            this.defaultTtl = defaultTtl;
        }

        public Duration getMaxTtl() {
            return maxTtl;
        }

        public void setMaxTtl(Duration maxTtl) {
            this.maxTtl = maxTtl;
        }

        public int getMaxUrlLength() {
            return maxUrlLength;
        }

        public void setMaxUrlLength(int maxUrlLength) {
            this.maxUrlLength = maxUrlLength;
        }

        public int getMinAliasLength() {
            return minAliasLength;
        }

        public void setMinAliasLength(int minAliasLength) {
            this.minAliasLength = minAliasLength;
        }

        public int getMaxAliasLength() {
            return maxAliasLength;
        }

        public void setMaxAliasLength(int maxAliasLength) {
            this.maxAliasLength = maxAliasLength;
        }

        public List<String> getReservedCodes() {
            return reservedCodes;
        }

        public void setReservedCodes(List<String> reservedCodes) {
            this.reservedCodes = reservedCodes;
        }

        public List<String> getAllowedSchemes() {
            return allowedSchemes;
        }

        public void setAllowedSchemes(List<String> allowedSchemes) {
            this.allowedSchemes = allowedSchemes;
        }

        public boolean isBlockPrivateDestinations() {
            return blockPrivateDestinations;
        }

        public void setBlockPrivateDestinations(boolean blockPrivateDestinations) {
            this.blockPrivateDestinations = blockPrivateDestinations;
        }
    }

    public static class Cache {

        private boolean enabled = true;
        private String keyPrefix = "shortlink:url:v1:";

        private Duration ttl = Duration.ofHours(1);

        private Duration negativeTtl = Duration.ofSeconds(30);

        private double jitter = 0.2;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public Duration getTtl() {
            return ttl;
        }

        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }

        public Duration getNegativeTtl() {
            return negativeTtl;
        }

        public void setNegativeTtl(Duration negativeTtl) {
            this.negativeTtl = negativeTtl;
        }

        public double getJitter() {
            return jitter;
        }

        public void setJitter(double jitter) {
            this.jitter = jitter;
        }
    }

    public static class RateLimit {

        private boolean enabled = true;
        private String keyPrefix = "shortlink:rl:v1:";

        private boolean failOpen = true;

        private Map<String, Policy> policies = new LinkedHashMap<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public boolean isFailOpen() {
            return failOpen;
        }

        public void setFailOpen(boolean failOpen) {
            this.failOpen = failOpen;
        }

        public Map<String, Policy> getPolicies() {
            return policies;
        }

        public void setPolicies(Map<String, Policy> policies) {
            this.policies = policies;
        }
    }

    public static class Policy {

        private long capacity = 60;

        private long refillTokens = 60;

        private Duration refillPeriod = Duration.ofMinutes(1);

        public long getCapacity() {
            return capacity;
        }

        public void setCapacity(long capacity) {
            this.capacity = capacity;
        }

        public long getRefillTokens() {
            return refillTokens;
        }

        public void setRefillTokens(long refillTokens) {
            this.refillTokens = refillTokens;
        }

        public Duration getRefillPeriod() {
            return refillPeriod;
        }

        public void setRefillPeriod(Duration refillPeriod) {
            this.refillPeriod = refillPeriod;
        }
    }

    public static class Geo {

        private boolean enabled = true;

        private String databasePath = "geoip/GeoLite2-City.mmdb";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getDatabasePath() {
            return databasePath;
        }

        public void setDatabasePath(String databasePath) {
            this.databasePath = databasePath;
        }
    }

    public static class Privacy {

        private String ipHashSalt = "replace-me-in-production";

        private boolean storeIpPrefix = true;

        public String getIpHashSalt() {
            return ipHashSalt;
        }

        public void setIpHashSalt(String ipHashSalt) {
            this.ipHashSalt = ipHashSalt;
        }

        public boolean isStoreIpPrefix() {
            return storeIpPrefix;
        }

        public void setStoreIpPrefix(boolean storeIpPrefix) {
            this.storeIpPrefix = storeIpPrefix;
        }
    }

    public static class Cleanup {

        private boolean enabled = true;
        private Duration interval = Duration.ofMinutes(5);
        private Duration initialDelay = Duration.ofSeconds(30);

        private int batchSize = 500;

        private int maxBatches = 20;

        private Duration purgeAfter = Duration.ofDays(30);

        private Duration lockTtl = Duration.ofMinutes(10);

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Duration getInterval() {
            return interval;
        }

        public void setInterval(Duration interval) {
            this.interval = interval;
        }

        public Duration getInitialDelay() {
            return initialDelay;
        }

        public void setInitialDelay(Duration initialDelay) {
            this.initialDelay = initialDelay;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public int getMaxBatches() {
            return maxBatches;
        }

        public void setMaxBatches(int maxBatches) {
            this.maxBatches = maxBatches;
        }

        public Duration getPurgeAfter() {
            return purgeAfter;
        }

        public void setPurgeAfter(Duration purgeAfter) {
            this.purgeAfter = purgeAfter;
        }

        public Duration getLockTtl() {
            return lockTtl;
        }

        public void setLockTtl(Duration lockTtl) {
            this.lockTtl = lockTtl;
        }
    }

    public static class Analytics {

        private int corePoolSize = 2;
        private int maxPoolSize = 4;

        private int queueCapacity = 10_000;

        private Duration maxQueryRange = Duration.ofDays(400);

        private int recentClicksLimit = 25;

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public Duration getMaxQueryRange() {
            return maxQueryRange;
        }

        public void setMaxQueryRange(Duration maxQueryRange) {
            this.maxQueryRange = maxQueryRange;
        }

        public int getRecentClicksLimit() {
            return recentClicksLimit;
        }

        public void setRecentClicksLimit(int recentClicksLimit) {
            this.recentClicksLimit = recentClicksLimit;
        }
    }
}
