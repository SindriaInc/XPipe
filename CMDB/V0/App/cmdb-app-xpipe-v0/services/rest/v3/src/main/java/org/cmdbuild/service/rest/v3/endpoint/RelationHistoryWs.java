package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.classe.access.CardHistoryService;

import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelperv3;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DOMAIN_ID;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;

@Path("domains/{domainId}/relations")
@Produces(APPLICATION_JSON)
public class RelationHistoryWs {

    private final UserDomainService domainService;
    private final DaoService dao;
    private final CardWsSerializationHelperv3 helper;
    private final CardHistoryService service;

    public RelationHistoryWs(UserDomainService domainService, DaoService dao, CardWsSerializationHelperv3 helper, CardHistoryService service) {
        this.domainService = checkNotNull(domainService);
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
        this.service = checkNotNull(service);
    }

    @GET
    @Path("history/{relationId}")
    public Object getRelationHistoryRecord(@PathParam(DOMAIN_ID) String domainId, @PathParam(CARD_ID) Long id, @PathParam("relationId") Long relationId) {
        CMRelation rel = service.getRelationHistoryRecord(domainId, relationId);
        return response(helper.serializeDetailedRelation(rel).with(
                "_endDate", toIsoDateTime(rel.getEndDate()),
                "_status", rel.getCardStatus().name()));
    }

}
