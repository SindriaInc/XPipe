/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.utils;

import static java.lang.String.format;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.SQLException;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisException;
import org.cmdbuild.gis.model.LinestringImpl;
import org.cmdbuild.gis.model.PointImpl;
import org.cmdbuild.gis.model.PolygonImpl;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.postgis.PGgeometry;
import org.cmdbuild.gis.GisValue;
import org.cmdbuild.gis.Linestring;
import org.cmdbuild.gis.Point;
import org.cmdbuild.gis.Polygon;
import org.cmdbuild.gis.model.GisValueImpl;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.slf4j.LoggerFactory;
import org.cmdbuild.gis.CmGeometry;

public class GisUtils {

    public static String buildGisTableName(String baseClassName, String layerName) {
        return format("gis.Gis_%s_%s", checkNotBlank(baseClassName), checkNotBlank(layerName));
    }

    public static String buildGisTableNameForQuery(String baseClassName, String layerName) {
        return format("gis.\"Gis_%s_%s\"", checkNotBlank(baseClassName), checkNotBlank(layerName));
    }

    public static String gisTableNameToBaseClassName(String tableName) {
        return checkNotBlank(tableName.replaceFirst("gis.Gis_(.+)_.+", "$1"));
    }

    public static String gisTableNameToAttributeName(String tableName) {
        return checkNotBlank(tableName.replaceFirst("gis.Gis_.+_(.+)", "$1"));
    }

    public static CmGeometry parseGeometry(String value) {
        try {
            org.postgis.Geometry postgisGeometry = PGgeometry.geomFromString(checkNotBlank(value));
            if (postgisGeometry instanceof org.postgis.Point) {
                return new PointImpl(((org.postgis.Point) postgisGeometry).getX(), ((org.postgis.Point) postgisGeometry).getY());
            } else if (postgisGeometry instanceof org.postgis.LineString) {
                return new LinestringImpl(asList(((org.postgis.LineString) postgisGeometry).getPoints()).stream().map((p) -> new PointImpl(p.getX(), p.getY())).collect(toList()));
            } else if (postgisGeometry instanceof org.postgis.Polygon) {
                if (((org.postgis.Polygon) postgisGeometry).numRings() > 1) {
//                    LoggerFactory.getLogger(GisUtils.class).warn("found geometry (polygon) with more than one ring for attr = {}.{} card = {}; only the first ring will be considered (others will be discarded)", ownerClassName, layerName, ownerCardId);//TODO check this
                    LoggerFactory.getLogger(GisUtils.class).warn("found geometry (polygon) with more than one ring; only the first ring will be considered (others will be discarded)");//TODO check this
                }
                return new PolygonImpl(asList(((org.postgis.Polygon) postgisGeometry).getRing(0).getPoints()).stream().map((p) -> new PointImpl(p.getX(), p.getY())).collect(toList()));
            } else {
                throw new IllegalArgumentException("unsupported postgis geometry = " + postgisGeometry);
            }
        } catch (SQLException ex) {
            throw new GisException(ex, "error parsing geometry from string value = '%s'", abbreviate(value));
        }
    }

    @Nullable
    public static GisValue postgisSqlToCmGeometryOrNull(GisAttribute attribute, long ownerCardId, @Nullable String sqlGeometryValue, @Nullable String ownerDescription) {
        return postgisSqlToCmGeometryOrNull(attribute.getLayerName(), attribute.getOwnerClassName(), ownerCardId, sqlGeometryValue, ownerDescription);
    }

    @Nullable
    public static GisValue postgisSqlToCmGeometryOrNull(String layerName, String ownerClassName, long ownerCardId, @Nullable String sqlGeometryValue, @Nullable String ownerDescription) {
        if (isBlank(sqlGeometryValue)) {
            return null;
        } else {
            CmGeometry cmGeometry = parseGeometry(sqlGeometryValue);
            return GisValueImpl.builder()
                    .withLayerName(layerName)
                    .withOwnerCardId(ownerCardId)
                    .withOwnerClassId(ownerClassName)
                    .withGeometry(cmGeometry)
                    .withOwnerCardDescription(ownerDescription)
                    .build();
        }
    }

    public static String cmGeometryToPostgisSql(CmGeometry geometry) {
        switch (geometry.getType()) {
            case POINT:
                return format("POINT(%s %s)", serializePostgisDouble(((Point) geometry).getX()), serializePostgisDouble(((Point) geometry).getY()));
            case LINESTRING:
                return format("LINESTRING(%s)", ((Linestring) geometry).getPoints().stream().map((p) -> format("%s %s", serializePostgisDouble(p.getX()), serializePostgisDouble(p.getY()))).collect(joining(",")));
            case POLYGON:
                return format("POLYGON((%s))", ((Polygon) geometry).getPoints().stream().map((p) -> format("%s %s", serializePostgisDouble(p.getX()), serializePostgisDouble(p.getY()))).collect(joining(",")));
            default:
                throw unsupported("unsupported geometry type = %s", geometry.getType());
        }
    }

    private static String serializePostgisDouble(double value) {
        return new BigDecimal(value, new MathContext(15, RoundingMode.HALF_UP)).stripTrailingZeros().toPlainString();
    }

}
