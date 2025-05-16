package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.formstructure.FormStructureImpl;
import org.cmdbuild.formstructure.FormStructureService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

import org.cmdbuild.service.rest.v3.serializationhelpers.ProcessWsSerializationHelper;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;

import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskDefinition;
import javax.annotation.security.RolesAllowed;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_PROCESSES_MODIFY_AUTHORITY;
import static org.cmdbuild.workflow.utils.ClosedFlowUtils.DUMMY_TASK_FOR_CLOSED_PROCESS;
import static org.cmdbuild.workflow.utils.ClosedFlowUtils.buildTaskDefinitionForClosedTask;

@Path("processes/{processId}/activities/")
@Produces(APPLICATION_JSON)
public class ProcessTaskDefinitionWs {

    private final WorkflowService workflowService;
    private final ProcessWsSerializationHelper converterService;
    private final FormStructureService formStructureService;

    public ProcessTaskDefinitionWs(WorkflowService workflowService, ProcessWsSerializationHelper converterService, FormStructureService formStructureService) {
        this.workflowService = checkNotNull(workflowService);
        this.converterService = checkNotNull(converterService);
        this.formStructureService = checkNotNull(formStructureService);
    }

    @GET
    @Path("")
    public Object getAllActivities(@PathParam("processId") String processId, @QueryParam(START) Long offset, @QueryParam(LIMIT) Long limit) {
        Process process = workflowService.getProcess(processId);
        List<TaskDefinition> tasks = workflowService.getTaskDefinitions(processId);
        return response(paged(tasks, offset, limit).map(t -> converterService.serializeDetailedTaskDefinition(process, t)));
    }

    @GET
    @Path("{taskId}")
    public Object getOne(@PathParam("processId") String processId, @PathParam("taskId") String taskId) {
        Process process = workflowService.getProcess(processId);
        TaskDefinition task = workflowService.getTaskDefinition(processId, taskId);
        return response(converterService.serializeDetailedTaskDefinition(process, task));
    }

    @PUT
    @Path("{taskId}")
    @RolesAllowed(ADMIN_PROCESSES_MODIFY_AUTHORITY)
    public Object update(@PathParam("processId") String processId, @PathParam("taskId") String taskId, WsTaskDefinitionData data) {
        Process process = workflowService.getProcess(processId);
        formStructureService.setFormForTask(process, taskId, data.formStructure.isNull() ? null : new FormStructureImpl(toJson(data.formStructure)));
        TaskDefinition task;
        if (taskId.equals(DUMMY_TASK_FOR_CLOSED_PROCESS)) {
            task = buildTaskDefinitionForClosedTask();
        } else {
            task = workflowService.getTaskDefinition(processId, taskId);
        }
        return response(converterService.serializeDetailedTaskDefinition(process, task));
    }

    public static class WsTaskDefinitionData {

        public final JsonNode formStructure;

        public WsTaskDefinitionData(@JsonProperty("formStructure") JsonNode formStructure) {
            this.formStructure = formStructure;
        }
    }
}
