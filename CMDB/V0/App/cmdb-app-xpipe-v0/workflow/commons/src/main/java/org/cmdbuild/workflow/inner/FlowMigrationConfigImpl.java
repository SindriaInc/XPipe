/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import com.google.common.collect.ImmutableMap;
import static java.util.Collections.emptyMap;
import java.util.Map;
import javax.annotation.Nullable;

public class FlowMigrationConfigImpl implements FlowMigrationConfig {

    private final FlowMigrationXpdlTarget defaultXpdl;
    private final Map<String, FlowMigrationXpdlTarget> flowMigrationMapping;

    public FlowMigrationConfigImpl() {
        this(null);
    }

    public FlowMigrationConfigImpl(@Nullable FlowMigrationXpdlTarget defaultXpdl) {
        this(defaultXpdl, emptyMap());
    }

    public FlowMigrationConfigImpl(@Nullable FlowMigrationXpdlTarget defaultXpdl, Map<String, FlowMigrationXpdlTarget> flowMigrationMapping) {
        this.defaultXpdl = defaultXpdl;
        this.flowMigrationMapping = ImmutableMap.copyOf(flowMigrationMapping);
    }

    @Override
    public FlowMigrationXpdlTarget getDefaultXpdl() {
        return defaultXpdl;
    }

    @Override
    public Map<String, FlowMigrationXpdlTarget> getFlowMigrationMapping() {
        return flowMigrationMapping;
    }

    @Override
    public String toString() {
        return "FlowMigrationConfig{" + "defaultXpdl=" + defaultXpdl + ", flowMigrationMapping=" + flowMigrationMapping + '}';
    }

}
