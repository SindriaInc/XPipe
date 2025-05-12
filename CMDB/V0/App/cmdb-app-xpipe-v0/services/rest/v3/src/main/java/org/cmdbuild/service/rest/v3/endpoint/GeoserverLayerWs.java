/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.Long.parseLong;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.gis.GeoserverLayer;
import org.cmdbuild.gis.GisService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ZOOM_DEF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ZOOM_MAX;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ZOOM_MIN;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import javax.ws.rs.HeaderParam;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.geoserver.GeoserverLayerImpl;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

@Path("{a:processes|classes}/{" + CLASS_ID + "}/{b:cards|instances}/{" + CARD_ID + "}/geolayers/")
@Produces(APPLICATION_JSON)
public class GeoserverLayerWs {

    private final OperationUserSupplier user;
    private final GisService service;
    private final DaoService dao;
    private final UserClassService userClassService;

    public GeoserverLayerWs(OperationUserSupplier user, GisService service, DaoService dao, UserClassService userClassService) {
        this.user = checkNotNull(user);
        this.service = checkNotNull(service);
        this.dao = checkNotNull(dao);
        this.userClassService = checkNotNull(userClassService);
    }

    @GET
    @Path("{attrName}/")
    public Object getOneForCard(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam("attrName") String layerCodeOrId) {
        checkArgument(userClassService.getUserClass(classId).hasGisAttributeReadPermission(layerCodeOrId), "User doesn't have read permissions on the specified geoattribute =< %s >", layerCodeOrId);
        GeoserverLayer geoServerLayer = service.getGeoserverLayer(classId, layerCodeOrId, cardId);//TODO add access control
        return response(serializeLayer(geoServerLayer));
    }

    @GET
    @Path("")
    public Object getMany(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) String cardId, @QueryParam(FILTER) String filterStr, @QueryParam("visible") @DefaultValue(FALSE) Boolean isVisible) {
        List list;
        if (equal(classId, "_ANY") && equal(cardId, "_ANY")) {
            //TODO improve this
            list = service.getGeoServerLayers().stream().filter(l -> user.hasPrivileges((p) -> p.hasReadAccess(dao.getClasse(l.getOwnerClass())))
                    && dao.getCardOrNull(dao.getClasse(BASE_CLASS_NAME), l.getOwnerCard()) != null).collect(toImmutableList());
        } else {
            //TODO add access control
            list = service.getGeoServerLayersForCard(classId, parseLong(cardId));
        }
        if (!isAdminViewMode(viewMode)) {
            list = list(list)
                    .withOnly((l) -> ((GeoserverLayer) l).isActive()
                    && service.getGisAttributeIncludeInherited(((GeoserverLayer) l).getOwnerClass(), ((GeoserverLayer) l).getAttributeName()).isActive()
                    && userClassService.getUserClass(((GeoserverLayer) l).getOwnerClass()).hasGisAttributeReadPermission(((GeoserverLayer) l).getAttributeName()));
        }
        list = list(list).map(l -> serializeLayer((GeoserverLayer) l));
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (filter.hasAttributeFilter()) {
            list = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, m) -> toStringOrNull(m.get(k))).withFilter(filter.getAttributeFilter()).filter(list);
        }
        return response(list);
    }

    @PUT
    @Path("{attrName}/")
    public Object update(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam("attrName") String attrName, @Multipart(value = FILE, required = false) DataHandler dataHandler, @Nullable WsLayerData data) {
        GeoserverLayer layer = service.getGeoserverLayerByCodeOrNull(classId, attrName, cardId);
        checkArgument(userClassService.getUserClass(classId).hasGisAttributeWritePermission(attrName), "User doesn't have write permissions on the specified geoattribute =< %s >", attrName);
        if (dataHandler != null) {
            layer = service.setGeoserverLayer(classId, attrName, cardId, dataHandler);
        }
        if (data != null) {
            layer = service.updateGeoserverLayer(GeoserverLayerImpl.copyOf(layer).withActive(data.active).build());
        }
        return response(serializeLayer(layer));
    }

    @DELETE
    @Path("{attrName}/")
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam("attrName") String attrName) {
        checkArgument(userClassService.getUserClass(classId).hasGisAttributeWritePermission(attrName), "User doesn't have write permissions on the specified geoattribute =< %s >", attrName);
        service.deleteGeoServerLayer(classId, attrName, cardId);
        return success();
    }

    private FluentMap serializeLayer(GeoserverLayer l) {
        GisAttribute a = service.getGisAttributeIncludeInherited(l.getOwnerClass(), l.getAttributeName());
        return map(
                "_id", l.getId(),
                "name", a.getLayerName(),
                "attribute_id", a.getId(),
                "active", a.isActive() && l.isActive(),
                "attribute_active", a.isActive(),
                "layer_active", l.isActive(),
                "_type", serializeEnum(a.getType()),
                "description", a.getDescription(),
                "index", a.getIndex(),
                "geoserver_name", l.getGeoserverLayer(),//TODO rename this, then remove
                "geoserver_store", l.getGeoserverStore(),
                "geoserver_layer", l.getGeoserverLayer(),
                "description", a.getDescription(),
                "x", l.getCenter().isZero() ? null : l.getCenter().getX(),
                "y", l.getCenter().isZero() ? null : l.getCenter().getY(),
                "_owner_description", dao.getCard(l.getOwnerCard()).getDescription(),
                ZOOM_MIN, a.getMinimumZoom(),
                ZOOM_DEF, a.getDefaultZoom(),
                ZOOM_MAX, a.getMaximumZoom(),
                "visibility", a.getVisibility(),
                "_owner_type", l.getOwnerClass(),
                "_owner_id", l.getOwnerCard(),
                "_beginDate", toIsoDateTime(l.getBeginDate()));
    }

    public static class WsLayerData {

        private final Boolean active;

        public WsLayerData(@JsonProperty("layer_active") Boolean active) {
            this.active = firstNonNull(active, true);
        }
    }
}
