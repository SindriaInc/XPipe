package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static java.lang.String.format;
import java.util.List;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import jakarta.activation.DataHandler;
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
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.calendar.beans.CalendarEvent;
import static org.cmdbuild.calendar.beans.CalendarEvent.EVENT_TABLE;
import static org.cmdbuild.calendar.data.CalendarEventRepositoryImpl.addCalendarEventUserFilter;
import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.classe.access.CardHistoryService.HistoryElement;
import static org.cmdbuild.common.Constants.DMS_MODEL_PARENT_CLASS;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.orm.CardMapperService;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_CARD;
import static org.cmdbuild.email.Email.EMAIL_ATTR_CARD;
import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import org.cmdbuild.report.SysReportService;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF_GOTOPAGE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper;
import static org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper.CAL_ATTR_MAPPING;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.common.serializationhelpers.HistorySerializationHelper;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.service.rest.v3.endpoint.ClassPrintWs.buildQueryOptions;
import org.cmdbuild.service.rest.v3.model.WsEventData;
import static org.cmdbuild.service.rest.v3.utils.PositionOfUtils.handlePositionOfAndGetMeta;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

@Path("calendar/events/")
@Produces(APPLICATION_JSON)
public class CalendarEventWs {

    private final CalendarService service;
    private final CalendarWsSerializationHelper helper;
    private final CardWsSerializationHelperv3 cardHelper;
    private final CardHistoryService historyService;
    private final HistorySerializationHelper historyHelper;
    private final CardMapperService mapper;
    private final OperationUserSupplier operationUser;
    private final DaoService dao;
    private final SysReportService reportService;

    public CalendarEventWs(CalendarService service, CalendarWsSerializationHelper helper, CardWsSerializationHelperv3 cardHelper, CardHistoryService historyService, HistorySerializationHelper historyHelper, CardMapperService mapper, OperationUserSupplier operationUser, DaoService dao, SysReportService reportService) {
        this.service = checkNotNull(service);
        this.helper = checkNotNull(helper);
        this.cardHelper = checkNotNull(cardHelper);
        this.historyService = checkNotNull(historyService);
        this.historyHelper = checkNotNull(historyHelper);
        this.mapper = checkNotNull(mapper);
        this.operationUser = checkNotNull(operationUser);
        this.dao = checkNotNull(dao);
        this.reportService = checkNotNull(reportService);
    }

    @GET
    @Path("{eventId}")
    public Object readOne(@PathParam("eventId") Long eventId, @QueryParam("includeStats") @DefaultValue(FALSE) Boolean includeStats) {
        CalendarEvent event = service.getUserEvent(eventId);
        return response(helper.serializeDetailedEvent(event).accept(m -> {
            if (includeStats) {
                m.put("_attachment_count", dao.selectCount().from(DMS_MODEL_PARENT_CLASS).where(DOCUMENT_ATTR_CARD, EQ, event.getId()).getCount(), //TODO: duplicate code, improve this
                        "_email_count", dao.selectCount().from(EMAIL_CLASS_NAME).where(EMAIL_ATTR_CARD, EQ, event.getId()).getCount());
            }
        }));
    }

    @GET
    @Path(EMPTY)
    public Object readMany(
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed,
            @QueryParam(POSITION_OF) Long positionOf,
            @QueryParam(POSITION_OF_GOTOPAGE) @DefaultValue(TRUE) Boolean goToPage) {//TODO improve this, auto processing of params in dao query options
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder().withFilter(filterStr).withSorter(sort).withPositionOf(positionOf, goToPage).withPaging(offset, limit).build().mapAttrNames(CAL_ATTR_MAPPING);
        PagedElements<CalendarEvent> events = service.getUserEvents(queryOptions);
        return response(events.map(detailed ? helper::serializeDetailedEvent : helper::serializeEvent), handlePositionOfAndGetMeta(queryOptions, events));
    }

    @POST
    @Path("")
    public Object createUserEvent(WsEventData data) {
        CalendarEvent event = data.buildEvent().accept((Consumer) service.fixTimeZone()).withOwner(operationUser.getUsername()).build();
        checkArgument(!event.hasSequence(), "cannot create standalone sequence event");
        event = service.createUserEvent(event);//TODO access control, etc
        return response(helper.serializeDetailedEvent(event));
    }

    @PUT
    @Path("{eventId}")
    public Object update(@PathParam("eventId") Long eventId, WsEventData data) {
        CalendarEvent event = service.updateEvent(data.buildEvent().accept((Consumer) service.fixTimeZone()).withId(eventId).build());//TODO access control, etc
        return response(helper.serializeDetailedEvent(event));
    }

    @DELETE
    @Path("{eventId}/")
    public Object delete(@PathParam("eventId") Long eventId) {
        service.deleteEvent(eventId);
        return success();
    }

    @GET
    @Path("{eventId}/history")
    public Object getHistory(@PathParam("eventId") Long eventId, WsQueryOptions wsQueryOptions, @QueryParam("types") @DefaultValue("cards") String types) {
        DaoQueryOptionsImpl query = DaoQueryOptionsImpl.copyOf(wsQueryOptions.getQuery())
                .withPaging(wsQueryOptions.getOffset(), wsQueryOptions.getLimit())
                .withFilter(wsQueryOptions.getQuery().getFilter())
                .accept(q -> {
                    if (wsQueryOptions.getQuery().getSorter().isNoop()) {
                        q.withSorter(CmdbSorterImpl.sorter(ATTR_BEGINDATE, DESC));
                    }
                }).build();
        List<HistoryElement> historyTypes = Splitter.on(",").splitToList(types).stream().map(e -> parseEnumOrNull(e, CardHistoryService.HistoryElement.class)).collect(toList());
        PagedElements<DatabaseRecord> history = historyService.getHistoryElements(CalendarEvent.EVENT_TABLE, eventId, query, historyTypes);
        return response(history.stream().map(historyHelper::serializeBasicHistory), history.totalSize());
    }

    @GET
    @Path("{eventId}/history/{recordId}")
    public Object getHistoryRecord(@PathParam("eventId") Long eventId, @PathParam("recordId") Long recordId) {
        Card record = historyService.getHistoryRecord(CalendarEvent.EVENT_TABLE, recordId);
        checkArgument(equal(record.getCurrentId(), eventId));//TODO improve this; access control
        return response(helper.serializeDetailedEvent(mapper.cardToObject(record)).with(
                "_endDate", toIsoDateTime(record.getEndDate()),//TODO duplicate code from history ws, improve this
                "_status", record.getCardStatus().name())
                .accept(m -> {//TODO move this into calendar helper, add previuos?
                    String matcher = "^_(.+)(_changed)";
                    record.getRawValues().forEach(a -> {
                        if (a.getKey().matches(matcher)) {
                            String attr = a.getKey().replaceAll(matcher, "$1");
                            String value = a.getKey().replaceAll(matcher, "$2");
                            switch (attr) {
                                case "EventDate" ->
                                    m.put(format("_date%s", value), a.getValue());
                                case "Description" ->
                                    m.put(format("_description%s", value), a.getValue());
                                case "Content" ->
                                    m.put(format("_content%s", value), a.getValue());
                                case "Category" ->
                                    m.put(format("_category%s", value), a.getValue());
                                case "Priority" ->
                                    m.put(format("_priority%s", value), a.getValue());
                                case "EventStatus" ->
                                    m.put(format("_status%s", value), a.getValue());
                                case "EventType" ->
                                    m.put(format("_type%s", value), a.getValue());
                                case "Notes" ->
                                    m.put(format("_notes%s", value), a.getValue());
                            }
                        }
                    });
                }));
    }

    @GET
    @Path("/print/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printCalendarEventReport(WsQueryOptions wsQueryOptions, @QueryParam(EXTENSION) String extension, @QueryParam("attributes") String attributes) {
        Classe classe = dao.getClasse(EVENT_TABLE);
        DaoQueryOptions queryOptions = buildQueryOptions(classe, wsQueryOptions, attributes).mapAttrNames(CAL_ATTR_MAPPING);
        return reportService.executeUserClassReport(classe, reportExtFromString(extension), queryOptions, () -> dao.selectAll().from(classe).withOptions(queryOptions).accept(addCalendarEventUserFilter(operationUser.getUser())).getCards().stream());
    }

}
