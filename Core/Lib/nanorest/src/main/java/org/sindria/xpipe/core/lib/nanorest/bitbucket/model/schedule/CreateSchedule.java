package org.sindria.xpipe.core.lib.nanorest.bitbucket.model.schedule;

import org.json.JSONObject;
import org.sindria.xpipe.core.lib.nanorest.serializer.JsonSerializer;

public class CreateSchedule {


    private final ScheduleTarget target;

    private final boolean enabled;

    private final String cronPattern;


    public CreateSchedule(ScheduleTarget target, boolean enabled, String cronPattern) {

        this.target = target;
        this.enabled = enabled;
        this.cronPattern = cronPattern;
    }


    public ScheduleTarget getTarget() {
        return target;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getCronPattern() {
        return cronPattern;
    }

    public JSONObject serialize() {
        return new JSONObject(JsonSerializer.toJson(this));
    }

}
