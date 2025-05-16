package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import org.cmdbuild.report.SysReportService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;

@Path("{a:classes}/{" + CLASS_ID + "}/{b:cards}/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardPrintWs {

    private final UserCardService cardService;
    private final SysReportService reportService;

    public CardPrintWs(UserCardService cardService, SysReportService reportService) {
        this.cardService = checkNotNull(cardService);
        this.reportService = checkNotNull(reportService);
    }

    @GET
    @Path("{" + CARD_ID + "}/print/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler readOne(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @QueryParam(EXTENSION) String extension) {
        Card card = cardService.getUserCard(classId, cardId);
        checkArgument(card.getType().hasServiceReadPermission(), "user not authorized to access card %s.%s", classId, cardId);
        return reportService.executeCardReport(card, reportExtFromString(extension));
    }
}
