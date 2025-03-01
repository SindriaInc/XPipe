package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.WILDCARD;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_VIEW_AUTHORITY;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.queue.EmailQueueService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.service.rest.v3.serializationhelpers.EmailWsHelper;

@Path("email/queue/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
public class EmailQueueWs {

    private final EmailWsHelper helper;
    private final EmailService emailService;
    private final EmailQueueService queueService;

    public EmailQueueWs(EmailWsHelper helper, EmailService emailService, EmailQueueService queueService) {
        this.helper = checkNotNull(helper);
        this.emailService = checkNotNull(emailService);
        this.queueService = checkNotNull(queueService);
    }

    @POST
    @Path("trigger")
    @Consumes(WILDCARD)
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object triggerQueue() {
        queueService.triggerEmailQueue();
        return success();
    }

    @GET
    @Path("outgoing/")
    public Object getOutgoingEmails() {
        return response(emailService.getAllForOutgoingProcessing().stream().sorted(Ordering.natural().onResultOf(Email::getDate).reversed()).map(helper::serializeBasicEmail));
    }

    @POST
    @Path("outgoing/{emailId}/trigger")
    @Consumes(WILDCARD)
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object sendSingleEmail(@PathParam("emailId") Long emailId) {
        queueService.sendSingleEmail(emailId);
        return success();
    }

}
