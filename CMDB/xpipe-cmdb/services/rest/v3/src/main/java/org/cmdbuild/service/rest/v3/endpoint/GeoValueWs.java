package org.cmdbuild.service.rest.v3.endpoint;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import org.cmdbuild.gis.Area;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.gis.GisValue;

import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.AREA;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTRIBUTE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.cmdbuild.service.rest.common.utils.WsSerializationUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.gis.GisValuesAndNavTree;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;

@Path("{a:processes|classes}/_ANY/{b:cards|instances}/_ANY/geovalues/")
@Produces(APPLICATION_JSON)
public class GeoValueWs {

    private final GisService gis;

    public GeoValueWs(GisService service) {
        this.gis = checkNotNull(service);
    }

    @GET
    @Path(EMPTY)
    public Object query(@QueryParam(ATTRIBUTE) Set<Long> attrs,
            @QueryParam(AREA) String area,
            @QueryParam("attach_nav_tree") @DefaultValue(FALSE) Boolean attachNavTree,
            @QueryParam(FILTER) String filterStr,
            @QueryParam("forOwner") String forOwner) {
        CmdbFilter filter = parseFilter(filterStr);
        if (attachNavTree) {
            GisValuesAndNavTree geoValuesAndNavTree = gis.getGisValuesAndNavTree(attrs, area, filter, forOwner);
            return geometriesToResponse(geoValuesAndNavTree.getGisValues()).accept((m) -> {
                FluentMap<String, Object> meta = (FluentMap<String, Object>) m.get("meta");
                meta.put("nav_tree_items", geoValuesAndNavTree.getNavTree().stream().map((n) -> map(
                        "_id", n.getCardId(),
                        "type", n.getClassId(),
                        "description", n.getDescription(),
                        "parentid", n.getParentCardId(),
                        "parenttype", n.getParentClassId(),
                        "navTreeNodeId", n.getNavTreeNodeId()
                )).collect(toList()));
            });
        } else {
            return geometriesToResponse(gis.getGisValues(attrs, area, filter, forOwner));
        }
    }

    @GET
    @Path("area")
    public Object queryArea(@QueryParam(ATTRIBUTE) Set<Long> attrs, @QueryParam(FILTER) String filterStr, @QueryParam("forOwner") String forOwner) {
        CmdbFilter filter = parseFilter(filterStr);
        Area area = gis.getAreaForValues(attrs, filter, forOwner);
        if (area == null) {
            return success().with("found", false);
        } else {
            return response(map(
                    "x1", area.getX1(),
                    "y1", area.getY1(),
                    "x2", area.getX2(),
                    "y2", area.getY2()
            )).with("found", true);
        }
    }

    @GET
    @Path("center")
    public Object queryCenter(@QueryParam(ATTRIBUTE) Set<Long> attrs, @QueryParam(FILTER) String filterStr, @QueryParam("forOwner") String forOwner) {
        CmdbFilter filter = parseFilter(filterStr);
        Area area = gis.getAreaForValues(attrs, filter, forOwner);
        if (area == null) {
            return success().with("found", false);
        } else {
            return response(map(
                    "x", area.getCenter().getX(),
                    "y", area.getCenter().getY()
            )).with("found", true);
        }
    }

    private static FluentMap<String, Object> geometriesToResponse(List<GisValue> geometries) {
        return response(geometries.stream().map(g -> WsSerializationUtils.serializeGeoValue(g).with("_owner_description", g.getOwnerCardDescription())));
    }
}
