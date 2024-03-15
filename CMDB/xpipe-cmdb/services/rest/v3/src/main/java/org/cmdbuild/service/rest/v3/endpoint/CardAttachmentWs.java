package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper;
import javax.activation.DataHandler;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper.WsAttachmentData;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmZipUtils.buildZipFile;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;

@Path("{a:processes|classes}/{" + CLASS_ID + "}/{b:instances|cards}/{" + CARD_ID + "}/attachments/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardAttachmentWs {

    private final AttachmentWsHelper service;

    public CardAttachmentWs(AttachmentWsHelper service) {
        this.service = checkNotNull(service);
    }

    @POST
    @Path(EMPTY)
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object create(@PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @Multipart(value = ATTACHMENT, required = false) @Nullable WsAttachmentData attachment,
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            @QueryParam("copyFrom_class") String sourceClassId,
            @QueryParam("copyFrom_card") Long sourceCardId,
            @QueryParam("copyFrom_id") String sourceAttachmentId,
            @QueryParam("tempId") List<String> tempId) throws IOException {
        if (sourceClassId == null) {
            if (dataHandler != null) {
                return service.create(classId, cardId, attachment, dataHandler);
            } else {
                return service.create(classId, cardId, attachment, tempId);
            }
        } else {
            return service.copyFrom(classId, cardId, attachment, sourceClassId, sourceCardId, sourceAttachmentId);
        }
    }

    @GET
    @Path(EMPTY)
    public Object readMany(WsQueryOptions wsQueryOptions, @PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId) {
        return service.readMany(wsQueryOptions, classId, cardId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/")
    public Object readOne(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId, @QueryParam("includeWidgets") @DefaultValue(FALSE) Boolean includeWidgets) {
        return service.readOne(classId, cardId, attachmentId, includeWidgets);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.download(classId, cardId, attachmentId);
    }

    @GET
    @Path("_MANY/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadMany(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @QueryParam(ATTACHMENT_ID) List<String> attachmentId) {
        checkArgument(!isNullOrEmpty(attachmentId), "empty attachmentId param");
        Map<String, BigByteArray> map = attachmentId.stream().flatMap(a -> Splitter.on(",").splitToList(a).stream()).map(a -> service.download(classId, cardId, a)).collect(toMap(DataHandler::getName, CmIoUtils::toBigByteArray));//TODO improve split
        return newDataHandler(buildZipFile(map), "application/zip", "attachments.zip");
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/preview")
    public Object preview(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.preview(classId, cardId, attachmentId);
    }

    @PUT
    @Path("{" + ATTACHMENT_ID + "}/")
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object update(@PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam(ATTACHMENT_ID) String attachmentId,
            @Nullable @Multipart(value = ATTACHMENT, required = false) WsAttachmentData attachment,
            @Nullable @Multipart(value = FILE, required = false) DataHandler dataHandler,
            @QueryParam("tempId") String tempId) {
        if (isNotBlank(tempId)) {
            return service.update(classId, cardId, attachmentId, attachment, tempId);
        } else {
            return service.update(classId, cardId, attachmentId, attachment, dataHandler);
        }
    }

    @DELETE
    @Path("{" + ATTACHMENT_ID + "}/")
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.delete(classId, cardId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history")
    public Object getAttachmentHistory(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId) {
        return service.getAttachmentHistory(classId, cardId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history/{version}/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadPreviousVersion(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(ATTACHMENT_ID) String attachmentId, @PathParam("version") String versionId) {
        return service.downloadPreviousVersion(classId, cardId, attachmentId, versionId);
    }

    @DELETE
    @Path("")
    public Object deleteMany(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, WsQueryOptions wsQueryOptions) {
        service.deleteAttachments(classId, cardId, wsQueryOptions.getQuery().getFilter());
        return success();
    }

}
