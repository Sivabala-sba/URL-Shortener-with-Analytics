package com.example.UrlShortener.repository;

import com.example.UrlShortener.domain.LinkStatus;
import com.example.UrlShortener.domain.ShortUrl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByCode(String code);

    boolean existsByCode(String code);

    List<ShortUrl> findByOwnerKeyOrderByCreatedAtDesc(String ownerKey, Pageable pageable);

    long countByStatus(LinkStatus status);

    @Modifying
    @Query("""
            UPDATE ShortUrl s
               SET s.clickCount = s.clickCount + 1,
                   s.lastAccessedAt = :at
             WHERE s.id = :id
            """)
    int recordClick(@Param("id") Long id, @Param("at") Instant at);

    @Query("SELECT COALESCE(SUM(s.clickCount), 0) FROM ShortUrl s")
    long sumAllClicks();

    @Query(value = """
            SELECT id AS id, code AS code
              FROM short_url
             WHERE status = 'ACTIVE'
               AND expires_at IS NOT NULL
               AND expires_at <= :now
             ORDER BY expires_at
             LIMIT :maxRows
            """, nativeQuery = true)
    List<AnalyticsProjections.ExpiredLinkRow> findLapsedLinks(@Param("now") Instant now,
                                                              @Param("maxRows") int maxRows);

    @Modifying
    @Query(value = """
            UPDATE short_url
               SET status = 'EXPIRED'
             WHERE id IN (:ids)
               AND status = 'ACTIVE'
            """, nativeQuery = true)
    int markExpired(@Param("ids") Collection<Long> ids);

    @Query(value = """
            SELECT id AS id, code AS code
              FROM short_url
             WHERE status = 'EXPIRED'
               AND expires_at IS NOT NULL
               AND expires_at <= :threshold
             ORDER BY expires_at
             LIMIT :maxRows
            """, nativeQuery = true)
    List<AnalyticsProjections.ExpiredLinkRow> findPurgeableLinks(@Param("threshold") Instant threshold,
                                                                 @Param("maxRows") int maxRows);

    @Modifying
    @Query(value = "DELETE FROM short_url WHERE id IN (:ids)", nativeQuery = true)
    int deleteByIds(@Param("ids") Collection<Long> ids);

    @Query(value = """
            SELECT code AS code,
                   long_url AS longUrl,
                   click_count AS clicks,
                   status AS status
              FROM short_url
             ORDER BY click_count DESC, id DESC
             LIMIT :maxRows
            """, nativeQuery = true)
    List<AnalyticsProjections.TopLinkRow> findTopLinks(@Param("maxRows") int maxRows);
}
