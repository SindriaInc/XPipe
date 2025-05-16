package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.regex.Pattern;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserDomainService;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.cmdbuild.dao.driver.postgres.q3.stats.DaoStatsQueryOptionsUtils;
import org.cmdbuild.dao.driver.postgres.q3.stats.StatsQueryResponse;
import static org.cmdbuild.dao.utils.DomainUtils.getActualCascadeAction;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

@Path("{a:classes}/{" + CLASS_ID + "}/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ClassStatsWs {

    private final UserClassService classService;
    private final UserDomainService domainService;
    private final UserCardService cardService;
    private final CardWsSerializationHelperv3 helper;

    public ClassStatsWs(UserClassService classService, UserDomainService domainService, UserCardService cardService, CardWsSerializationHelperv3 helper) {
        this.classService = checkNotNull(classService);
        this.domainService = checkNotNull(domainService);
        this.cardService = checkNotNull(cardService);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path("{b:stats}")
    public Object stats(@PathParam(CLASS_ID) String classId, WsQueryOptions wsQueryOptions, @QueryParam("select") String select) {
        Classe classe = classService.getUserClass(classId);
        StatsQueryResponse response = cardService.getStats(classId, wsQueryOptions.getQuery(), DaoStatsQueryOptionsUtils.statsQueryOptionsFromJson(select));
        return response(map("aggregate", response.getAggregateResults().stream().map(r -> map("attribute", r.getAttribute(), "operation", serializeEnum(r.getOperation())).accept(m -> {
            helper.serializeAttributeValue(classe, r.getAttribute(), r.getResult()).mapKeys(k -> k.replaceFirst(Pattern.quote(r.getAttribute()), "result")).forEach(m::put);
        })).collect(toImmutableList())));
    }

    @GET
    @Path("{b:relations}")
    public Object relations(@PathParam(CLASS_ID) String classId, WsQueryOptions wsQueryOptions) {
        return response(list(domainService.getRelationsStats(classId, wsQueryOptions.getQuery().getFilter())).map(s -> map(
                "domain", s.getDomain().getName(),
                "direction", serializeEnum(s.getDirection()),
                "cardinality", serializeDomainCardinality(s.getDomain().getCardinality()),
                "cascadeAction", s.getDirection().equals(RD_DIRECT)
                ? serializeEnum(getActualCascadeAction(s.getDomain(), s.getDomain().getMetadata().getCascadeActionDirect(), RD_DIRECT))
                : serializeEnum(getActualCascadeAction(s.getDomain(), s.getDomain().getMetadata().getCascadeActionInverse(), RD_INVERSE)),
                "count", s.getRelationCount()
        )));
    }

}
