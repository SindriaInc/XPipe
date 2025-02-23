package org.sindria.xpipe.lib.nanoREST.libs.bitbucket.models.schedule;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.serializers.JsonSerializer;

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
