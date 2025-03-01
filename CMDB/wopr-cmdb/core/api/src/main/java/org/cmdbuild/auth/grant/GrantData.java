/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.auth.grant.GrantMode;
import org.cmdbuild.auth.grant.PrivilegedObjectType;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CLONE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CREATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_PRINT;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_SEARCH;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface GrantData {

    static final String GDCP_CREATE = serializeEnum(CP_CREATE),
            GDCP_UPDATE = serializeEnum(CP_UPDATE),
            GDCP_DELETE = serializeEnum(CP_DELETE),
            GDCP_CLONE = serializeEnum(CP_CLONE),
            GDCP_PRINT = serializeEnum(CP_PRINT),
            GDCP_SEARCH = serializeEnum(CP_SEARCH);

    @Nullable
    Long getId();

    PrivilegedObjectType getType();

    GrantMode getMode();

    @Nullable
    String getClassName();

    @Nullable
    Long getObjectId();

    @Nullable
    String getObjectCode();

    long getRoleId();

    @Nullable
    String getPrivilegeFilter();

    @Nullable
    Map<String, String> getAttributePrivileges();

    @Nullable
    Map<String, String> getDmsPrivileges();

    @Nullable
    Map<String, String> getGisPrivileges();

    Map<String, Object> getCustomPrivileges();

    default Object getObjectIdOrClassNameOrCode() {
        return switch (getType()) {
            case POT_CLASS, POT_PROCESS ->
                checkNotBlank(getClassName());
            case POT_VIEW, POT_FILTER, POT_CUSTOMPAGE, POT_REPORT, POT_DASHBOARD ->
                checkNotNull(getObjectId());
            case POT_ETLTEMPLATE, POT_ETLGATE, POT_OFFLINE ->
                checkNotBlank(getObjectCode());
            default ->
                throw unsupported("unsupported grant type = %s", getType());
        };
    }

    default boolean isMode(GrantMode grantMode) {
        return equal(getMode(), grantMode);
    }

}
