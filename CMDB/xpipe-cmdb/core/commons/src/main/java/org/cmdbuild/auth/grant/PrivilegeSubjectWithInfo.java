package org.cmdbuild.auth.grant;

import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

public interface PrivilegeSubjectWithInfo extends PrivilegeSubject {

    @Nullable
    Long getId();

    String getName();

    String getDescription();

    @Override
    default boolean hasInfo() {
        return true;
    }

    default boolean hasId() {
        return isNotNullAndGtZero(getId());
    }
}
