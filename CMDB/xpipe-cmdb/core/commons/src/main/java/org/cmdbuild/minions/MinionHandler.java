package org.cmdbuild.minions;

import static com.google.common.base.Objects.equal;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import java.util.List;
import java.util.Set;

public interface MinionHandler extends MinionComponentCommons {

    default MinionRuntimeStatus checkRuntimeStatus() {
        return getRuntimeStatus();
    }

    default MinionStatus checkStatus() {
        checkRuntimeStatus();
        return getStatus();
    }

    default boolean isHidden() {
        return false;
    }

    default boolean isAutostart() {
        return true;
    }

    default Set<String> getRequires() {
        return emptySet();
    }

    default boolean hasStatus(MinionStatus status) {
        return equal(getStatus(), status);
    }

    default int getOrder() {
        return 0;
    }

    default List<Object> getReloadOnConfigs() {
        return emptyList();
    }

    default boolean checkRuntimeStatus(MinionRuntimeStatus minionRuntimeStatus) {
        return equal(checkRuntimeStatus(), minionRuntimeStatus);
    }
}
