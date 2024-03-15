package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_VIEW_AUTHORITY;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.EmailStatus;
import static org.cmdbuild.email.EmailStatus.ES_ERROR;
import org.cmdbuild.email.queue.EmailQueueService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EMAIL_ID;
import org.cmdbuild.service.rest.v3.model.WsEmailData;
import org.cmdbuild.service.rest.v3.serializationhelpers.EmailWsHelper;

@Path("email/error/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
public class EmailErrorWs {

    private final EmailWsHelper helper;
    private final EmailService emailService;

    public EmailErrorWs(EmailWsHelper helper, EmailService emailService, EmailQueueService queueService) {
        this.helper = checkNotNull(helper);
        this.emailService = checkNotNull(emailService);
    }

    @GET
    @Path(EMPTY)
    public Object getErrorEmails() {
        return response(emailService.getAllForErrorProcessing().stream().sorted(Ordering.natural().onResultOf(Email::getDate).reversed()).map(helper::serializeBasicEmail));
    }

    @GET
    @Path("{" + EMAIL_ID + "}/")
    public Object read(@PathParam(EMAIL_ID) Long emailId) {
        Email email = emailService.getOne(emailId);
        return response(helper.serializeDetailedEmail(email));
    }

    @PUT
    @Path("{" + EMAIL_ID + "}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object update(@PathParam(EMAIL_ID) Long emailId, WsEmailData emailData) {
        Email current = emailService.getOne(emailId);
        checkArgument(equal(ES_ERROR, current.getStatus()), "cannot update email with status = %s", current.getStatus());
        Email email = emailData.toEmail().withReference(current.getReference()).withId(emailId).withStatus(EmailStatus.ES_OUTGOING).withErrorCount(0).withDelay(0L).build();
        email = emailService.update(email);
        return response(helper.serializeDetailedEmail(email));
    }

    @DELETE
    @Path("{" + EMAIL_ID + "}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object delete(@PathParam(EMAIL_ID) Long emailId) {
        emailService.delete(emailService.getOne(emailId));
        return success();
    }

}
