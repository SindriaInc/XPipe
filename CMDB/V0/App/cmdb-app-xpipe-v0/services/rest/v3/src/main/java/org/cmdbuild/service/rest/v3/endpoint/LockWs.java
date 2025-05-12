/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.lock.ItemLock;
import static org.cmdbuild.lock.LockScopeUtils.serializeLockScope;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import javax.annotation.security.RolesAllowed;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;

@Path("/locks")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ACCESS_AUTHORITY)
public class LockWs {

    private final LockService lockService;

    public LockWs(LockService lockService) {
        this.lockService = checkNotNull(lockService);
    }

    @GET
    @Path("")
    public Object getLocks() {
        return response(lockService.getAllLocks().stream().sorted(Ordering.natural().reverse().onResultOf(ItemLock::getLastActiveDate)).map(LockWs::serializeLockData).collect(toList()));
    }

    @GET
    @Path("/{lockId}")
    public Object getLock(@PathParam("lockId") String lockId) {
        return response(serializeLockData(lockService.getLock(lockId)));
    }

    @DELETE
    @Path("/{lockId}")
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object deleteLock(@PathParam("lockId") String lockId) {
        lockService.deleteLock(lockId);
        return success();
    }

    @DELETE
    @Path("/_ANY")
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public void deleteAllLocks() {
        lockService.releaseAllLocks();
    }

    public static FluentMap<String, Object> serializeLockData(ItemLock lock) {
        return map("_id", lock.getItemId(),
                "sessionId", lock.getSessionId(),
                "requestId", lock.getRequestId(),
                "scope", serializeLockScope(lock.getScope()),
                "beginDate", toIsoDateTime(lock.getBeginDate()),
                "lastActive", toIsoDateTime(lock.getLastActiveDate()));
    }

}
