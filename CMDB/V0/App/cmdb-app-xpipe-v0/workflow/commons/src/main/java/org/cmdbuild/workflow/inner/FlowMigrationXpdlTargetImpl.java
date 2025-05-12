/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.workflow.inner.FlowMigrationXpdlTargetType.FMT_LEGACY;
import static org.cmdbuild.workflow.inner.FlowMigrationXpdlTargetType.FMT_NEW;

public class FlowMigrationXpdlTargetImpl implements FlowMigrationXpdlTarget {

    private final DataSource content;
    private final String planId;
    private final FlowMigrationXpdlTargetType type;

    public FlowMigrationXpdlTargetImpl(FlowMigrationXpdlTargetType type, @Nullable DataSource content, @Nullable String planId) {
        this.type = checkNotNull(type);
        switch (type) {
            case FMT_LEGACY:
                this.content = null;
                this.planId = planId;
                break;
            case FMT_NEW:
                this.content = checkNotNull(content);
                this.planId = null;
                break;
            default:
                throw new IllegalArgumentException("unsupported type = " + type);
        }
    }

    public static FlowMigrationXpdlTarget fromNewXpdl(DataSource content) {
        return new FlowMigrationXpdlTargetImpl(FMT_NEW, content, null);
    }

    public static FlowMigrationXpdlTarget fromLegacyXpdl() {
        return new FlowMigrationXpdlTargetImpl(FMT_LEGACY, null, null);
    }

    public static FlowMigrationXpdlTarget fromLegacyXpdl(String planId) {
        return new FlowMigrationXpdlTargetImpl(FMT_LEGACY, null, checkNotBlank(planId));
    }

    @Nullable
    @Override
    public DataSource getContent() {
        return content;
    }

    @Nullable
    @Override
    public String getPlanId() {
        return planId;
    }

    @Override
    public FlowMigrationXpdlTargetType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "FlowMigrationXpdlTarget{" + "planId=" + planId + ", type=" + type + '}';
    }

}
