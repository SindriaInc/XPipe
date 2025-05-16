package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.gis.GisValue;
import org.cmdbuild.gis.GisValueType;
import org.cmdbuild.gis.model.GisValueImpl;
import org.cmdbuild.gis.model.LinestringImpl;
import org.cmdbuild.gis.model.PointImpl;
import org.cmdbuild.gis.model.PolygonImpl;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import org.cmdbuild.service.rest.common.utils.WsSerializationUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("{a:processes|classes}/{" + CLASS_ID + "}/{b:cards|instances}/{" + CARD_ID + "}/geovalues")
@Produces(APPLICATION_JSON)
public class CardGeoValueWs {

    private final GisService service;
    private final CoreConfiguration coreConfiguration;
    private final EmailTemplateProcessorService emailTemplateProcessorService;
    private final DaoService dao;
    private final DmsService dmsService;
    private final UserClassService userClassService;

    public CardGeoValueWs(GisService logic, CoreConfiguration coreConfiguration, EmailTemplateProcessorService emailTemplateProcessorService, DaoService dao, DmsService dmsService, UserClassService userClassService) {
        this.service = checkNotNull(logic);
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.emailTemplateProcessorService = checkNotNull(emailTemplateProcessorService);
        this.dao = checkNotNull(dao);
        this.dmsService = checkNotNull(dmsService);
        this.userClassService = checkNotNull(userClassService);
    }

    @GET
    @Path("")
    public Object getAllForCard(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId) {
        return response(service.getGisValuesForCurrentUser(classId, cardId).stream().map(e -> serializeGeoValueWithPanelInfo(e, service.getGisAttributeWithCurrentUserByClassAndNameOrId(classId, e.getLayerName()))).collect(toList()));
    }

    @GET
    @Path("/{attributeId}")
    public Object get(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam("attributeId") String attributeId) {
        return response(serializeGeoValueWithPanelInfo(service.getGisValueForCurrentUser(classId, cardId, attributeId), service.getGisAttributeWithCurrentUserByClassAndNameOrId(classId, attributeId)));
    }

    @PUT
    @Path("/{attributeId}")
    public Object set(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam("attributeId") String attributeId, WsGisValue data) {
        GisValue value = GisValueImpl.builder()
                .withOwnerClassId(classId)
                .withOwnerCardId(cardId)
                .withLayerName(attributeId)
                .accept((b) -> {
                    switch (data.type) {
                        case POINT ->
                            b.withGeometry(new PointImpl(((WsPoint) data.geometry).x, ((WsPoint) data.geometry).y));
                        case LINESTRING ->
                            b.withGeometry(new LinestringImpl(((WsPoints) data.geometry).points.stream().map((p) -> new PointImpl(p.x, p.y)).collect(toList())));
                        case POLYGON ->
                            b.withGeometry(new PolygonImpl(((WsPoints) data.geometry).points.stream().map((p) -> new PointImpl(p.x, p.y)).collect(toList())));
                        default ->
                            throw unsupported("unsupported geometry type = %s", data.type);
                    }
                }).build();
        value = service.setGisValueWithCurrentUser(value);
        return response(serializeGeoValueWithPanelInfo(value, service.getGisAttributeWithCurrentUserByClassAndNameOrId(classId, attributeId)));
    }

    @DELETE
    @Path("/{attributeId}")
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam("attributeId") String attributeId) {
        service.deleteGisValueWithCurrentUser(classId, cardId, attributeId);
        return success();
    }

    private FluentMap serializeGeoValueWithPanelInfo(GisValue<?> value, GisAttribute geoAttr) {
        Card ownerCard = dao.getCard(value.getOwnerCardId());
        String imageAttachmentId;
        if (ownerCard.get(geoAttr.getConfig().getInfoWindowImage()) != null) {
            imageAttachmentId = dmsService.getCardAttachmentByMetadataId(toLong(ownerCard.get(geoAttr.getConfig().getInfoWindowImage()))).getDocumentId();
        } else {
            imageAttachmentId = null;
        }
        return WsSerializationUtils.serializeGeoValue(value).with(
                "infoWindowEnabled", geoAttr.getConfig().getInfoWindowEnabled(),
                "infoWindowContent", emailTemplateProcessorService.applyEmailTemplateExpr(geoAttr.getConfig().getInfoWindowContent(), ownerCard),
                "infoWindowImage", userClassService.getUserClass(geoAttr.getOwnerClassName()).getActiveServiceAttributes().stream().anyMatch(a -> a.getName().equals(geoAttr.getConfig().getInfoWindowImage())) ? imageAttachmentId : null,
                "_owner_description", ownerCard.getDescription());
    }

    public static class WsGisValue {

        public final GisValueType type;
        public final Object geometry;

        public WsGisValue(
                @JsonProperty("_type") String type,
                @JsonProperty("x") Double x,
                @JsonProperty("y") Double y,
                @JsonProperty("points") List<WsPoint> points) {
            this.type = GisValueType.valueOf(checkNotBlank(type).toUpperCase());
            geometry = switch (this.type) {
                case POINT ->
                    new WsPoint(x, y);
                case LINESTRING, POLYGON ->
                    new WsPoints(points);
                default ->
                    throw unsupported("unsupported geometry type = %s", this.type);
            };
        }
    }

    public static class WsPoint {

        public final double x, y;

        public WsPoint(@JsonProperty("x") Double x, @JsonProperty("y") Double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class WsPoints {

        public final List<WsPoint> points;

        public WsPoints(List<WsPoint> points) {
            this.points = checkNotNull(points);
        }
    }

}
