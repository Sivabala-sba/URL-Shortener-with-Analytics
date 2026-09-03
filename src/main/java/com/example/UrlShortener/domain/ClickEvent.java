package com.example.UrlShortener.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "click_event")
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "short_url_id", nullable = false, updatable = false)
    private Long shortUrlId;

    @Column(name = "code", nullable = false, length = 32, updatable = false)
    private String code;

    @Column(name = "clicked_at", nullable = false, updatable = false)
    private Instant clickedAt;

    @Column(name = "ip_hash", length = 64, updatable = false)
    private String ipHash;

    @Column(name = "ip_prefix", length = 45, updatable = false)
    private String ipPrefix;

    @Column(name = "country_code", length = 2, updatable = false)
    private String countryCode;

    @Column(name = "country_name", length = 64, updatable = false)
    private String countryName;

    @Column(name = "city", length = 96, updatable = false)
    private String city;

    @Column(name = "referrer_domain", length = 255, updatable = false)
    private String referrerDomain;

    @Column(name = "referrer_url", length = 1024, updatable = false)
    private String referrerUrl;

    @Column(name = "user_agent", length = 512, updatable = false)
    private String userAgent;

    @Column(name = "device_type", length = 16, updatable = false)
    private String deviceType;

    protected ClickEvent(){
        // for JPA
    }

    public ClickEvent(Long shortUrlId,
                      String code,
                      Instant clickedAt,
                      String ipHash,
                      String ipPrefix,
                      String countryCode,
                      String countryName,
                      String city,
                      String referrerDomain,
                      String referrerUrl,
                      String userAgent,
                      String deviceType){
        this.shortUrlId = shortUrlId;
        this.code = code;
        this.clickedAt = clickedAt;
        this.ipHash = ipHash;
        this.ipPrefix = ipPrefix;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.city = city;
        this.referrerDomain = referrerDomain;
        this.referrerUrl = referrerUrl;
        this.userAgent = userAgent;
        this.deviceType = deviceType;
    }

    public Long getId() {
        return id;
    }

    public Long getShortUrlId() {
        return shortUrlId;
    }

    public String getCode() {
        return code;
    }

    public Instant getClickedAt() {
        return clickedAt;
    }

    public String getIpHash() {
        return ipHash;
    }

    public String getIpPrefix() {
        return ipPrefix;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCity() {
        return city;
    }

    public String getReferrerDomain() {
        return referrerDomain;
    }

    public String getReferrerUrl() {
        return referrerUrl;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getDeviceType() {
        return deviceType;
    }
}
