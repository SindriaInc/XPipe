package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.beans.RelationImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CREATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_SERVICE;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v3.endpoint.CardRelationWs.WsRelationData;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelperv3;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

@Path("domains/{domainId}/relations")
@Produces(APPLICATION_JSON)
public class RelationWs {

    private final UserDomainService domainService;
    private final DaoService dao;
    private final CardWsSerializationHelperv3 helper;

    public RelationWs(UserDomainService domainService, DaoService dao, CardWsSerializationHelperv3 helper) {
        this.domainService = checkNotNull(domainService);
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@PathParam("domainId") String domainId, WsQueryOptions queryOptions) {
        return response(domainService.getUserRelations(domainId, queryOptions.getQuery()).map(queryOptions.isDetailed() ? helper::serializeDetailedRelation : helper::serializeMinimalRelation));
    }

    @GET
    @Path("{relationId}/")
    public Object read(@PathParam("domainId") String domainId, @PathParam("relationId") Long relationId) {
        return response(helper.serializeDetailedRelation(domainService.getUserRelation(domainId, relationId)));
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("domainId") String domainId, WsRelationData relationData) {
        Domain domain = domainService.getUserDomain(domainId);
        domain.checkPermission(PS_SERVICE, CP_CREATE);
        relationData = relationData.getDataDirect();
        CMRelation relation = RelationImpl.builder()
                .withType(domain)
                .withSourceCard(dao.getCard(relationData.getSourceClassId(), relationData.getSourceCardId()))
                .withTargetCard(dao.getCard(relationData.getDestinationClassId(), relationData.getDestinationCardId()))
                .addAttributes(relationData.getValues())
                .build();
        relation = dao.create(relation);
        return response(helper.serializeDetailedRelation(relation));
    }

    @PUT
    @Path("{relationId}/")
    public Object update(@PathParam("domainId") String domainId, @PathParam("relationId") Long relationId, WsRelationData relationData) {
        CMRelation relation = domainService.getUserRelation(domainId, relationId);
        relation.getType().checkPermission(PS_SERVICE, CP_UPDATE);
        relationData = relationData.getDataDirect();
        relation = RelationImpl.copyOf(relation)
                .withSourceCard(dao.getCard(relationData.getSourceClassId(), relationData.getSourceCardId()))
                .withTargetCard(dao.getCard(relationData.getDestinationClassId(), relationData.getDestinationCardId()))
                .addAttributes(relationData.getValues())
                .build();
        relation = dao.update(relation);
        return response(helper.serializeDetailedRelation(relation));
    }

    @DELETE
    @Path("{relationId}/")
    public Object delete(@PathParam("domainId") String domainId, @PathParam("relationId") Long relationId) {
        CMRelation relation = domainService.getUserRelation(domainId, relationId);
        relation.getType().checkPermission(PS_SERVICE, CP_DELETE);
        dao.delete(relation);
        return success();
    }

    @POST
    @Path("_ANY/move")
    public Object moveManyRelations(@PathParam("domainId") String domainId, WsRelationCopyParams params) {
        checkArgument(equal(domainId, "_ANY"), "domain id path param must be set to '_ANY'");
        copyOrMoveManyRelations(params, false);
        return success();
    }

    @POST
    @Path("_ANY/copy")
    public Object copyManyRelations(@PathParam("domainId") String domainId, WsRelationCopyParams params) {
        checkArgument(equal(domainId, "_ANY"), "domain id path param must be set to '_ANY'");
        copyOrMoveManyRelations(params, true);
        return success();
    }

    public void copyOrMoveManyRelations(WsRelationCopyParams params, boolean copy) {
        List<WsDomainAndDirectionInfo> domainInfo = params.domains;
        long sourceCardId = params.sourceCardId, destinationCardId = params.destinationCardId;
        if (isNullOrEmpty(domainInfo)) {
            List<WsDomainAndDirectionInfo> list = domainInfo = list();
            domainService.getUserRelationsForCard(dao.getCard(sourceCardId).getClassName(), sourceCardId, DaoQueryOptionsImpl.emptyOptions()).stream()
                    .map(r -> new WsDomainAndDirectionInfo(r.getType().getName(), r.getDirection()))
                    .filter(i -> list.stream().noneMatch(ii -> equal(ii.direction, i.direction) && equal(ii.domainId, i.domainId)))
                    .forEach(list::add);
        }
        domainInfo.forEach(d -> {
            if (copy) {
                domainService.copyManyRelations(sourceCardId, destinationCardId, d.domainId, d.direction);
            } else {
                domainService.moveManyRelations(sourceCardId, destinationCardId, d.domainId, d.direction);
            }
        });
    }

    public static class WsRelationCopyParams {

        private final List<WsDomainAndDirectionInfo> domains;

        private final long sourceCardId, destinationCardId;

        public WsRelationCopyParams(
                @JsonProperty("domains") List<WsDomainAndDirectionInfo> domains,
                @JsonProperty("source") Long sourceCardId,
                @JsonProperty("destination") Long destinationCardId) {
            this.domains = nullToEmpty(domains);
            this.sourceCardId = checkNotNullAndGtZero(sourceCardId);
            this.destinationCardId = checkNotNullAndGtZero(destinationCardId);
        }

    }

    public static class WsDomainAndDirectionInfo {

        private final String domainId;
        private final RelationDirection direction;

        @JsonCreator
        public WsDomainAndDirectionInfo(@JsonProperty("_id") String id, @JsonProperty("direction") String direction) {
            this(id, parseEnum(direction, RelationDirection.class));
        }

        public WsDomainAndDirectionInfo(String domainId, RelationDirection direction) {
            this.domainId = checkNotBlank(domainId);
            this.direction = checkNotNull(direction);
        }

    }
}
