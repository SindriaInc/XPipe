package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import static java.util.stream.Collectors.toList;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.cmdbuild.service.rest.v3.endpoint.ProcessInstancesWs.WsFlowData;
import org.cmdbuild.service.rest.v3.serializationhelpers.EmailWsHelper;

@Path("processes/{processId}/instances/{instanceId}/activities/{activityId}/emails")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstanceActivityEmailWs {

    private final EmailWsHelper helper;
    private final EmailService emailService;

    public ProcessInstanceActivityEmailWs(EmailWsHelper helper, EmailService emailService) {
        this.helper = checkNotNull(helper);
        this.emailService = checkNotNull(emailService);
    }

    @POST
    @Path("sync")
    public Object updateEmailWithCardData(@PathParam("processId") String processId, @PathParam("instanceId") Long flowId, WsFlowData flowData, WsQueryOptions wsQueryOptions) {
        //TODO check user permissions
        //TODO auto update email data from template, with current flow data (and trigger email widget hooks)
        DaoQueryOptions queryOptions = wsQueryOptions.getQuery();
        Collection<Email> emails = emailService.getAllForCard(flowId, queryOptions);
        return response(emails.stream().map(helper::serializeBasicEmail).collect(toList()));
    }

}
