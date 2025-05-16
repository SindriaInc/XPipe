package org.cmdbuild.config;

public interface GisConfiguration {

    boolean isEnabled();

    boolean isGeoServerEnabled();

    boolean isNavigationEnabled();

    boolean isYahooEnabled();

    boolean isGoogleEnabled();

    boolean isOsmEnabled();
    
    boolean enableAngleDisplacementProcessing();

    String getGoogleKey();

    String getYahooKey();

    String getGeoServerUrl();

    String getGeoServerWorkspace();

    String getGeoServerAdminUser();

    String getGeoServerAdminPassword();

    double getCenterLat();

    double getCenterLon();

    double getInitialZoomLevel();

    double getOsmMaxZoom();

    double getOsmMinZoom();

}
