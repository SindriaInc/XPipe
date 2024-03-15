package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.classe.access.CardHistoryService.HistoryElement;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.beans.DatabaseRecord;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import org.cmdbuild.service.rest.common.serializationhelpers.HistorySerializationHelper;

import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_INSTANCE_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import org.cmdbuild.service.rest.v3.serializationhelpers.ProcessWsSerializationHelper;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import org.cmdbuild.workflow.inner.FlowHistoryService;
import org.cmdbuild.workflow.model.Flow;

@Path("processes/{" + PROCESS_ID + "}/instances/{" + PROCESS_INSTANCE_ID + "}/history")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstancesHistoryWs {

    private final FlowHistoryService service;
    private final ProcessWsSerializationHelper helper;
    private final HistorySerializationHelper historyHelper;

    public ProcessInstancesHistoryWs(FlowHistoryService service, ProcessWsSerializationHelper helper, HistorySerializationHelper historyHelper) {
        this.service = checkNotNull(service);
        this.helper = checkNotNull(helper);
        this.historyHelper = checkNotNull(historyHelper);
    }

    @GET
    @Path("")
    public Object getHistory(
            @PathParam(PROCESS_ID) String classId,
            @PathParam(PROCESS_INSTANCE_ID) Long cardId,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed,
            @QueryParam(FILTER) String filterStr,
            @QueryParam("types") @DefaultValue("cards") String types) {
        DaoQueryOptionsImpl query = DaoQueryOptionsImpl.builder()
                .withPaging(offset, limit)
                .withFilter(filterStr)
                .orderBy(ATTR_BEGINDATE, DESC)
                .build();
        List<HistoryElement> historyTypes = Splitter.on(",").splitToList(types).stream().map(e -> parseEnumOrNull(e, CardHistoryService.HistoryElement.class)).collect(toList());
        PagedElements<DatabaseRecord> history = service.getHistory(classId, cardId, query, historyTypes);
        return response(history.stream().map(p -> {
            if (p instanceof Flow) {
                return detailed ? helper.serializeDetailedHistory((Flow) p) : helper.serializeBasicHistory((Flow) p);
            } else {
                return historyHelper.serializeBasicHistory(p);
            }
        }), history.totalSize());
    }

    @GET
    @Path("{recordId}/")
    public Object getHistoryRecord(@PathParam(PROCESS_ID) String classId, @PathParam(PROCESS_INSTANCE_ID) Long id, @PathParam("recordId") Long recordId) {
        Flow record = service.getHistoryRecord(classId, recordId);
        checkArgument(equal(record.getCurrentId(), id));
        return response(helper.serializeDetailedHistory(record));
    }

}
