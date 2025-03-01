package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import jakarta.annotation.security.RolesAllowed;
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
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.beans.EmailImpl;
import static org.cmdbuild.email.utils.EmailMtaUtils.acquireEmail;
import static org.cmdbuild.email.utils.EmailMtaUtils.parseEmail;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EMAIL_ID;
import org.cmdbuild.service.rest.v3.model.WsEmailData;
import org.cmdbuild.service.rest.v3.serializationhelpers.EmailWsHelper;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytes;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("{a:classes|processes}/{" + CLASS_ID + "}/{b:cards|instances}/{" + CARD_ID + "}/emails")
@Produces(APPLICATION_JSON)
public class CardEmailWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final EmailService emailService;
    private final EmailWsHelper helper;
    private final AttachmentWsHelper attachmentHelper;
    private final OperationUserSupplier operationUser;

    public CardEmailWs(DaoService dao, EmailService emailService, EmailWsHelper helper, AttachmentWsHelper attachmentHelper, OperationUserSupplier operationUser) {
        this.dao = checkNotNull(dao);
        this.emailService = checkNotNull(emailService);
        this.helper = checkNotNull(helper);
        this.attachmentHelper = checkNotNull(attachmentHelper);
        this.operationUser = checkNotNull(operationUser);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, WsQueryOptions wsQueryOptions) {
        DaoQueryOptions queryOptions = wsQueryOptions.getQuery();
        Card card = dao.getCard(classId, cardId);
        Collection<Email> list = emailService.getAllForCard(card.getId(), queryOptions);
        return response(paged(list, wsQueryOptions.isDetailed() ? helper::serializeDetailedEmail : helper::serializeBasicEmail, toInt(wsQueryOptions.getOffset()), toInt(wsQueryOptions.getLimit())));
    }

    @GET
    @Path("{" + EMAIL_ID + "}/")
    public Object read(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(EMAIL_ID) Long emailId) {
        dao.getCard(classId, cardId); //TODO check user permissions
        Email email = emailService.getOne(emailId);
        return response(helper.serializeDetailedEmail(email));//TODO check email id
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) String cardId, @QueryParam("apply_template") @DefaultValue(FALSE) Boolean applyTemplate, @QueryParam("upload_template_attachments") @DefaultValue(FALSE) Boolean uploadTemplateAttachments, @QueryParam("template_only") @DefaultValue(FALSE) Boolean templateOnly, @QueryParam("attachments") List<String> tempId, List<Attachment> parts) {
        List<Attachment> attachments = parts.size() == 1 ? emptyList() : parts.stream().filter(a -> !equal(a.getDataHandler().getName(), "email")).collect(toImmutableList());
        Attachment body = parts.size() == 1 ? getOnlyElement(parts) : parts.stream().filter(a -> equal(a.getDataHandler().getName(), "email")).collect(onlyElement("missing 'email' multipart json payload"));
        List<EmailAttachment> emailAttachments = attachmentHelper.convertAttachmentsToEmailAttachments(attachments, tempId);

        logger.debug("attachments in input [{}], email attachments created [{}]", attachments.size(), emailAttachments.size());

        WsEmailData emailData = fromJson(readToString(body.getDataHandler()), WsEmailData.class);

        return response(helper.createEmail(classId, cardId, emailData, helper.templateSerializationType(applyTemplate, templateOnly, uploadTemplateAttachments), emailAttachments));
    }

    @POST
    @Path("load")
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object load(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, WsEmailLoadData data) {
        Email email = emailService.create(EmailImpl.copyOf(parseEmail(data.data)).withReference(cardId).build());
        logger.info("loaded email = {}", email);
        return response(helper.serializeDetailedEmail(email));
    }

    @POST
    @Path("acquire")
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object acquire(WsEmailLoadData data) {
        Email email = emailService.create(acquireEmail(unpackBytes(data.data)));
        logger.info("acquired email = {}", email);
        return response(helper.serializeDetailedEmail(email));
    }

    @POST
    @Path("{" + EMAIL_ID + "}/send")
    public Object send(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(EMAIL_ID) Long emailId, @QueryParam("upload_template_attachments") @DefaultValue(FALSE) Boolean uploadTemplateAttachments, @QueryParam("template_only") @DefaultValue(FALSE) Boolean templateOnly, @QueryParam("delay") @DefaultValue("skipDelay") String customDelay) {
        Card card = dao.getCard(classId, cardId); //TODO check user permissions
        Email current = emailService.getOne(emailId);
        checkArgument(equal(ES_DRAFT, current.getStatus()), "cannot send email with status = %s", current.getStatus());
        Email email = current;
        if (!templateOnly) {
            checkArgument(customDelay.equals("skipDelay") || isCreatable(customDelay), "cannot send email with delay = %s", customDelay);
            if (customDelay.equals("skipDelay")) {
                email = EmailImpl.copyOf(current).withStatus(ES_OUTGOING).withReference(card.getId()).withId(emailId).withAbortableByUser(operationUser.getUsername()).build();
            } else {
                email = EmailImpl.copyOf(current).withStatus(ES_OUTGOING).withReference(card.getId()).withId(emailId).withAbortableByUser(operationUser.getUsername()).withDelay(toLong(customDelay)).build();
            }
            email = emailService.update(email);
        }

        if (uploadTemplateAttachments) {
            helper.handleTemplateAttachments(email, null, card);
        }

        return response(helper.serializeDetailedEmail(email));
    }

    @POST
    @Path("{" + EMAIL_ID + "}/abort")
    public Object abort(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(EMAIL_ID) Long emailId, WsEmailData emailData) {
        Email current = emailService.getOne(emailId);
        checkArgument(current.isOutgoing() && current.isAbortableByUser(operationUser.getUsername()) && emailData.hasStatus(ES_DRAFT), "cannot abort email with status = %s from user = %s", current.getStatus(), operationUser.getUsername());
        Email email = emailService.update(EmailImpl.copyOf(current).withDelay(emailData.toEmail().build().getDelay()).withStatus(ES_DRAFT).build());
        return response(helper.serializeDetailedEmail(email));
    }

    @PUT
    @Path("{" + EMAIL_ID + "}/")
    public Object update(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(EMAIL_ID) Long emailId, WsEmailData emailData, @QueryParam("apply_template") @DefaultValue(FALSE) Boolean applyTemplate, @QueryParam("upload_template_attachments") @DefaultValue(FALSE) Boolean uploadTemplateAttachments, @QueryParam("template_only") @DefaultValue(FALSE) Boolean templateOnly) {
        Card card = dao.getCard(classId, cardId); //TODO check user permissions
        Email current = emailService.getOne(emailId);
        if (current.isReceived()) {
            Email email = emailService.update(EmailImpl.copyOf(current).withIsReadByUser(emailData.isReadByUser()).build());
            return response(helper.serializeDetailedEmail(email));
        } else if (current.isOutgoing() && current.isAbortableByUser(operationUser.getUsername()) && emailData.hasStatus(ES_DRAFT)) {
            Email email = emailService.update(EmailImpl.copyOf(current).withStatus(ES_DRAFT).build());
            return response(helper.serializeDetailedEmail(email));
        } else {
            checkArgument(equal(ES_DRAFT, current.getStatus()), "cannot update email with status = %s", current.getStatus());
            Email email = emailData.toEmail().withReference(card.getId()).withId(emailId).withAbortableByUser(operationUser.getUsername()).build();

            return response(helper.updateEmail(email, card, emailData, helper.templateSerializationType(applyTemplate, templateOnly, uploadTemplateAttachments)));
        }
    }

    @DELETE
    @Path("{" + EMAIL_ID + "}/")
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam(EMAIL_ID) Long emailId) {
        Card card = dao.getCard(classId, cardId); //TODO check user permissions
        emailService.delete(emailService.getOne(emailId));//TODO check email id
        return success();
    }

    public static class WsEmailLoadData {

        private final String data;

        public WsEmailLoadData(@JsonProperty("data") String data) {
            this.data = checkNotBlank(data);
        }

    }

}
