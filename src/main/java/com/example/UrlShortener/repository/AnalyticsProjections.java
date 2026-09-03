package com.example.UrlShortener.repository;

public final class AnalyticsProjections {

    private  AnalyticsProjections(){
    }

    public interface BucketRow{
        String getBucket();
        long getClicks();
        long getVisitors();
    }

    public interface LabelCountRow{
        String getLabel();
        long getClicks();
    }

    public interface GeoRow{
        String getCountryCode();
        String getCountryName();
        long getClicks();
    }

    public interface  TotalsRow{
        long getClicks();
        long getVisitors();
    }

    public interface  TopLinkRow{
        String getCode();
        String getLongUrl();
        long getClicks();
        String getStatus();
    }

    public interface ExpiredLinkRow{
        Long getId();
        String getCode();
    }
}
