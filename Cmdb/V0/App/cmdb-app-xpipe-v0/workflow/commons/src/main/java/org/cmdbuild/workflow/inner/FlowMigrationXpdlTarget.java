/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import com.google.common.base.Objects;
import javax.activation.DataSource;
import javax.annotation.Nullable;

public interface FlowMigrationXpdlTarget {

    FlowMigrationXpdlTargetType getType();

    @Nullable
    DataSource getContent();

    @Nullable
    String getPlanId();

    default boolean isNew() {
        return Objects.equal(getType(), FlowMigrationXpdlTargetType.FMT_NEW);
    }

    default boolean isLegacy() {
        return Objects.equal(getType(), FlowMigrationXpdlTargetType.FMT_LEGACY);
    }

}
