package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.IMPERSONATE_ALL_AUTHORITY;

import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.USERNAME;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;

@Path("sessions/current/impersonate/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ImpersonationWs {

    private final SessionService sessionService;

    public ImpersonationWs(SessionService sessionLogic) {
        this.sessionService = checkNotNull(sessionLogic);
    }

    @POST
    @Path("{" + USERNAME + "}/")
    @RolesAllowed(IMPERSONATE_ALL_AUTHORITY)
    public Object impersonate(@PathParam(USERNAME) String username) {
        sessionService.impersonate(username);
        return success();
    }

    @DELETE
    @Path(EMPTY)
    public Object deimpersonare() {
        sessionService.deimpersonate();
        return success();
    }

}
