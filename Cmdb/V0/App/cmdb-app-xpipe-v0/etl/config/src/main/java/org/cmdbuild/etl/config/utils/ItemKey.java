package org.cmdbuild.etl.config.utils;

import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.getDescriptorCodeFromKey;

public interface ItemKey {

    String getCode();

    String getDescriptorKey();

    default String getDescriptorCode() {
        return getDescriptorCodeFromKey(getDescriptorKey());
    }

}
