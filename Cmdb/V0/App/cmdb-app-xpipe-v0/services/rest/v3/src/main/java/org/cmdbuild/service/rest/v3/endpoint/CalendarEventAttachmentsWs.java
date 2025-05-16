package org.cmdbuild.service.rest.v3.endpoint;

import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper;
import javax.activation.DataHandler;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;

import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper.WsAttachmentData;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;

@Path("calendar/events/{eventId}/attachments")
public class CalendarEventAttachmentsWs {

    private final AttachmentWsHelper service;
    private final CalendarService calendarService;

    private final String EVENT_CLASS_NAME = "_CalendarEvent";

    public CalendarEventAttachmentsWs(AttachmentWsHelper attachmentWs, CalendarService calendarService) {
        this.service = checkNotNull(attachmentWs);
        this.calendarService = checkNotNull(calendarService);
    }

    @POST
    @Path(EMPTY)
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object create(@PathParam("eventId") Long eventId,
            @Multipart(value = ATTACHMENT, required = false) @Nullable WsAttachmentData attachment,
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            @QueryParam("copyFrom_class") String sourceClassId,
            @QueryParam("copyFrom_card") Long sourceCardId,
            @QueryParam("copyFrom_id") String sourceAttachmentId,
            @QueryParam("tempId") List<String> tempId) throws IOException {
        calendarService.getUserEvent(eventId);
        if (sourceClassId == null) {
            if (dataHandler != null) {
                return service.create(EVENT_CLASS_NAME, eventId, attachment, dataHandler);
            } else {
                return service.create(EVENT_CLASS_NAME, eventId, attachment, tempId);
            }
        } else {
            return service.copyFrom(EVENT_CLASS_NAME, eventId, attachment, sourceClassId, sourceCardId, sourceAttachmentId);
        }
    }

    @GET
    @Path(EMPTY)
    public Object readmany(WsQueryOptions wsQueryOptions, @PathParam("eventId") Long eventId) {
        calendarService.getUserEvent(eventId);
        return service.readMany(wsQueryOptions, EVENT_CLASS_NAME, eventId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/")
    public Object readOne(@PathParam("eventId") Long eventId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        calendarService.getUserEvent(eventId);
        return service.readOne(EVENT_CLASS_NAME, eventId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(@PathParam("eventId") Long eventId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        calendarService.getUserEvent(eventId);
        return service.download(EVENT_CLASS_NAME, eventId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/preview")
    public Object preview(@PathParam("eventId") Long eventId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        calendarService.getUserEvent(eventId);
        return service.preview(EVENT_CLASS_NAME, eventId, attachmentId);
    }

    @PUT
    @Path("{" + ATTACHMENT_ID + "}/")
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object update(@PathParam("eventId") Long eventId, @PathParam(ATTACHMENT_ID) String attachmentId, @Nullable @Multipart(value = ATTACHMENT, required = false) WsAttachmentData attachment, @Nullable @Multipart(value = FILE, required = false) DataHandler dataHandler) {
        calendarService.getUserEvent(eventId);
        return service.update(EVENT_CLASS_NAME, eventId, attachmentId, attachment, dataHandler);
    }

    @DELETE
    @Path("{" + ATTACHMENT_ID + "}/")
    public Object delete(@PathParam("eventId") Long eventId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        calendarService.getUserEvent(eventId);
        return service.delete(EVENT_CLASS_NAME, eventId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history")
    public Object getAttachmentHistory(@PathParam("eventId") Long eventId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        calendarService.getUserEvent(eventId);
        return service.getAttachmentHistory(EVENT_CLASS_NAME, eventId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history/{version}/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadPreviousVersion(@PathParam("eventId") Long eventId, @PathParam(ATTACHMENT_ID) String attachmentId, @PathParam("version") String versionId) {
        calendarService.getUserEvent(eventId);
        return service.downloadPreviousVersion(EVENT_CLASS_NAME, eventId, attachmentId, versionId);
    }
}
