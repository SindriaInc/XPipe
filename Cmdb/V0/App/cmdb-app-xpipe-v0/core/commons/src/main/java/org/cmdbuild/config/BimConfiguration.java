package org.cmdbuild.config;

import static com.google.common.base.Objects.equal;

public interface BimConfiguration {

    String getUrl();

    BimViewers getViewer();

    String getUsername();

    String getPassword();

    boolean isEnabled();

    boolean isBimserverEnabled();

    boolean deleteProjectBeforeUpload();

    Long getConversionTimeout();

    default boolean hasViewer(BimViewers v) {
        return equal(v, getViewer());
    }

}
