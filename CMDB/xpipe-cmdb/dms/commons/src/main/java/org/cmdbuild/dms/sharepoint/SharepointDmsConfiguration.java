package org.cmdbuild.dms.sharepoint;

import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dms.DmsConfiguration;

public interface SharepointDmsConfiguration extends SharepointConfiguration, DmsConfiguration {

    String getSharepointPath();

    @Nullable
    String getSharepointCustomAuthorColumn();

    @Nullable
    String getSharepointCustomDescriptionColumn();

    @Nullable
    String getSharepointCustomCategoryColumn();

    default boolean hasSharepointCustomAuthorColumn() {
        return isNotBlank(getSharepointCustomAuthorColumn());
    }

    default boolean hasSharepointCustomDescriptionColumn() {
        return isNotBlank(getSharepointCustomDescriptionColumn());
    }

    default boolean hasSharepointCustomCategoryColumn() {
        return isNotBlank(getSharepointCustomCategoryColumn());
    }

    @Override
    default boolean autodeleteEmptyDirectories() {
        return true;
    }
}
