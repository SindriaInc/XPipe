package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;
import org.cmdbuild.service.rest.v3.serializationhelpers.ProcessWsSerializationHelper;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskDefinition;

@Path("processes/{" + PROCESS_ID + "}/start_activities/")
@Produces(APPLICATION_JSON)
public class ProcessStartActivityWs {

    private final WorkflowService workflowService;
    private final ProcessWsSerializationHelper converterService;

    public ProcessStartActivityWs(WorkflowService workflowService, ProcessWsSerializationHelper converterService) {
        this.workflowService = checkNotNull(workflowService);
        this.converterService = checkNotNull(converterService);
    }

    @GET
    @Path(EMPTY)
    public Object read(@PathParam(PROCESS_ID) String processId) {
        Process planClasse = workflowService.getProcess(processId);
        TaskDefinition task = workflowService.getEntryTaskForCurrentUser(processId);
        return response(converterService.serializeDetailedTaskDefinition(planClasse, task));
    }

}
