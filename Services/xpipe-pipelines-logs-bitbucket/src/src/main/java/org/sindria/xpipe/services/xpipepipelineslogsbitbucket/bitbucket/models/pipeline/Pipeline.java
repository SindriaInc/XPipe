package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket.models.pipeline;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.serializers.JsonSerializer;

import java.util.ArrayList;

public class Pipeline {

    private final ArrayList<PipelineVariable> variables;

    private final PipelineTarget target;

    public Pipeline(PipelineTarget pipelineTarget, ArrayList<PipelineVariable> pipelineVariables) {
        this.target = pipelineTarget;
        this.variables = pipelineVariables;
    }

    public PipelineTarget getTarget() {
        return target;
    }

    public ArrayList<PipelineVariable> getVariables() {
        return variables;
    }

    public JSONObject serialize() {
        return new JSONObject(JsonSerializer.toJson(this));
    }
}
