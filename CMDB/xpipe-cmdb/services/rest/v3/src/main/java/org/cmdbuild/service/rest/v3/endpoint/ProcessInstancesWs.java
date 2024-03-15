package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.emptyOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FILE;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.email.Email.EMAIL_ATTR_CARD;
import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_INSTANCE_ID;
import static org.cmdbuild.service.rest.v3.endpoint.ProcessTaskWs.handlePositionOfAndGetMeta;
import org.cmdbuild.service.rest.v3.serializationhelpers.ProcessWsSerializationHelper;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.workflow.WorkflowGraphService;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("{a:processes}/{" + PROCESS_ID + "}/{b:instances}/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstancesWs {

    private final WorkflowGraphService graphService;
    private final DaoService dao;
    private final WorkflowService workflowService;
    private final ProcessWsSerializationHelper converterService;
    private final DmsService dmsService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ProcessInstancesWs(
            WorkflowGraphService graphService,
            WorkflowService workflowService,
            ProcessWsSerializationHelper converterService,
            CardFilterService filterService,
            DaoService dao,
            DmsService dmsService) {
        this.graphService = checkNotNull(graphService);
        this.workflowService = checkNotNull(workflowService);
        this.converterService = checkNotNull(converterService);
        this.dao = checkNotNull(dao);
        this.dmsService = checkNotNull(dmsService);
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam(PROCESS_ID) String processId, WsFlowData processInstance) {
        Process processClass = workflowService.getProcess(processId);
        FlowAdvanceResponse response = workflowService.startProcess(
                processId,
                convertInputValuesForFlow(processClass, processInstance),
                //				adaptWidgets(processInstance.getWidgets()),
                processInstance.isAdvance());
        return response(toData(response));
    }

    @PUT
    @Path("{" + PROCESS_INSTANCE_ID + "}")
    public Object update(@PathParam(PROCESS_ID) String planClassId, @PathParam(PROCESS_INSTANCE_ID) Long flowCardId, WsFlowData processInstance) {
        Flow flowCard = workflowService.getFlowCard(planClassId, flowCardId);
        Task task = workflowService.getTask(flowCard, checkNotBlank(processInstance.getActivity(), "must set 'activity' param"));

        Map<String, Object> map = convertInputValuesForFlow(flowCard.getType(), processInstance);
        map = convertTaskValues(task, map);

        FlowAdvanceResponse response = workflowService.updateProcess(planClassId, flowCardId, task.getId(), map, processInstance.isAdvance());

        return response(toData(response));
    }

    @GET
    @Path("{" + PROCESS_INSTANCE_ID + "}")
    public Object read(
            @PathParam(PROCESS_ID) String planClasseId,
            @PathParam(PROCESS_INSTANCE_ID) Long flowCardId,
            @QueryParam("includeModel") @DefaultValue(FALSE) Boolean includeModel,
            @QueryParam("include_tasklist") @DefaultValue(FALSE) Boolean includeTasklist,
            @QueryParam("includeStats") @DefaultValue(FALSE) Boolean includeStats) {
        Flow card = workflowService.getUserFlowCard(planClasseId, flowCardId);
        CmMapUtils.FluentMap<String, Object> map = converterService.serializeFlow(card, includeTasklist, true, includeModel, emptyOptions());
        if (includeStats) {
            Integer attachmentCount = null;
            if (dmsService.isEnabled()) {
                attachmentCount = dmsService.getCardAttachmentCountSafe(card);
            }
            map.put("_attachment_count", attachmentCount,
                    "_email_count", dao.selectCount().from(EMAIL_CLASS_NAME).where(EMAIL_ATTR_CARD, EQ, flowCardId).getCount());
        }
        return response(map);
    }

    @GET
    @Path("{" + PROCESS_INSTANCE_ID + "}/graph/")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler plotGraph(@PathParam(PROCESS_ID) String processId, @PathParam(PROCESS_INSTANCE_ID) Long cardId, @QueryParam("simplified") @DefaultValue(FALSE) Boolean simplified) {
        Flow card = workflowService.getFlowCard(processId, cardId);
        DataSource graph;
        if (simplified) {
            graph = graphService.getSimplifiedGraphImageForFlow(card);
        } else {
            graph = graphService.getGraphImageForFlow(card);
        }
        return new DataHandler(graph);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam(PROCESS_ID) String processId, WsQueryOptions wsQueryOptions, @QueryParam("include_tasklist") @DefaultValue(FALSE) Boolean includeTasklist) {
        Process found = workflowService.getProcess(processId);

//        CmdbFilter filter = CmdbFilterUtils.parseFilter(getFilterOrNull(filterStr));//TODO map filter attribute names
//        CmdbSorter sorter = CmdbSorterUtils.parseSorter(sorterStr);
        // TODO do it better
//		// <<<<<
//		String regex = "\"attribute\"[\\s]*:[\\s]*\"" + UNDERSCORED_STATUS + "\"";
//		String replacement = "\"attribute\":\"" + ATTR_FLOW_STATUS + "\"";
//		String _filter = defaultString(getFilter(filter)).replaceAll(regex, replacement);
//		// <<<<<
//		Iterable<String> attributes = activeAttributes(found);
//		Iterable<String> _attributes = concat(attributes, asList(ATTR_FLOW_STATUS));
//        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder()
//                //				.onlyAttributes(_attributes)
//                .withFilter(filter)
//                .withSorter(sorter)
//                .withPaging(offset, limit)
//                .withPositionOf(positionOfCard, goToPage)
//                .build();
//.filterKeys(k -> !queryOptions.hasAttrs() || queryOptions.getAttrs().contains(card.getType().getAliasToAttributeMap().getOrDefault(k, (String) k)))
        DaoQueryOptions queryOptions = wsQueryOptions.getQuery().mapAttrNames(found.getAliasToAttributeMap());

        DaoQueryOptions daoQueryOptions = queryOptions;
        if (wsQueryOptions.getQuery().getOnlyGridAttrs()) {
            checkArgument(!wsQueryOptions.getQuery().hasAttrs(), "use attrs or onlyGridAttrs, cannot be used at the same time");
            Set<String> setAttributes = found.getCoreAttributes().stream().filter(Attribute::isActive).filter(not(Attribute::isVirtual)).filter(not(Attribute::isHiddenInGrid)).map(Attribute::getName).collect(toSet());
            if (!setAttributes.isEmpty()) {
                daoQueryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).withAttrs(setAttributes).build();
            }
        }

        PagedElements< Flow> elements = workflowService.getUserFlowCardsByClasseIdAndQueryOptions(found.getName(), daoQueryOptions);

        return response(elements.stream().map(f -> converterService.serializeFlow(f, includeTasklist, false, false, queryOptions)).collect(toList()), elements.totalSize(), handlePositionOfAndGetMeta(wsQueryOptions.getQuery(), elements));
    }

    @DELETE
    @Path("{" + PROCESS_INSTANCE_ID + "}") //TODO add permission control; use 'user can stop' wf option'
    public void delete(@PathParam(PROCESS_ID) String processId, @PathParam(PROCESS_INSTANCE_ID) Long instanceId) {
        workflowService.abortProcessFromUser(processId, instanceId);
    }

    @POST
    @Path("{" + PROCESS_INSTANCE_ID + "}/suspend")
    public void suspend(@PathParam(PROCESS_ID) String processId, @PathParam(PROCESS_INSTANCE_ID) Long instanceId) {
        workflowService.suspendProcessFromUser(processId, instanceId);
    }

    @POST
    @Path("{" + PROCESS_INSTANCE_ID + "}/resume")
    public void resume(@PathParam(PROCESS_ID) String processId, @PathParam(PROCESS_INSTANCE_ID) Long instanceId) {
        workflowService.resumeProcessFromUser(processId, instanceId);
    }

    @DELETE
    @Path("")
    public Object deleteMany(@PathParam(PROCESS_ID) String processId, WsQueryOptions wsQueryOptions) {
        // TODO access control (can_bulk), bulk query
        workflowService.getUserFlowCardsByClasseIdAndQueryOptions(processId, wsQueryOptions.getQuery()).forEach(c -> workflowService.abortProcessFromUser(c.getClassName(), c.getId()));
        return success();
    }

    private Object toData(FlowAdvanceResponse response) {
        List tasklist = response.getTasklist().stream().map((task) -> converterService.serializeDetailedTask(task)).collect(toList());
        return converterService.serializeFlow(response.getFlowCard()).with("_flowStatus", response.getAdvancedFlowStatus().name(), "_flowId", response.getFlowId(), "_tasklist", tasklist);
    }

    private Map<String, Object> convertInputValuesForFlow(Process userProcessClass, WsFlowData processInstanceAdvanceable) {
        return convertValues(userProcessClass, firstNonNull(processInstanceAdvanceable.getValues(), emptyMap()));
    }

    private Map<String, Object> convertValues(Process type, Map<String, Object> values) {
        return map(values).mapValues((key, value) -> {
            try {
                if (type.hasAttribute(key) && !type.getAttribute(key).isOfType(FILE)) {//TODO improve this
                    value = rawToSystem(type.getAttribute(key).getType(), value);
                }
                return value;
            } catch (Exception ex) {
                throw runtime(ex, "error converting attr =< {} > type = {} value =< {} >", key, type, abbreviate(value));
            }
        });
    }

    private Map<String, Object> convertTaskValues(Task task, Map<String, Object> values) {
        return map(values).accept(m -> {
            task.getWidgets().forEach((w) -> {
                if (w.hasOutputKey() && w.hasOutputType()) {
                    Object rawValue = values.get(w.getOutputKey());
                    Object value = rawToSystem(w.getOutputType(), rawValue);
                    m.put(w.getOutputKey(), value);
                }
            });
        });
    }

    public static class WsFlowData {

        private final Map<String, Object> values;
        private final boolean advance;
        private final String taskId;

        @JsonCreator
        public WsFlowData(Map<String, Object> values) {
            this.values = map(checkNotNull(values)).immutable();
            advance = toBooleanOrDefault(values.get("_advance"), false);
            taskId = emptyToNull(toStringOrNull(values.get("_activity")));
        }

        public Map<String, Object> getValues() {
            return values;
        }

        public boolean isAdvance() {
            return advance;

        }

        @Nullable
        public String getActivity() {
            return taskId;
        }

    }

}
