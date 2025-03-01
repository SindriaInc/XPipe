package org.cmdbuild.service.rest.v3.endpoint;

import com.google.common.base.Function;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.classe.access.UserCardQueryForDomain;
import org.cmdbuild.classe.access.UserCardQueryForDomainImpl;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.DaoService.COUNT;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import static org.cmdbuild.dao.core.q3.WhereOperator.ISNOTNULL;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.dao.utils.SorterProcessor.sorted;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.helpers.CardsForDomainFetcher;
import org.cmdbuild.service.rest.common.serializationhelpers.CompositeDataSerializer;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardSerializer;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardSerializerEnhancer_Stats;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardSerializerEnhancer_Widgets;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardSerializer_WithModel;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import org.cmdbuild.service.rest.v3.model.WsCardData;
import static org.cmdbuild.service.rest.v3.utils.PositionOfUtils.handlePositionOfAndGetMeta;
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
    private final CardsForDomainFetcher cardsForDomainFetcher;

    public CardWs(UserClassService classService, UserCardService cardService, DaoService dao, CardWsSerializationHelperv3 helper, DmsService dmsService, CardsForDomainFetcher cardsForDomainFetcher) {
        this.classService = checkNotNull(classService);
        this.cardService = checkNotNull(cardService);
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
        this.dmsService = checkNotNull(dmsService);
        this.cardsForDomainFetcher = checkNotNull(cardsForDomainFetcher);
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
        CardSerializer cardSerializer = new CardSerializer(helper);
        if (infoOnly) {
            return response(cardSerializer.serialize(cardService.getUserCardInfo(classId, cardId)));
        }

        Card card = cardService.getUserCard(classId, cardId);

        if (includeModel) {
            cardSerializer = new CardSerializer_WithModel(helper);
        }
        FluentMap<String, Object> serialization = new CompositeDataSerializer(cardSerializer)
                .addEnhancer(new CardSerializerEnhancer_Widgets(includeWidgets, helper))
                .addEnhancer(new CardSerializerEnhancer_Stats(includeStats, dmsService, dao))
                .serialize(card);

        return response(serialization);
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

        if (isNotBlank(distinctAttribute)) {//TODO move count, distinct etc to /statistics endpoint (3.3)
            DaoQueryOptions tqueryOptions = queryOptions.mapAttrNames(dao.getClasse(classId).getAliasToAttributeMap()).expandFulltextFilter(classe);
            List<Map<String, Object>> list = loadDistinctAttributeValues(classe, tqueryOptions, selectFunctionValue, distinctIncludeNull, distinctAttribute, countAttribute);

            return response(paged(list, tqueryOptions.getOffset(), tqueryOptions.getLimit()));
        }

        UserCardQueryForDomain forDomain = wsForDomainOptions.getForDomain();
        PagedElements<Card> cards = cardsForDomainFetcher.fetchCards(forDomain, classId, queryOptions, selectFunctionValue);
        final List<Map<String, Object>> cardsForDomain = cardsForDomainFetcher.fetchCardsForDomain(forDomain, cards, queryOptions, selectFunctionValue);

        return response(cardsForDomain, cards.totalSize(), handlePositionOfAndGetMeta(queryOptions, cards));
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
            JsonPrimitive filterId = JsonParser.parseString(filter).getAsJsonObject().getAsJsonPrimitive("_id");
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

    private List<Map<String, Object>> loadDistinctAttributeValues(Classe classe, DaoQueryOptions queryOptions, String selectFunctionValue, Boolean distinctIncludeNull, String distinctAttribute, String countAttribute) {
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
        }).from(classe.getName()).where(queryOptions.getFilter()).run().stream().map(r -> (distinctOnFunctionValue ? helper.serializeAttributeValue(getSelectFunctionValueStoredFunction(selectFunctionValue).getOnlyOutputParameter(), r.asMap()) : helper.serializeAttributeValue(classe, distinctAttribute, r.asMap())).accept(m -> {
            if (count) {
                m.put("_count", r.get(COUNT, Long.class));
            }
        })).collect(toList());
        list = sorted(list, queryOptions.getSorter());
        return list;
    }

    public static class WsForDomainOptions {

        private final UserCardQueryForDomain forDomain;

        public WsForDomainOptions(@QueryParam("forDomain_name") String forDomainName, @QueryParam("forDomain_direction") String forDomainDirection, @QueryParam("forDomain_originId") Long forDomainOriginId, @QueryParam("forDomain_all") Boolean forDomainAll) {
            if (isBlank(forDomainName)) {
                forDomain = null;
            } else {
                forDomain = UserCardQueryForDomainImpl.builder()
                        .withDomainName(forDomainName)
                        .withDirection(parseEnumOrNull(forDomainDirection, RelationDirection.class))
                        .withOriginId(forDomainOriginId)
                        .withAll(forDomainAll)
                        .build();
            }
        }

        public UserCardQueryForDomain getForDomain() {
            return forDomain;
        }

    }
}
