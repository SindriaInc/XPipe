package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;

import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
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
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import static org.cmdbuild.dao.utils.CmSorterUtils.parseSorter;
import org.cmdbuild.report.SysReportService;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import org.cmdbuild.service.rest.v3.model.WsCardData;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF_GOTOPAGE;
import static org.cmdbuild.service.rest.v3.endpoint.ProcessTaskWs.handlePositionOfAndGetMeta;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelperv3;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewService;
import static org.cmdbuild.view.ViewService.JOIN_VIEW_ATTR_JOIN_ID;

@Path("views/{viewId}/cards/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ViewCardWs {

    private final DaoService dao;
    private final CardWsSerializationHelperv3 helper;
    private final ViewService viewService;
    private final CardFilterService filterService;
    private final SysReportService reportService;

    public ViewCardWs(DaoService dao, CardWsSerializationHelperv3 helper, ViewService viewService, CardFilterService filterService, SysReportService reportService) {
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
        this.viewService = checkNotNull(viewService);
        this.filterService = checkNotNull(filterService);
        this.reportService = checkNotNull(reportService);
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("viewId") String viewId, WsCardData data) {
        Card card = viewService.createUserCard(viewId, data.getValues());
        return response(serializeCard(card));
    }

    @GET
    @Path("{" + CARD_ID + "}/")
    public Object readOne(@PathParam("viewId") String viewId, @PathParam(CARD_ID) String cardId) {
        return response(serializeCard(viewService.getCardForCurrentUser(viewId, cardId)));
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("viewId") String viewId, @QueryParam(FILTER) String filterStr, @QueryParam(SORT) String sort, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(POSITION_OF) Long positionOf, @QueryParam(POSITION_OF_GOTOPAGE) @DefaultValue(TRUE) Boolean goToPage) {
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder()
                .withFilter(parseFilter(getFilterOrNull(filterStr)))//TODO map filter attribute names; 
                .withSorter(parseSorter(sort))
                .withPaging(offset, limit)
                .withPositionOf(positionOf, goToPage)
                .build();
        PagedElements<Card> cards = viewService.getCards(viewService.getForCurrentUserByNameOrId(viewId), queryOptions);
        return response(cards.stream().map(this::serializeCard).collect(toList()), cards.totalSize(), handlePositionOfAndGetMeta(queryOptions, cards));
    }

    @PUT
    @Path("{" + CARD_ID + "}/")
    public Object update(@PathParam("viewId") String viewId, @PathParam(CARD_ID) Long cardId, WsCardData data) {
        Card card = viewService.updateUserCard(viewId, cardId, data.getValues());
        return response(serializeCard(card));
    }

    @DELETE
    @Path("{" + CARD_ID + "}/")
    public Object delete(@PathParam("viewId") String viewId, @PathParam(CARD_ID) Long cardId) {
        viewService.deleteUserCard(viewId, cardId);
        return success();
    }

    @GET
    @Path("/cards/{cardId}/print/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler print(@PathParam("viewId") String viewId, @PathParam("cardId") String cardId, @QueryParam(EXTENSION) String extension) {
        View view = viewService.getForCurrentUserByNameOrId(viewId);
        Card card = viewService.getCardById(view, cardId);
        return reportService.executeCardReport(card, reportExtFromString(extension));
    }

    @Nullable//TODO duplicate code
    private String getFilterOrNull(@Nullable String filter) {
        return CardWs.getFilterOrNull(filter, (id) -> filterService.getById(id).getConfiguration());
    }

    private FluentMap<String, Object> serializeCard(Card card) {
        return helper.serializeCard(card).accept(m -> {
            if (card.hasValue(JOIN_VIEW_ATTR_JOIN_ID)) {
                m.put("_id", card.get(JOIN_VIEW_ATTR_JOIN_ID));
            }
        });
    }
}
