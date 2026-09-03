package com.example.UrlShortener.repository;

import com.example.UrlShortener.domain.ClickEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    @Query(value = """
            SELECT COUNT(*) AS clicks,
                   COUNT(DISTINCT ip_hash) AS visitors
                FROM click_event
               WHERE short_url_id = :linkId
                 AND clicked_at >= :from
                 AND clicked_at < :to
            """, nativeQuery = true)
    AnalyticsProjections.TotalsRow findTotals(@Param("linkId") Long linkId,
                                              @Param("from")Instant from,
                                              @Param("to") Instant to);

    @Query(value = """
            SELECT DATE_FORMAT(clicked_at, '%Y-%m-%d') AS bucket,
                   COUNT(*) AS clicks,
                   COUNT(DISTINCT ip_hash) AS visitors
              FROM click_event
             WHERE short_url_id = :linkId
               AND clicked_at >= :from
               AND clicked_at < :to
             GROUP BY bucket
             ORDER BY bucket
            """, nativeQuery = true)
    List<AnalyticsProjections.BucketRow> findDailyBuckets(@Param("linkId") Long linkId,
                                                          @Param("from") Instant from,
                                                          @Param("to") Instant to);

    @Query(value = """
            SELECT DATE_FORMAT(clicked_at, '%Y-%m-%dT%H:00') AS bucket,
                   COUNT(*) AS clicks,
                   COUNT(DISTINCT ip_hash) AS visitors
              FROM click_event
             WHERE short_url_id = :linkId
               AND clicked_at >= :from
               AND clicked_at < :to
             GROUP BY bucket
             ORDER BY bucket
            """, nativeQuery = true)
    List<AnalyticsProjections.BucketRow> findHourlyBuckets(@Param("linkId") Long linkId,
                                                          @Param("from") Instant from,
                                                          @Param("to") Instant to);

    @Query(value = """
            SELECT COALESCE(NULLIF(referrer_domain, ''), 'direct') AS label,
                   COUNT(*) AS clicks
              FROM click_event
             WHERE short_url_id = :linkId
               AND clicked_at >= :from
               AND clicked_at < :to
             GROUP BY label
             ORDER BY clicks DESC, label
             LIMIT :maxRows
            """, nativeQuery = true)
    List<AnalyticsProjections.LabelCountRow> findTopReferrers(@Param("linkId") Long linkId,
                                                              @Param("from") Instant from,
                                                              @Param("to") Instant to,
                                                              @Param("maxRows") int maxRows);

    @Query(value = """
            SELECT COALESCE(country_code, 'ZZ') AS countryCode,
                   COALESCE(country_name, 'Unknown') AS countryName,
                   COUNT(*) AS clicks
              FROM click_event
             WHERE short_url_id = :linkId
               AND clicked_at >= :from
               AND clicked_at < :to
             GROUP BY countryCode, countryName
             ORDER BY clicks DESC, countryName
             LIMIT :maxRows
            """, nativeQuery = true)
    List<AnalyticsProjections.GeoRow> findTopCountries(@Param("linkId") Long linkId,
                                                              @Param("from") Instant from,
                                                              @Param("to") Instant to,
                                                              @Param("maxRows") int maxRows);

    @Query(value = """
            SELECT COALESCE(device_type, 'unknown') AS label,
                   COUNT(*) AS clicks
              FROM click_event
             WHERE short_url_id = :linkId
               AND clicked_at >= :from
               AND clicked_at < :to
             GROUP BY label
             ORDER BY clicks DESC, label
            """, nativeQuery = true)
    List<AnalyticsProjections.LabelCountRow> findDeviceBreakdown(@Param("linkId") Long linkId,
                                                                 @Param("from") Instant from,
                                                                 @Param("to") Instant to);

    List<ClickEvent> findByShortUrlIdOrderByClickedAtDesc(Long shortUrlId, Pageable pageable);

    @Query(value = """
            SELECT COUNT(*)
              FROM click_event
             WHERE clicked_at >= :from
            """, nativeQuery = true)
    long countSince(@Param("from") Instant from);
}
