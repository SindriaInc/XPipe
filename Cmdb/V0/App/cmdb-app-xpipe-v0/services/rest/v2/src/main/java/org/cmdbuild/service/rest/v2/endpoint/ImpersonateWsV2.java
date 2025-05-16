package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.security.RolesAllowed;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.IMPERSONATE_ALL_AUTHORITY;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;

@Path("sessions/{sessionId}/impersonate/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ImpersonateWsV2 {

    private final SessionService sessionService;

    public ImpersonateWsV2(SessionService sessionService) {
        this.sessionService = checkNotNull(sessionService);
    }

    @PUT
    @Path("{username}/")
    @RolesAllowed(IMPERSONATE_ALL_AUTHORITY)
    public Object start(@PathParam("sessionId") String id, @PathParam("username") String username) {
        sessionService.impersonate(username);
        return success();
    }

    @DELETE
    @Path(EMPTY)
    public Object stop(@PathParam("sessionId") String id) {
        sessionService.deimpersonate();
        return success();
    }

}
