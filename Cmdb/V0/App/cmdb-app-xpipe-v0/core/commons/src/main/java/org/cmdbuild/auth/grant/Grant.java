package org.cmdbuild.auth.grant;

import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface Grant {

    PrivilegedObjectType getObjectType();

    Set<GrantPrivilege> getPrivileges();

    PrivilegeSubjectWithInfo getSubject();

    @Nullable
    String getFilterOrNull();

    Map<String, GrantAttributePrivilege> getAttributePrivileges();
    
    Map<String, GrantAttributePrivilege> getDmsPrivileges();
    
    Map<String, GrantAttributePrivilege> getGisPrivileges();

    Map<String, Object> getCustomPrivileges();

    default String getName() {
        return getSubject().getPrivilegeId();
    }

    default boolean hasFilter() {
        return isNotBlank(getFilterOrNull());
    }
}
