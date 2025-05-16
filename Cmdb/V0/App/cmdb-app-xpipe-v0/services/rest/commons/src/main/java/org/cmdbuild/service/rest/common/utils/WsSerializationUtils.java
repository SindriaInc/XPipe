/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.utils;

import static com.google.common.collect.FluentIterable.from;
import java.util.function.BiConsumer;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.common.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.config.inner.Patch;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.gis.CmGeometry;
import org.cmdbuild.gis.GisValue;
import org.cmdbuild.gis.Linestring;
import org.cmdbuild.gis.Point;
import org.cmdbuild.gis.Polygon;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.serializeListOfStrings;

public class WsSerializationUtils {

    public static CmMapUtils.FluentMap<String, Object> serializePatchInfo(Patch patch) {
        return map("name", patch.getVersion(),
                "description", patch.getDescription(),
                "category", String.valueOf(patch.getCategory()));
    }

    public static CmMapUtils.FluentMap serializeGeoValue(GisValue<?> value) {
        return map(
                "_id", hash(key(value.getOwnerClassId(), value.getLayerName(), Long.toString(value.getOwnerCardId()))),
                "_type", value.getType().name().toLowerCase(),
                "_attr", value.getLayerName(),
                "_owner_type", value.getOwnerClassId(),
                "_owner_id", value.getOwnerCardId()).with(serializeGeometry(value.getGeometry()));
    }

    public static CmMapUtils.FluentMap serializeGeometry(CmGeometry value) {
        switch (value.getType()) {
            case POINT:
                return map("x", value.as(Point.class).getX(), "y", value.as(Point.class).getY());
            case LINESTRING:
                return map("points", value.as(Linestring.class).getPoints().stream().map((p) -> map("x", p.getX(), "y", p.getY())).collect(toList()));
            case POLYGON:
                return map("points", value.as(Polygon.class).getPoints().stream().map((p) -> map("x", p.getX(), "y", p.getY())).collect(toList()));
            default:
                throw new IllegalArgumentException("unsupported geometry type = " + value.getType());
        }
    }

    public static void expandReferenceAttributes(Card card, BiConsumer<String, Object> newAttrConsumer) {//duplicate code :(
        for (Attribute referenceAttribute : from(card.getType().getAllAttributes()).filter((a) -> a.isOfType(REFERENCE))) {
            Object value = card.get(referenceAttribute.getName());
            if (value instanceof IdAndDescriptionImpl) {
                IdAndDescriptionImpl idAndDescriptionValue = (IdAndDescriptionImpl) value;
                if (idAndDescriptionValue.getId() != null) {
                    newAttrConsumer.accept("_" + referenceAttribute.getName() + "_description", idAndDescriptionValue.getDescription());
                }
            }
        }
    }

}
