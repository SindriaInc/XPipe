/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.lock.LockService.itemIdFromCardId;
import static org.cmdbuild.service.rest.v3.endpoint.LockWs.serializeLockData;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.failure;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

@Path("{a:processes|classes}/{classId}/{b:cards|instances}/{cardId}/lock")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardLockWs {

    private final LockService lockService;
    private final SessionService sessionService;
    private final CoreConfiguration coreConfig;

    public CardLockWs(LockService lockService, SessionService sessionService, CoreConfiguration coreConfig) {
        this.lockService = checkNotNull(lockService);
        this.sessionService = checkNotNull(sessionService);
        this.coreConfig = checkNotNull(coreConfig);
    }

    @GET
    @Path("")
    public Object getLock(@PathParam("classId") String classId, @PathParam("cardId") Long cardId) {
        //TODO authorize card lock access
        ItemLock lock = lockService.getLockOrNull(itemIdFromCardId(cardId));
        if (lock == null) {
            return success().with("found", false);
        } else {
            return response(serializeLock(lock)).with("found", true);
        }
    }

    @POST
    @Path("")
    public Object createLock(@PathParam("classId") String classId, @PathParam("cardId") Long cardId) {
        //TODO authorize card lock create
        LockResponse lockResponse = lockService.aquireLock(itemIdFromCardId(cardId));
        if (lockResponse.isAquired()) {
            return response(serializeLock(lockResponse.getLock()));
        } else {
            if (coreConfig.getCardlockShowUser()) {
                String username = sessionService.getSessionById(lockService.getLock(itemIdFromCardId(cardId)).getSessionId()).getOperationUser().getUsername();
                return failure().with("user", username);
            } else {
                return failure();
            }
        }
    }

    @DELETE
    @Path("")
    public Object releaseLock(@PathParam("classId") String classId, @PathParam("cardId") Long cardId) {
        //TODO authorize card lock delete
        lockService.releaseLock(itemIdFromCardId(cardId));
        return success();
    }

    private FluentMap<String, Object> serializeLock(ItemLock lock) {
        return serializeLockData(lock).with("_owned_by_current_session", equal(sessionService.getCurrentSessionIdOrNull(), lock.getSessionId()));
    }

}
