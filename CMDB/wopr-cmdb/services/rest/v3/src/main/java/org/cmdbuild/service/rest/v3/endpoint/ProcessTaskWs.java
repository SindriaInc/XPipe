package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;

import java.util.Map;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF_GOTOPAGE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.service.rest.v3.serializationhelpers.ProcessWsSerializationHelper;
import static org.cmdbuild.service.rest.v3.utils.PositionOfUtils.handlePositionOfAndGetMeta;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Task;

@Path("processes/{processId}/instance_activities/")
@Produces(APPLICATION_JSON)
public class ProcessTaskWs {

    private final WorkflowService workflowService;
    private final ProcessWsSerializationHelper converterService;

    public ProcessTaskWs(WorkflowService workflowService, ProcessWsSerializationHelper converterService) {
        this.workflowService = checkNotNull(workflowService);
        this.converterService = checkNotNull(converterService);
    }

    @GET
    @Path("")
    public Object getAllActivities(@PathParam("processId") String processId,
            @QueryParam(POSITION_OF) Long positionOfCard,
            @QueryParam(POSITION_OF_GOTOPAGE) @DefaultValue(TRUE) Boolean goToPage,
            @QueryParam(FILTER) String filter,
            @QueryParam(START) Long offset,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(SORT) String sort) {

        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder()
                .withOffset(offset)
                .withLimit(limit)
                .withSorter(CmSorterUtils.parseSorter(sort))
                .withFilter(CmFilterUtils.parseFilter(filter))
                .withPositionOf(positionOfCard, goToPage)
                .build();

        PagedElements<Task> tasklist = workflowService.getTaskListForCurrentUserByClassIdSkipFlowData(processId, queryOptions);

        return response(tasklist.map((task) -> converterService.serializeFlow(task.getProcessInstance()).with(
                "_activity_id", task.getId(),
                "_activity_writable", task.isWritable(),
                "_activity_performer", task.getPerformerName(),
                "_activity_description", task.getDefinition().getDescription(),
                "_activity_description_addition", converterService.serializeTaskDescriptionValue(task.getDescriptionValue()),
                "_activity_subset_id", task.getActivitySubsetId())
        ).elements(), tasklist.totalSize(), handlePositionOfAndGetMeta(queryOptions, tasklist));
    }
}
