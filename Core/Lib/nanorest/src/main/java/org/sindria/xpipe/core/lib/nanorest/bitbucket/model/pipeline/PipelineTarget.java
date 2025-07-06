package org.sindria.xpipe.core.lib.nanorest.bitbucket.model.pipeline;

public class PipelineTarget {

    private final String refType;

    private final String type;

    private final String refName;

    public PipelineTarget(String refType, String refName) {
        this.refType = refType;
        this.type = "pipeline_ref_target";
        this.refName = refName;
    }

    public String getRefType() {
        return refType;
    }

    public String getType() {
        return type;
    }

    public String getRefName() {
        return refName;
    }
}
