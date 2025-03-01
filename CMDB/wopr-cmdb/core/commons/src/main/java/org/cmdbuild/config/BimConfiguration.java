package org.cmdbuild.config;

import static com.google.common.base.Objects.equal;

public interface BimConfiguration {

    boolean isEnabled();

    BimViewers getViewer();

    Long getConversionTimeout();

    default boolean hasViewer(BimViewers v) {
        return equal(v, getViewer());
    }

}
