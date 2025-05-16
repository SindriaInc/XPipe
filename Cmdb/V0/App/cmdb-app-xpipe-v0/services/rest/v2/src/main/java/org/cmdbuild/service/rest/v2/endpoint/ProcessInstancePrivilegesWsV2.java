package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.WorkflowService;

@Path("processes/{processId}/instances/{processInstanceId}/privileges/")
@Produces(APPLICATION_JSON)
public class ProcessInstancePrivilegesWsV2 {

    private final WorkflowService workflowService;

    public ProcessInstancePrivilegesWsV2(WorkflowService workflowService) {
        this.workflowService = checkNotNull(workflowService);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("processId") String processId, @PathParam("processInstanceId") Long processInstanceId) {
        return map("data", map("stoppable", workflowService.getProcess(processId).getMetadata().isWfUserStoppable()));
    }

}
