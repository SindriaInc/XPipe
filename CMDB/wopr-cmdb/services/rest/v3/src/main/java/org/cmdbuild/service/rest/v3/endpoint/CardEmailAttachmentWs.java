package org.cmdbuild.service.rest.v3.endpoint;

import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper;
import jakarta.activation.DataHandler;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.util.List;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper.WsAttachmentData;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;

@Path("{a:processes|classes}/{classId}/{b:instances|cards}/{cardId}/emails/{emailId}/attachments|calendar/events/{cardId}/emails/{emailId}/attachments/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardEmailAttachmentWs {//note: duplicate code from AttachmentWs; would be great if it could be possible to merge CardEmailAttachmentWs and AttachmentWs

    private final AttachmentWsHelper service;

    public CardEmailAttachmentWs(AttachmentWsHelper attachmentWs) {
        this.service = checkNotNull(attachmentWs);
    }

    @POST
    @Path(EMPTY)
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object create(@PathParam("emailId") Long emailId,
            @Multipart(value = ATTACHMENT, required = false) @Nullable WsAttachmentData attachment,
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            @QueryParam("copyFrom_class") String sourceClassId,
            @QueryParam("copyFrom_card") Long sourceCardId,
            @QueryParam("copyFrom_id") String sourceAttachmentId,
            @QueryParam("tempId") List<String> tempId) throws IOException {
        if (sourceClassId == null) {
            if (dataHandler != null) {
                return service.create(EMAIL_CLASS_NAME, emailId, attachment, dataHandler);
            } else {
                return service.create(EMAIL_CLASS_NAME, emailId, attachment, tempId);
            }
        } else {
            return service.copyFrom(EMAIL_CLASS_NAME, emailId, attachment, sourceClassId, sourceCardId, sourceAttachmentId);
        }
    }

    @GET
    @Path(EMPTY)
    public Object readMany(WsQueryOptions wsQueryOptions, @PathParam("emailId") Long emailId) {
        return service.readMany(wsQueryOptions, EMAIL_CLASS_NAME, emailId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/")
    public Object readOne(@PathParam("emailId") Long emailId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.readOne(EMAIL_CLASS_NAME, emailId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(@PathParam("emailId") Long emailId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.download(EMAIL_CLASS_NAME, emailId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/preview")
    public Object preview(@PathParam("emailId") Long emailId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.preview(EMAIL_CLASS_NAME, emailId, attachmentId);
    }

    @PUT
    @Path("{" + ATTACHMENT_ID + "}/")
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object update(@PathParam("emailId") Long emailId, @PathParam(ATTACHMENT_ID) String attachmentId, @Nullable @Multipart(value = ATTACHMENT, required = false) WsAttachmentData attachment, @Nullable @Multipart(value = FILE, required = false) DataHandler dataHandler) {
        return service.update(EMAIL_CLASS_NAME, emailId, attachmentId, attachment, dataHandler);
    }

    @DELETE
    @Path("{" + ATTACHMENT_ID + "}/")
    public Object delete(@PathParam("emailId") Long emailId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.delete(EMAIL_CLASS_NAME, emailId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history")
    public Object getAttachmentHistory(@PathParam("emailId") Long emailId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.getAttachmentHistory(EMAIL_CLASS_NAME, emailId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history/{version}/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadPreviousVersion(@PathParam("emailId") Long emailId, @PathParam(ATTACHMENT_ID) String attachmentId, @PathParam("version") String versionId) {
        return service.downloadPreviousVersion(EMAIL_CLASS_NAME, emailId, attachmentId, versionId);
    }

}
