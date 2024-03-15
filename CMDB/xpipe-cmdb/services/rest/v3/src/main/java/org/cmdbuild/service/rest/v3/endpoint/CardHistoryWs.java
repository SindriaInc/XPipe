package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.stream.Stream;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.classe.access.CardHistoryService.HistoryElement;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.common.serializationhelpers.HistorySerializationHelper;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmPreconditions.checkSingleElement;

@Path("{a:processes|classes}/{" + CLASS_ID + "}/{b:cards|instances}/{" + CARD_ID + "}/history")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardHistoryWs {

    private final CardHistoryService service;
    private final CardWsSerializationHelperv3 cardHistoryHelper;
    private final HistorySerializationHelper historyHelper;

    public CardHistoryWs(CardHistoryService service, CardWsSerializationHelperv3 helper, HistorySerializationHelper historyHelper) {
        this.service = checkNotNull(service);
        this.cardHistoryHelper = checkNotNull(helper);
        this.historyHelper = checkNotNull(historyHelper);
    }

    @GET
    @Path("")
    public Object getHistory(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, WsQueryOptions wsQueryOptions, @QueryParam("types") @DefaultValue("cards") String types) {
        DaoQueryOptions queryOptions = buildHistoryDaoQueryOptions(wsQueryOptions);
        List<HistoryElement> historyTypes = service.fetchHistoryTypes(types);
        PagedElements<DatabaseRecord> history = service.getHistoryElements(classId, cardId, queryOptions, historyTypes);
        return response(history.stream().map(h -> wsQueryOptions.isDetailed() ? getHistoryRecord(classId, cardId, h.getId()) : historyHelper.serializeBasicHistory(h)), history.totalSize());
    }

    /**
     * History for given card attribute changes.
     *
     * <b>Note</b>: pagination not handled, because all values have to be actually loaded.
     *
     * @param classId
     * @param cardId
     * @param wsQueryOptions
     * @param types
     * @return
     */
    @GET
    @Path("changes")
    public Object getHistoryOnlyChanges(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, WsQueryOptions wsQueryOptions, @QueryParam("types") @DefaultValue("cards") String types) {
        String attr = checkSingleElement(wsQueryOptions.getQuery().getAttrs(), "attrs");
        DaoQueryOptions queryOptions = buildHistoryDaoQueryOptions(wsQueryOptions).withoutPaging();
        List<HistoryElement> historyTypes = service.fetchHistoryTypes(types);
        List<Card> history = service.getHistoryElementsOnlyChanges(classId, cardId, queryOptions, historyTypes);

        Stream data = history.stream().map(h -> historyHelper.serializeBasicHistory(h).with(serializeAttributeValue(h, attr))); // Add chosen attribute detail

        return response(data, history.size());
    }

    @GET
    @Path("{recordId}")
    public Object getHistoryRecord(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long id, @PathParam("recordId") Long recordId) {
        Card record = service.getHistoryRecord(classId, recordId);
        checkArgument(equal(record.getCurrentId(), id));
        return response(cardHistoryHelper.serializeCard(record).with(
                "_endDate", toIsoDateTime(record.getEndDate()),
                "_status", record.getCardStatus().name()));
    }

    /**
     *
     * @param wsQueryOptions (optionally) with pagination information
     * @return
     */
    private DaoQueryOptions buildHistoryDaoQueryOptions(WsQueryOptions wsQueryOptions) {
        DaoQueryOptionsImpl query = DaoQueryOptionsImpl.copyOf(wsQueryOptions.getQuery())
                .withPaging(wsQueryOptions.getOffset(), wsQueryOptions.getLimit())
                .withFilter(wsQueryOptions.getQuery().getFilter())
                .accept(q -> {
                    if (wsQueryOptions.getQuery().getSorter().isNoop()) {
                        q.withSorter(CmdbSorterImpl.sorter(ATTR_BEGINDATE, DESC));
                    }
                })
                .build();
        return query;
    }

    private CmMapUtils.FluentMap<String, Object> serializeAttributeValue(Card card, String attr) {
        return cardHistoryHelper.serializeAttributeValue(card.getType(), attr, card.get(attr));
    }

}
