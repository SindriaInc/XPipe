package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket.models.pipeline;

public class PipelineVariable {

    private final String key;

    private final String value;

    private final boolean secured;

    public PipelineVariable(String key, String value, boolean secured) {
        this.key = key;
        this.value = value;
        this.secured = secured;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isSecured() {
        return secured;
    }
}
