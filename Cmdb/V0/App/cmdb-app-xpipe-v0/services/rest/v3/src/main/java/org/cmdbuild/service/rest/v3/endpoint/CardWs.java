package org.cmdbuild.service.rest.v3.endpoint;

import com.google.common.base.Function;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import static java.lang.String.format;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
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
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.classe.access.UserCardQueryForDomain;
import org.cmdbuild.classe.access.UserCardQueryForDomainImpl;
import org.cmdbuild.classe.access.UserCardQueryOptions;
import org.cmdbuild.classe.access.UserCardQueryOptionsImpl;
import org.cmdbuild.classe.access.UserCardService;
import static org.cmdbuild.classe.access.UserCardService.FOR_DOMAIN_HAS_ANY_RELATION;
import static org.cmdbuild.classe.access.UserCardService.FOR_DOMAIN_HAS_THIS_RELATION;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.DaoService.COUNT;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.ISNOTNULL;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_MANY;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.dao.utils.SorterProcessor.sorted;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.email.Email.EMAIL_ATTR_CARD;
import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelperv3.ExtendedCardOptions;
import static org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelperv3.ExtendedCardOptions.INCLUDE_MODEL;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.v3.endpoint.ProcessTaskWs.handlePositionOfAndGetMeta;
import org.cmdbuild.service.rest.v3.model.WsCardData;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

@Path("{a:classes}/{" + CLASS_ID + "}/{b:cards}/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardWs {

    private final UserClassService classService;
    private final UserCardService cardService;
    private final DaoService dao;
    private final CardWsSerializationHelperv3 helper;
    private final DmsService dmsService;

    public CardWs(UserClassService classService, UserCardService cardService, DaoService dao, CardWsSerializationHelperv3 helper, DmsService dmsService) {
        this.classService = checkNotNull(classService);
        this.cardService = checkNotNull(cardService);
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
        this.dmsService = checkNotNull(dmsService);
    }

    @GET
    @Path("{" + CARD_ID + "}/")
    public Object readOne(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @QueryParam("includeModel") @DefaultValue(FALSE) Boolean includeModel,
            @QueryParam("includeWidgets") @DefaultValue(FALSE) Boolean includeWidgets,
            @QueryParam("includeStats") @DefaultValue(FALSE) Boolean includeStats,
            @QueryParam("infoOnly") @DefaultValue(FALSE) Boolean infoOnly) {
        if (infoOnly) {
            return response(helper.serializeCard(cardService.getUserCardInfo(classId, cardId)));
        } else {
            Card card = cardService.getUserCard(classId, cardId);
            Set<ExtendedCardOptions> extendedCardOptions = EnumSet.noneOf(ExtendedCardOptions.class);
            if (includeModel) {
                extendedCardOptions.add(INCLUDE_MODEL);
            }
            FluentMap<String, Object> map = helper.serializeCard(card, extendedCardOptions);
            if (includeWidgets) {
                map.accept(helper.serializeWidgets(card));
            }
            if (includeStats) {
                map.put("_attachment_count", dmsService.getCardAttachmentCountSafe(card),
                        "_email_count", dao.selectCount().from(EMAIL_CLASS_NAME).where(EMAIL_ATTR_CARD, EQ, cardId).getCount()
                );
            }
            return response(map);
        }
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam(CLASS_ID) String classId,
            WsQueryOptions wsQueryOptions,
            WsForDomainOptions wsForDomainOptions,
            @QueryParam("functionValue") String selectFunctionValue,
            @QueryParam("distinctIncludeNull") @DefaultValue(FALSE) Boolean distinctIncludeNull,
            @QueryParam("distinct") String distinctAttribute,
            @QueryParam("count") String countAttribute) {

        Classe classe = classService.getUserClass(classId);

        DaoQueryOptions queryOptions = wsQueryOptions.getQuery();

        if (wsQueryOptions.getQuery().getOnlyGridAttrs()) {
            checkArgument(!wsQueryOptions.getQuery().hasAttrs(), "use attrs or onlyGridAttrs, cannot be used at the same time");
            Set<String> setAttributes = classe.getCoreAttributes().stream().filter(Attribute::isActive).filter(not(Attribute::isVirtual)).filter(not(Attribute::isHiddenInGrid)).map(Attribute::getName).collect(toSet());
            if (!setAttributes.isEmpty()) {
                queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).withAttrs(setAttributes).build();
            }
        }

        UserCardQueryForDomain forDomain = wsForDomainOptions.getForDomain();

        if (isNotBlank(distinctAttribute)) {//TODO move count, distinct etc to /statistics endpoint (3.3)
            DaoQueryOptions tqueryOptions = queryOptions.mapAttrNames(dao.getClasse(classId).getAliasToAttributeMap()).expandFulltextFilter(classe);
            //TODO order, card level permissions
            boolean count;
            QueryBuilder query;
            if (isNotBlank(countAttribute)) {
                checkArgument(equal(countAttribute, distinctAttribute), "count attribute must match distinct attribute");
                count = true;
            } else {
                count = false;
            }
            boolean distinctOnFunctionValue = isNotBlank(selectFunctionValue) && equal(getSelectFunctionValueStoredFunction(selectFunctionValue).getOnlyOutputParameter().getName(), distinctAttribute);
            if (distinctOnFunctionValue) {
                query = dao.selectDistinctExpr(getSelectFunctionValueStoredFunction(selectFunctionValue).getOnlyOutputParameter().getName(), format("%s(\"Id\")", quoteSqlIdentifier(getSelectFunctionValueStoredFunction(selectFunctionValue).getName())));//TODO duplicate code, improve this
            } else {
                query = dao.selectDistinct(distinctAttribute);
            }
            List<Map<String, Object>> list = (List) query.accept(q -> {
                if (count) {
                    q.selectCount();
                }
                if (!distinctIncludeNull) {
                    q.where(distinctAttribute, ISNOTNULL);
                }
            }).from(classId).where(tqueryOptions.getFilter()).run().stream().map(r -> (distinctOnFunctionValue ? helper.serializeAttributeValue(getSelectFunctionValueStoredFunction(selectFunctionValue).getOnlyOutputParameter(), r.asMap()) : helper.serializeAttributeValue(classe, distinctAttribute, r.asMap())).accept(m -> {
                if (count) {
                    m.put("_count", r.get(COUNT, Long.class));
                }
            })).collect(toList());

            list = sorted(list, tqueryOptions.getSorter());
            return response(paged(list, tqueryOptions.getOffset(), tqueryOptions.getLimit()));
        }

        UserCardQueryOptions userQueryOptions = UserCardQueryOptionsImpl.builder()
                .withQueryOptions(queryOptions)
                .withForDomain(forDomain)
                .withFunctionValue(selectFunctionValue)
                .build();

        PagedElements<Card> cards = cardService.getUserCards(classId, userQueryOptions);

        return response(cards.stream().map(c -> helper.serializeCard(c, userQueryOptions.getQueryOptions()).accept((m) -> {//TODO move this in getUserCard method
            if (forDomain != null) {
                Domain dom = dao.getDomain(forDomain.getDomainName()).getThisDomainWithDirection(forDomain.getDirection());
                boolean hasAnyRelation = c.get(FOR_DOMAIN_HAS_ANY_RELATION, Boolean.class),
                        hasThisRelation = c.get(FOR_DOMAIN_HAS_THIS_RELATION, Boolean.class),
                        available = (!hasThisRelation || dom.hasDomainKeyAttrs()) && dom.isDomainForTargetClasse(c.getType()) && (!hasAnyRelation || (dom.hasCardinality(MANY_TO_ONE) || dom.hasCardinality(MANY_TO_MANY)));
                m.put(format("_%s_available", dom.getName()), available, format("_%s_hasrelation", dom.getName()), hasThisRelation);
            }
            if (isNotBlank(selectFunctionValue)) {
                Attribute param = dao.getFunctionByName(selectFunctionValue).getOnlyOutputParameter();
                helper.addCardValuesAndDescriptionsAndExtras(param.getName(), param.getType(), c::get, m::put);
            }
        })).collect(toList()), cards.totalSize(), handlePositionOfAndGetMeta(queryOptions, cards));
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam(CLASS_ID) String classId, WsCardData data) {
        return response(helper.serializeCard(cardService.createCard(classId, data.getValues())));
    }

    @PUT
    @Path("{" + CARD_ID + "}/")
    public Object update(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, WsCardData data) {
        return response(helper.serializeCard(cardService.updateCard(classId, cardId, data.getValues())));
    }

    @DELETE
    @Path("{" + CARD_ID + "}/")
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId) {
        cardService.deleteCard(classId, cardId);
        return success();
    }

    @PUT
    @Path("")
    public Object updateMany(@PathParam(CLASS_ID) String classId, WsCardData data, WsQueryOptions wsQueryOptions) {
        cardService.updateCards(classId, wsQueryOptions.getQuery().getFilter(), data.getValues());
        return success();
    }

    @DELETE
    @Path("")
    public Object deleteMany(@PathParam(CLASS_ID) String classId, WsQueryOptions wsQueryOptions) {
        cardService.deleteCards(classId, wsQueryOptions.getQuery().getFilter());
        return success();
    }

    @Nullable
    public static String getFilterOrNull(@Nullable String filter, Function<Long, String> filterRepo) {
        if (isBlank(filter)) {
            return null;
        } else {
            JsonPrimitive filterId = new JsonParser().parse(filter).getAsJsonObject().getAsJsonPrimitive("_id");
            if (filterId != null && !filterId.isJsonNull()) {
                return filterRepo.apply(filterId.getAsLong());
            } else {
                return filter;
            }
        }
    }

    private StoredFunction getSelectFunctionValueStoredFunction(String selectFunctionValue) {//TODO duplicate code
        StoredFunction storedFunction = dao.getFunctionByName(selectFunctionValue);//TODO check fun permission
        checkArgument(storedFunction.hasOnlyOneOutputParameter());//TODO
        return storedFunction;
    }

    public static class WsForDomainOptions {

        private final UserCardQueryForDomain forDomain;

        public WsForDomainOptions(@QueryParam("forDomain_name") String forDomainName, @QueryParam("forDomain_direction") String forDomainDirection, @QueryParam("forDomain_originId") Long forDomainOriginId) {
            if (isBlank(forDomainName)) {
                forDomain = null;
            } else {
                forDomain = UserCardQueryForDomainImpl.builder()
                        .withDomainName(forDomainName)
                        .withDirection(parseEnumOrNull(forDomainDirection, RelationDirection.class))
                        .withOriginId(forDomainOriginId)
                        .build();
            }
        }

        public UserCardQueryForDomain getForDomain() {
            return forDomain;
        }

    }
}
