package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import static java.util.stream.Collectors.toList;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ACTIVITY_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_INSTANCE_ID;
import org.cmdbuild.service.rest.v3.serializationhelpers.ProcessWsSerializationHelper;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;

import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.Flow;

@Path("processes/{" + PROCESS_ID + "}/instances/{" + PROCESS_INSTANCE_ID + "}/activities/")
@Produces(APPLICATION_JSON)
public class ProcessInstanceActivityWs {

    private final WorkflowService workflowService;
    private final ProcessWsSerializationHelper converterService;

    public ProcessInstanceActivityWs(WorkflowService workflowService, ProcessWsSerializationHelper converterService) {
        this.workflowService = checkNotNull(workflowService);
        this.converterService = checkNotNull(converterService);
    }

    @GET
    @Path("")
    public Object readMany(@PathParam(PROCESS_ID) String classId, @PathParam(PROCESS_INSTANCE_ID) Long cardId, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed) {//TODO pagination
        List<Task> tasks = workflowService.getTaskListForCurrentUserByClassIdAndCardId(classId, cardId);
        return response(tasks.stream().map(detailed ? t -> converterService.serializeDetailedTask(t) : t -> converterService.serializeBasicTask(t)).collect(toList()));
    }

    @GET
    @Path("{" + PROCESS_ACTIVITY_ID + "}/")
    public Object readOne(@PathParam(PROCESS_ID) String classId, @PathParam(PROCESS_INSTANCE_ID) Long cardId, @PathParam(PROCESS_ACTIVITY_ID) String taskId) {
        Flow card = workflowService.getFlowCard(classId, cardId);
        Task task = workflowService.getUserTask(card, taskId);
        return response(converterService.serializeDetailedTask(task));
    }

}
