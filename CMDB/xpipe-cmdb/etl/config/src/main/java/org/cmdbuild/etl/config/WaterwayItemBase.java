package org.cmdbuild.etl.config;

import javax.annotation.Nullable;

public interface WaterwayItemBase {

    String getCode();

    String getDescription();

    String getNotes();

    WaterwayItemType getType();

    @Nullable
    String getSubtype();

    boolean isEnabled();
}
