package org.cmdbuild.config;

import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.gis")
public final class GisConfigurationImpl implements GisConfiguration {

    @ConfigValue(key = "center.lon", defaultValue = "0")
    private double centerLon;

    @ConfigValue(key = "center.lat", defaultValue = "0")
    private double centerLat;

    @ConfigValue(key = "initialZoomLevel", defaultValue = "3")
    private double initialZoomLevel;

    @ConfigValue(key = "minZoomLevel", defaultValue = "0")
    private double minZoomLevel;

    @ConfigValue(key = "maxZoomLevel", defaultValue = "25")
    private double maxZoomLevel;

    @ConfigValue(key = "osm_minzoom", defaultValue = "0")
    private double osm_minzoom;

    @ConfigValue(key = "osm_maxzoom", defaultValue = "24")
    private double osm_maxzoom;

    @ConfigValue(key = "enabled", defaultValue = FALSE)
    private boolean isEnabled;

    @ConfigValue(key = "yahoo", defaultValue = FALSE)
    private boolean isYahooEnabled;

    @ConfigValue(key = "google", defaultValue = FALSE)
    private boolean isGoogleEnabled;

    @ConfigValue(key = "geoserver.enabled", defaultValue = FALSE)//TODO auto migrate config from org.cmdbuild.gis.geoserver to org.cmdbuild.gis.geoserver.enabled
    private boolean isGeoserverEnabled;

    @ConfigValue(key = "navigation.enabled", defaultValue = FALSE)
    private boolean isNavigationEnabled;

    @ConfigValue(key = "osm", defaultValue = FALSE)
    private boolean isOsmEnabled;

    @ConfigValue(key = "enableAngleDisplacementProcessing", defaultValue = TRUE)
    private boolean enableAngleDisplacementProcessing;

    @ConfigValue(key = "google_key", defaultValue = "", category = CC_ENV)
    private String googleKey;

    @ConfigValue(key = "yahoo_key", defaultValue = "", category = CC_ENV)
    private String yahooKey;

    @ConfigValue(key = "geoserver_url", defaultValue = "http://localhost:12080/geoserver", category = CC_ENV)
    private String geoserverUrl;

    @ConfigValue(key = "geoserver_workspace", defaultValue = "cmdbuild", category = CC_ENV)
    private String geoserverWorkspace;

    @ConfigValue(key = "geoserver_admin_user", defaultValue = "admin", category = CC_ENV)
    private String geoserverAdminUser;

    @ConfigValue(key = "geoserver_admin_password", defaultValue = "geoserver", category = CC_ENV)
    private String geoserverAdminPassword;

    @Override
    public boolean isNavigationEnabled() {
        return isNavigationEnabled;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public double getCenterLon() {
        return centerLon;
    }

    @Override
    public double getCenterLat() {
        return centerLat;
    }

    @Override
    public double getInitialZoomLevel() {
        return initialZoomLevel;
    }

    @Override
    public double getOsmMinZoom() {
        return osm_minzoom;
    }

    @Override
    public double getOsmMaxZoom() {
        return osm_maxzoom;
    }

    @Override
    public boolean isGeoServerEnabled() {
        return isGeoserverEnabled;
    }

    @Override
    public boolean isYahooEnabled() {
        return isYahooEnabled;
    }

    @Override
    public boolean isGoogleEnabled() {
        return isGoogleEnabled;
    }

    @Override
    public boolean isOsmEnabled() {
        return isOsmEnabled;
    }

    @Override
    public boolean enableAngleDisplacementProcessing() {
        return enableAngleDisplacementProcessing;
    }

    @Override
    public String getGoogleKey() {
        return googleKey;
    }

    @Override
    public String getYahooKey() {
        return yahooKey;
    }

    @Override
    public String getGeoServerUrl() {
        return geoserverUrl.replaceFirst("[/]$", "");
    }

    @Override
    public String getGeoServerWorkspace() {
        return geoserverWorkspace;
    }

    @Override
    public String getGeoServerAdminUser() {
        return geoserverAdminUser;
    }

    @Override
    public String getGeoServerAdminPassword() {
        return geoserverAdminPassword;
    }

}
