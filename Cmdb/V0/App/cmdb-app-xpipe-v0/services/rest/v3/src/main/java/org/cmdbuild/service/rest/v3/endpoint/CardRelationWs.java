package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelperv3;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

@Path("{a:classes|processes}/{" + CLASS_ID + "}/{b:cards|instances}/{" + CARD_ID + "}/relations/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardRelationWs {

    private final UserDomainService domainService;
    private final DaoService dao;
    private final CardWsSerializationHelperv3 helper;

    public CardRelationWs(UserDomainService domainService, DaoService dao, CardWsSerializationHelperv3 helper) {
        this.domainService = checkNotNull(domainService);
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path(EMPTY)
    public Object read(
            @PathParam(CLASS_ID) String className,
            @PathParam(CARD_ID) Long cardId,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort) {
        PagedElements<CMRelation> relations = domainService.getUserRelationsForCard(className, cardId, DaoQueryOptionsImpl.builder()
                .withFilter(filterStr).
                withPaging(offset, limit)
                .withSorter(sort).build());
        return response(relations.map(detailed ? helper::serializeDetailedRelation : helper::serializeMinimalRelation));

//        List<CMRelation> relations = list(dao.getRelationsInfoForCard(card(className, cardId)));
//
//        Collections.sort(relations, (r1, r2) -> {
//            Domain d1 = r1.getDomainWithThisRelationDirection(),
//                    d2 = r2.getDomainWithThisRelationDirection();
//
//            return ComparisonChain.start()
//                    .compare(d1.getIndexForSource(), d2.getIndexForSource())//TODO check this
//                    .compare(d1.getName(), d2.getName())
//                    .compare(r1.getTargetCard().getClassName(), r2.getTargetCard().getClassName())
//                    .result();
//
//        });
//
//        return response(relations.stream().map(detailed ? CardRelationWs::serializeDetailedRelation : CardRelationWs::serializeMinimalRelation).collect(toList()));
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam(CARD_ID) Long cardId, WsRelationData relationData) {
        relationData = relationData.getDataDirect();
        CMRelation relation = RelationImpl.builder()
                .withType(dao.getDomain(relationData.getDomainType()))
                .withSourceCard(dao.getCard(relationData.getSourceClassId(), relationData.getSourceCardId()))
                .withTargetCard(dao.getCard(relationData.getDestinationClassId(), relationData.getDestinationCardId()))
                .addAttributes(relationData.getValues())
                .build();

        relation = dao.create(relation);
        relation = relation.getRelationWithSource(cardId);

        return response(helper.serializeDetailedRelation(relation));
    }

    @PUT
    @Path("{" + RELATION_ID + "}/")
    public Object update(@PathParam(CARD_ID) Long cardId, @PathParam(RELATION_ID) Long relationId, WsRelationData relationData) {
        relationData = relationData.getDataDirect();
        CMRelation relation = dao.getRelation(relationData.getDomainType(), relationId);

        relation = RelationImpl.copyOf(relation)
                .withSourceCard(dao.getCard(relationData.getSourceClassId(), relationData.getSourceCardId()))
                .withTargetCard(dao.getCard(relationData.getDestinationClassId(), relationData.getDestinationCardId()))
                .addAttributes(relationData.getValues())
                .build();

        relation = dao.update(relation);
        relation = relation.getRelationWithSource(cardId);

        return response(helper.serializeDetailedRelation(relation));
    }

    @DELETE
    @Path("{" + RELATION_ID + "}/")
    public Object delete(@PathParam(RELATION_ID) Long relationId) {
        CMRelation relation = dao.getRelation(relationId);
        dao.delete(relation);
        return success();
    }

    public static class WsRelationData {

        private final boolean isDirect;
        private final Long id, sourceId, destinationId;
        private final String sourceType, destinationType, domainType;
        @JsonAnySetter
        private final Map<String, Object> values = map();

        public WsRelationData(
                @JsonProperty("_id") Long id,
                @JsonProperty("_type") String domainType,
                @JsonProperty("_sourceType") String sourceType,
                @JsonProperty("_sourceId") Long sourceId,
                @JsonProperty("_destinationType") String destinationType,
                @JsonProperty("_destinationId") Long destinationId,
                @JsonProperty("_is_direct") Boolean isDirect) {
            this.id = ltEqZeroToNull(id);
            this.domainType = checkNotBlank(domainType, "missing '_type' param");
            this.sourceId = checkNotNullAndGtZero(sourceId, "missing '_sourceId' param");
            this.sourceType = checkNotBlank(sourceType, "missing '_sourceType' param");
            this.destinationId = checkNotNullAndGtZero(destinationId, "missing '_destinationId' param");
            this.destinationType = checkNotBlank(destinationType, "missing '_destinationType' param");
            this.isDirect = firstNonNull(isDirect, true);
        }

        @Nullable
        public Long getId() {
            return id;
        }

        public Long getSourceCardId() {
            return sourceId;
        }

        public String getSourceClassId() {
            return sourceType;
        }

        public Long getDestinationCardId() {
            return destinationId;
        }

        public String getDestinationClassId() {
            return destinationType;
        }

        public Map<String, Object> getValues() {
            return values;
        }

        public String getDomainType() {
            return domainType;
        }

        public boolean isDirect() {
            return isDirect;
        }

        public WsRelationData getDataDirect() {
            if (isDirect) {
                return this;
            } else {
                WsRelationData res = new WsRelationData(id, domainType, destinationType, destinationId, sourceType, sourceId, true);
                res.getValues().putAll(values);
                return res;
            }
        }

    }
}
