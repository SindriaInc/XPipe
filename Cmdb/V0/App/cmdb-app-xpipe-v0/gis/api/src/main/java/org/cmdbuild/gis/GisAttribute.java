package org.cmdbuild.gis;

import static com.google.common.base.Objects.equal;
import java.time.ZonedDateTime;
import java.util.Set;
import javax.annotation.Nullable;
import static org.cmdbuild.gis.GisAttributeType.GAT_POLYGON;

public interface GisAttribute {

    @Nullable
    Long getId();

    String getLayerName();

    int getIndex();

    int getMinimumZoom();

    int getDefaultZoom();

    int getMaximumZoom();

    Set<String> getVisibility();

    ZonedDateTime getBeginDate();

    String getOwnerClassName();

    boolean isActive();

    String getDescription();

    String getMapStyle();

    GisAttributeType getType();
    
    GisAttributeConfig getConfig();

    default boolean isVisible(String tableName) {
        return getVisibility().contains(tableName);
    }

    default boolean hasId() {
        return getId() != null;
    }

    default boolean isPostgis() {
        switch (getType()) {
            case GAT_POINT:
            case GAT_POLYGON:
            case GAT_LINESTRING:
                return true;
            default:
                return false;
        }
    }

    default boolean isPolygon() {
        return equal(getType(), GAT_POLYGON);
    }

    default boolean isGeoserver() {
        switch (getType()) {
            case GAT_GEOTIFF:
            case GAT_SHAPE:
                return true;
            default:
                return false;
        }
    }

}
