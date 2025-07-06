package org.sindria.xpipe.core.lib.nanorest.bitbucket.model.pipeline;

import org.json.JSONObject;
import org.sindria.xpipe.core.lib.nanorest.serializer.JsonSerializer;
import org.sindria.xpipe.core.lib.nanorest.bitbucket.model.Variable;

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
