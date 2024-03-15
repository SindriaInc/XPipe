package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupValue;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.FLOW_STATUS_LOOKUP;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.STATE_CLOSED_ABORTED;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.STATE_CLOSED_COMPLETED;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.STATE_OPEN_NOT_RUNNING_SUSPENDED;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.STATE_OPEN_RUNNING;

@Path("configuration/processes/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessConfigurationWs {

    private final LookupService service;

    public ProcessConfigurationWs(LookupService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path("statuses/")
    public Object readStatuses() {
        return response(list(service.getAllLookup(FLOW_STATUS_LOOKUP)).filter(LookupValue::isActive).map(l -> map("id", l.getId(), "value", PROCESS_STATUS_CODE_MAP.get(l.getCode()), "description", l.getDescription())));
    }

    public static final String OPEN = "open";
    public static final String SUSPENDED = "suspended";
    public static final String COMPLETED = "completed";
    public static final String ABORTED = "closed";

    private static final Map<String, String> PROCESS_STATUS_CODE_MAP = ImmutableMap.of(
            STATE_OPEN_RUNNING, OPEN,
            STATE_OPEN_NOT_RUNNING_SUSPENDED, SUSPENDED,
            STATE_CLOSED_COMPLETED, COMPLETED,
            STATE_CLOSED_ABORTED, ABORTED
    );
}
