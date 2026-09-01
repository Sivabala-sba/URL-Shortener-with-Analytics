package com.example.UrlShortener.domain;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;

@Entity
@Table(name = "short_url")
public class ShortUrl implements Persistable<Long> {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "code", nullable = false, length = 32, updatable = false)
    private String code;

    @Column(name = "long_url", nullable = false, columnDefinition = "TEXT")
    private String longUrl;

    @Column(name = "owner_key", nullable = false, length = 64, updatable = false)
    private String ownerKey;

    @Column(name = "custom_alias", nullable = false, updatable = false)
    private boolean customAlias;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private LinkStatus status = LinkStatus.ACTIVE;

    @Column(name = "click_count", nullable = false)
    private long clickCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    @Transient
    private boolean persisted = true;

    protected ShortUrl(){
        // for JPA
    }

    public static ShortUrl create(long id,
                                  String code,
                                  String longUrl,
                                  String ownerKey,
                                  boolean customAlias,
                                  Instant createdAt,
                                  Instant expiresAt){
        ShortUrl link = new ShortUrl();
        link.id = id;
        link.code = code;
        link.longUrl = longUrl;
        link.ownerKey = ownerKey;
        link.customAlias = customAlias;
        link.status = LinkStatus.ACTIVE;
        link.clickCount = 0L;
        link.createdAt = createdAt;
        link.expiresAt = expiresAt;
        link.persisted = false;
        return link;
    }

    @PostPersist
    @PostLoad
    void markPersisted(){
        this.persisted = true;
    }

    @Override
    public Long getId(){
        return id;
    }

    @Override
    public boolean isNew(){
        return !persisted;
    }

    public String getCode() {
        return code;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public String getOwnerKey() {
        return ownerKey;
    }

    public boolean isCustomAlias() {
        return customAlias;
    }

    public LinkStatus getStatus() {
        return status;
    }

    public long getClickCount() {
        return clickCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getLastAccessedAt() {
        return lastAccessedAt;
    }

    public boolean isExpiredAt(Instant now){
        return expiresAt != null && !expiresAt.isAfter(now);
    }

    public boolean isResolvableAt(Instant now){
        return  status == LinkStatus.ACTIVE && !isExpiredAt(now);
    }

    public void markExpired(){
        this.status = LinkStatus.EXPIRED;
    }

    public void disable(){
        this.status = LinkStatus.DISABLED;
    }
}
