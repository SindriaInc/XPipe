package org.cmdbuild.lock;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import java.util.List;
import javax.annotation.Nullable;
import static org.cmdbuild.lock.LockScope.LS_REQUEST;
import static org.cmdbuild.lock.LockScope.LS_SESSION;

/**
 * lock service
 *
 * itemId is an unique key for item on which to aquire lock; it may be used
 * as-is, or hashed/replaced (so the actual lock itemId is the one returned by {@link ItemLock#getItemId()
 * }, which may or may not be equal to the supplied itemId (but is guaranteed to
 * be unique and generated in a repeatable way)). 
 *
 */
public interface LockService {

    LockResponse aquireLock(String itemId, LockScope lockScope);

    LockResponse aquireLockOrWait(String itemId, LockScope lockScope, long waitForMillis);

    @Nullable
    ItemLock getLockOrNull(String itemId);

    void releaseLock(ItemLock itemLock);

    void deleteLock(String lockId);

    void releaseAllLocks();

    List<ItemLock> getAllLocks();

    void requireNotLockedByOthers(String itemId);

    void requireLockedByCurrent(String itemId);

    LockResponse aquireLockOrWait(String itemId, LockScope lockScope);

    default void doWithRequestLock(String itemId, Runnable task) {
        doWithRequestLock(itemId, (Supplier) () -> {
            task.run();
            return null;
        });
    }

    default <T> T doWithRequestLock(String itemId, Supplier<T> task) {
        try (AutoCloseableItemLock lock = aquireLockOrFail(itemId, LS_REQUEST)) {
            return task.get();
        }
    }

    default AutoCloseableItemLock aquireLockOrFail(String itemId) {
        return aquireLockOrFail(itemId, LockScope.LS_SESSION);
    }

    default AutoCloseableItemLock aquireLockOrWaitOrFail(String itemId, LockScope lockScope) {
        return aquireLockOrWait(itemId, lockScope).getLock();
    }

    default LockResponse aquireLockOrWait(String itemId) {
        return aquireLockOrWait(itemId, LS_SESSION);
    }

    default AutoCloseableItemLock aquireLockOrFail(String itemId, LockScope lockScope) {
        return aquireLock(itemId, lockScope).getLock();
    }

    default LockResponse aquireLock(String itemId) {
        return aquireLock(itemId, LS_SESSION);
    }

    default ItemLock getLock(String itemId) {
        return checkNotNull(getLockOrNull(itemId), "lock not found for id = %s", itemId);
    }

    default AutoCloseableItemLock renewLock(String itemId) {
        return aquireLockOrFail(itemId);
    }

    default void releaseLock(String itemId) {
        ItemLock lock = getLockOrNull(itemId);
        if (lock != null) {
            releaseLock(lock);
        }
    }

    static String itemIdFromCardId(Long cardId) {
        return "card_" + cardId.toString();
    }

    static String itemIdFromCardIdAndActivityId(Long cardId, String activityId) {
        return "activity_" + activityId + "_" + cardId.toString();
    }

}
