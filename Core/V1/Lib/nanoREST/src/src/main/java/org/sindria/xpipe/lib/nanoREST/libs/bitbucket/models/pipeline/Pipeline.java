package org.sindria.xpipe.lib.nanoREST.libs.bitbucket.models.pipeline;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.serializers.JsonSerializer;
import org.sindria.xpipe.lib.nanoREST.libs.bitbucket.models.Variable;

import java.util.ArrayList;

public class Pipeline {

    private final ArrayList<Variable> variables;

    private final PipelineTarget target;

    public Pipeline(PipelineTarget pipelineTarget, ArrayList<Variable> pipelineVariables) {
        this.target = pipelineTarget;
        this.variables = pipelineVariables;
    }

    public PipelineTarget getTarget() {
        return target;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public JSONObject serialize() {
        return new JSONObject(JsonSerializer.toJson(this));
    }
}
