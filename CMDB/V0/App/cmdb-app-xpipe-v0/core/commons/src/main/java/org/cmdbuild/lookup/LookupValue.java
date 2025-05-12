package org.cmdbuild.lookup;

import javax.annotation.Nullable;

public interface LookupValue extends LookupValueData {

    LookupType getType();

    default LookupSpeciality getSpeciality() {
        return getType().getSpeciality();
    }

    @Override
    default String getLookupType() {
        return getType().getName();
    }

    @Nullable
    default Long getParentTypeOrNull() {
        return getType().getParent();
    }
}
