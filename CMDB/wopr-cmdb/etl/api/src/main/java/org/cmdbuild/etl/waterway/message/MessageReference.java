package org.cmdbuild.etl.waterway.message;

import jakarta.annotation.Nullable;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.buildMessageKey;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public interface MessageReference {

    @Nullable
    String getStorage();

    String getMessageId();

    @Nullable
    Integer getTransactionId();

    default boolean hasTransactionId() {
        return getTransactionId() != null;
    }

    default boolean hasStorage() {
        return isNotBlank(getStorage());
    }

    default String getMessageKey() {
        return buildMessageKey(getMessageId(), getTransactionId());
    }

}
