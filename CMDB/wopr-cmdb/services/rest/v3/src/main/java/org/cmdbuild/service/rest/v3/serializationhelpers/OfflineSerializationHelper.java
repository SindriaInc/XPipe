/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.serializationhelpers;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.function.Function;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.lock.LockType.ILT_OFFLINE;
import static org.cmdbuild.lock.LockTypeUtils.itemIdWithLockType;
import org.cmdbuild.offline.Offline;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.failure;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.v3.endpoint.LockWs.serializeLockData;
import org.cmdbuild.translation.TranslationService;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OfflineSerializationHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TranslationService translationService;
    private final LockService lockService;
    private final SessionService sessionService;

    public OfflineSerializationHelper(TranslationService translationService, LockService lockService, SessionService sessionService) {
        this.translationService = checkNotNull(translationService);
        this.lockService = checkNotNull(lockService);
        this.sessionService = checkNotNull(sessionService);
    }

    public Function<Offline, FluentMap> serializeOffline(boolean detailed) {
        return detailed ? this::serializeDetailedOffline : this::serializeBasicOffline;
    }

    public FluentMap serializeBasicOffline(Offline offline) {
        return map(
                "_id", offline.getCode(),
                "name", offline.getCode(),
                "description", offline.getDescription(),
                "_description_translation", translationService.translateOfflineDescription(offline.getCode(), offline.getDescription()),
                "_description_plural_translation", translationService.translateOfflineDescription(offline.getCode(), offline.getDescription()),
                "active", offline.isActive()
        );
    }

    public FluentMap serializeDetailedOffline(Offline offline) {
        return serializeBasicOffline(offline).with(
                "classes", fromJson(offline.getMetadata(), MAP_OF_OBJECTS).getOrDefault("classes", list()),
                "processes", fromJson(offline.getMetadata(), MAP_OF_OBJECTS).getOrDefault("processes", list()),
                "views", fromJson(offline.getMetadata(), MAP_OF_OBJECTS).getOrDefault("views", list()),
                "masterClass", fromJson(offline.getMetadata(), MAP_OF_OBJECTS).get("masterClass"),
                "_can_modify", lockService.getLockOrNull(itemIdWithLockType(ILT_OFFLINE, offline.getCode())) == null
        );
    }

    public Map<String, Object> aquireLockOffline(String offlineCode) {
        LockResponse lockResponse = lockService.aquireLockTimeToLiveSeconds(itemIdWithLockType(ILT_OFFLINE, offlineCode), 2592000);
        if (lockResponse.isAquired()) {
            return response(serializeLock(lockResponse.getLock()));
        } else {
            String username = sessionService.getSessionById(lockService.getLock(itemIdWithLockType(ILT_OFFLINE, offlineCode)).getSessionId()).getOperationUser().getUsername();
            return failure().with("user", username);
        }
    }

    public Map<String, Object> releaseLockOffline(String offlineCode) {
        lockService.releaseLock(itemIdWithLockType(ILT_OFFLINE, offlineCode));
        return success();
    }

    private FluentMap<String, Object> serializeLock(ItemLock lock) {
        return serializeLockData(lock).with("_owned_by_current_session", equal(sessionService.getCurrentSessionIdOrNull(), lock.getSessionId()));
    }
}
