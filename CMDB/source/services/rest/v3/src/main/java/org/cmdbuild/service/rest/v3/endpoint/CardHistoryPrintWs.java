package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WF_BASIC;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.SysReportService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.service.rest.v3.endpoint.ClassPrintWs.buildQueryOptions;

@Path("{a:processes|classes}/{" + CLASS_ID + "}/{b:cards|instances}/{" + CARD_ID + "}/history")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardHistoryPrintWs {

    private final UserCardService cardService;
    private final CardHistoryService historyService;
    private final SysReportService reportService;

    public CardHistoryPrintWs(UserCardService cardService, CardHistoryService historyService, SysReportService reportService) {
        this.cardService = checkNotNull(cardService);
        this.historyService = checkNotNull(historyService);
        this.reportService = checkNotNull(reportService);
    }

    @GET
    @Path("print")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printHistoryReport(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @QueryParam(EXTENSION) String extension, WsQueryOptions wsQueryOptions, @QueryParam("attributes") String attributes, @QueryParam("types") @DefaultValue("cards") String types) {
        Card card = cardService.getUserCard(classId, cardId);
        DaoQueryOptions queryOptions = buildQueryOptions(card.getType(), wsQueryOptions, attributes);
        List<CardHistoryService.HistoryElement> historyTypes = historyService.fetchHistoryTypes(types);
        checkArgument(card.getType().hasServicePermission(CP_READ) || (card.getType().isProcess() && card.getType().hasServicePermission(CP_WF_BASIC)), "user not authorized to access card %s.%s", classId, cardId);
        return reportService.executeUserClassHistoryReport(card.getType(), card.getId(), ReportFormat.CSV, queryOptions, historyTypes);
    }
}
