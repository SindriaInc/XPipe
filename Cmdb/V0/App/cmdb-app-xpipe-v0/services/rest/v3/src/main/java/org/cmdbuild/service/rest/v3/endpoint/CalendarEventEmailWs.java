package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.calendar.CalendarService;
import static org.cmdbuild.calendar.beans.CalendarEvent.EVENT_TABLE;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.service.rest.v3.model.WsEmailData;
import org.cmdbuild.service.rest.v3.serializationhelpers.EmailWsHelper;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;

@Path("calendar/events/{eventId}/emails")
public class CalendarEventEmailWs {

    private final EmailService emailService;
    private final CalendarService calendarService;
    private final EmailWsHelper helper;
    private final AttachmentWsHelper attachmentHelper;

    public CalendarEventEmailWs(EmailService emailService,
            CalendarService calendarService,
            EmailWsHelper emailSerialization,
            AttachmentWsHelper attachmentHelper) {
        this.emailService = checkNotNull(emailService);
        this.calendarService = checkNotNull(calendarService);
        this.helper = checkNotNull(emailSerialization);
        this.attachmentHelper = checkNotNull(attachmentHelper);
    }

    @POST
    @Path(EMPTY)
    public Object create(
            @PathParam("eventId") Long eventId, 
            @QueryParam("apply_template") @DefaultValue(FALSE) Boolean applyTemplate, 
            @QueryParam("template_only") @DefaultValue(FALSE) Boolean templateOnly, 
            @QueryParam("attachments") List<String> tempId, List<Attachment> parts) {
        calendarService.getUserEvent(eventId);
        List<Attachment> attachments = parts.size() == 1 ? emptyList() : parts.stream().filter(a -> !equal(a.getDataHandler().getName(), "email")).collect(toImmutableList());
        Attachment body = parts.size() == 1 ? getOnlyElement(parts) : parts.stream().filter(a -> equal(a.getDataHandler().getName(), "email")).collect(onlyElement("missing 'email' multipart json payload"));
        List<EmailAttachment> emailAttachments = attachmentHelper.convertAttachmentsToEmailAttachments(attachments, tempId);
        WsEmailData emailData = fromJson(readToString(body.getDataHandler()), WsEmailData.class);
        return response(helper.createEmail(EVENT_TABLE, eventId.toString(), emailData, applyTemplate, templateOnly, emailAttachments));
    }

    @PUT
    @Path("{emailId}/")
    public Object update(@PathParam("eventId") Long eventId, @PathParam("emailId") Long emailId, WsEmailData emailData) {
        calendarService.getUserEvent(eventId);
        Email current = emailService.getOne(emailId);
        checkArgument(equal(ES_DRAFT, current.getStatus()), "cannot update email with status = %s", current.getStatus());
        Email email = emailData.toEmail().withReference(eventId).withId(emailId).build();
        email = emailService.update(email);
        return response(helper.serializeDetailedEmail(email, null));
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@PathParam("eventId") Long eventId, WsQueryOptions wsQueryOptions) {
        DaoQueryOptions queryOptions = wsQueryOptions.getQuery();
        calendarService.getUserEvent(eventId);
        Collection<Email> list = emailService.getAllForCard(eventId, queryOptions);
        return response(paged(list, wsQueryOptions.isDetailed() ? helper::serializeDetailedEmail : helper::serializeBasicEmail, toInt(wsQueryOptions.getOffset()), toInt(wsQueryOptions.getLimit())));
    }

    @GET
    @Path("{emailId}/")
    public Object read(@PathParam("eventId") Long eventId, @PathParam("emailId") Long emailId) {
        calendarService.getUserEvent(eventId);
        Email email = emailService.getOne(emailId);
        return response(helper.serializeDetailedEmail(email));//TODO check email id
    }

    @DELETE
    @Path("{emailId}/")
    public Object delete(@PathParam("eventId") Long eventId, @PathParam("emailId") Long emailId) {
        calendarService.getUserEvent(eventId);
        emailService.delete(emailService.getOne(emailId));
        return success();
    }

    private Object createEmail(Long eventId, WsEmailData emailData) {
        Email email = emailData.toEmail().withReference(eventId).build();
        email = emailService.create(email);
        return response(helper.serializeDetailedEmail(email, null));
    }
}
