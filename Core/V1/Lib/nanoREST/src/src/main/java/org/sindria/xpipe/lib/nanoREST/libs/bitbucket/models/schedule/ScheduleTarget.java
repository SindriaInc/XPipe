package org.sindria.xpipe.lib.nanoREST.libs.bitbucket.models.schedule;

import java.util.HashMap;

public class ScheduleTarget {

    private final HashMap<String, String> selector;

    private final String refName;

    private final String refType;

    private final String type;

    public ScheduleTarget(HashMap<String, String> selector, String refName, String refType) {
        this.selector = selector;
        this.refName = refName;
        this.refType = refType;
        this.type = "pipeline_ref_target";
    }

    public HashMap<String, String> getSelector() {
        return selector;
    }

    public String getRefName() {
        return refName;
    }

    public String getRefType() {
        return refType;
    }

    public String getType() {
        return type;
    }
}
