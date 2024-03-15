package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.Ordering;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.utils.date.CmDateUtils;
import org.cmdbuild.workflow.WorkflowService;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.workflow.model.XpdlInfo;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.workflow.model.Process;
import javax.annotation.security.RolesAllowed;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_PROCESSES_MODIFY_AUTHORITY;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FILTER_CEVICE;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FOR_USER;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_INCLUDE_INACTIVE_ELEMENTS;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_ADMIN;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.service.rest.v3.serializationhelpers.ProcessWsSerializationHelper;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.workflow.WorkflowCommonConst.RIVER;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.inner.FlowMigrationConfigImpl;
import org.cmdbuild.workflow.inner.FlowMigrationConfig;
import org.cmdbuild.workflow.inner.FlowMigrationXpdlTarget;
import org.cmdbuild.workflow.inner.FlowMigrationXpdlTargetType;
import org.cmdbuild.workflow.inner.FlowMigrationXpdlTargetImpl;

@Path("{a:processes}/")
@Produces(APPLICATION_JSON)
public class ProcessWs {

    private final WorkflowConfiguration workflowConfiguration;
    private final WorkflowService workflowService;//TODO replace with user wf service
    private final UserClassService classService;
    private final ClassSerializationHelper helper;
    private final ProcessWsSerializationHelper converterService;

    public ProcessWs(WorkflowConfiguration workflowConfiguration, WorkflowService workflowService, UserClassService classService, ClassSerializationHelper helper, ProcessWsSerializationHelper converterService) {
        this.workflowConfiguration = checkNotNull(workflowConfiguration);
        this.workflowService = checkNotNull(workflowService);
        this.classService = checkNotNull(classService);
        this.helper = checkNotNull(helper);
        this.converterService = checkNotNull(converterService);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed) {
        Collection<Process> all = isAdminViewMode(viewMode) ? workflowService.getAllProcessClasses() : workflowService.getActiveProcessClasses();
        List<Process> ordered = Ordering.natural().onResultOf(Process::getName).sortedCopy(all);
        PagedElements<Process> paged = paged(ordered, offset, limit);
        return response(paged.map(detailed ? p -> detailedResponse(p, isAdminViewMode(viewMode)) : this::minimalResponse));
    }

    @GET
    @Path("{processId}/")
    public Object read(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("processId") String processId) {
        Process classe = workflowService.getProcess(processId);
        return response(detailedResponse(classe, isAdminViewMode(viewMode)));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_PROCESSES_MODIFY_AUTHORITY)
    public Object create(WsClassData data) {
        ExtendedClass classe = classService.createClass(helper.extendedClassDefinitionForNewClass(data));
        return read(VIEW_MODE_ADMIN, classe.getClasse().getName());
    }

    @PUT
    @Path("{processId}/")
    @RolesAllowed(ADMIN_PROCESSES_MODIFY_AUTHORITY)
    public Object update(@PathParam("processId") String classId, WsClassData data) {
        ExtendedClass classe = classService.updateClass(helper.extendedClassDefinitionForExistingClass(classId, data));
        return read(VIEW_MODE_ADMIN, classe.getClasse().getName());
    }

    @DELETE
    @Path("{processId}/")
    @RolesAllowed(ADMIN_PROCESSES_MODIFY_AUTHORITY)
    public Object delete(@PathParam("processId") String classId) {
        classService.deleteClass(classId);
        return success();
    }

    @POST
    @Path("{processId}/versions")
    @Consumes(MULTIPART_FORM_DATA)
    public Object uploadNewXpdlVersion(@PathParam("processId") String processId, @Multipart(FILE) DataHandler dataHandler, @QueryParam("replace") @DefaultValue(FALSE) Boolean replace) {
        XpdlInfo xpdlInfo;
        if (replace) {
            xpdlInfo = workflowService.addXpdlReplaceCurrent(processId, toDataSource(dataHandler));
        } else {
            xpdlInfo = workflowService.addXpdl(processId, toDataSource(dataHandler));
        }
        return response(xpdlInfoToResponse(xpdlInfo));
    }

    @POST
    @Path("{processId}/migration")
    public Object uploadXpdlVersionAndMigrateProcessToNewProvider(@PathParam("processId") String processId, @Nullable FlowMigrationConfigWfData data, @Multipart(value = FILE, required = false) DataHandler dataHandler) {
        if (dataHandler != null) {
            workflowService.migrateFlowInstancesToNewProvider(processId, new FlowMigrationConfigImpl(FlowMigrationXpdlTargetImpl.fromNewXpdl(toDataSource(dataHandler))));
        } else {
            workflowService.migrateFlowInstancesToNewProvider(processId, checkNotNull(data, "missing flow migration config").toConfig());
        }
        return success();
    }

    @GET
    @Path("{processId}/versions")
    public Object getAllXpdlVersions(@PathParam("processId") String processId) {
        List<XpdlInfo> versions;
        if (workflowService.isWorkflowEnabled()) {
            versions = workflowService.getXpdlInfosOrderByVersionDesc(processId);
        } else {
            versions = emptyList();
        }
        return response(versions.stream().map(ProcessWs::xpdlInfoToResponse).collect(toList()));
    }

    @GET
    @Path("{processId}/versions/{planId}/file")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler getXpdlVersionFile(@PathParam("processId") String processId, @PathParam("planId") String planId) {
        DataSource dataSource = workflowService.getXpdlByClasseIdAndPlanId(processId, planId);
        return new DataHandler(dataSource);
    }

    @GET
    @Path("{processId}/template")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler getXpdlTemplateFile(@PathParam("processId") String processId) {
        DataSource dataSource = workflowService.getXpdlTemplate(processId);
        return new DataHandler(dataSource);
    }

    private static Object xpdlInfoToResponse(XpdlInfo version) {
        return map("_id", version.getPlanId(),
                "provider", version.getProvider(),
                "version", version.getVersion(),
                "planId", version.getPlanId(),
                "default", version.isDefault(),
                "lastUpdate", CmDateUtils.toIsoDateTime(version.getLastUpdate()));
    }

    private FluentMap<String, Object> minimalResponse(Process p) {
        return helper.buildBasicResponse(classService.getUserClass(p.getName())).accept(processSpecificDataMapConsumer(p, false)); //TODO avoid new user service query
    }

    private Consumer<FluentMap<String, Object>> processSpecificDataMapConsumer(Process p, boolean detailed) {
        return (m) -> {
            m.put(
                    "flowStatusAttr", p.getFlowStatusLookup(),
                    "messageAttr", p.getMetadata().getMessageAttr(),
                    "enableSaveButton", firstNotNull(p.isFlowSaveButtonEnabled(), workflowConfiguration.enableSaveButton()),
                    "stoppableByUser", p.getMetadata().isWfUserStoppable(),//TODO add user permissions here ??
                    "engine", p.getProviderOrDefault(RIVER),//ider(),//) firstNotBlank(p.getProviderOrNull(), workflowService.getDefaultProvider()),
                    "planId", p.getPlanIdOrNull()
            );
            if (detailed) {
                m.put("activities", (p.isSuperclass() || !p.isActive()) ? emptyList() : workflowService.getTaskDefinitions(p.getName()).stream().map(t -> converterService.serializeEssentialTaskDefinition(p, t)).collect(toImmutableList()));
            }
        };
    }

    private FluentMap<String, Object> detailedResponse(Process process, boolean isAdminViewMode) {
        return helper.buildFullDetailExtendedResponse(isAdminViewMode ? classService.getExtendedClass(process.getName(), CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER) : classService.getExtendedClass(process.getName(), CQ_FILTER_CEVICE, CQ_FOR_USER)).accept(processSpecificDataMapConsumer(process, true));//TODO avoid new user service query

    }

    public static class FlowMigrationConfigWfData {

        private final FlowMigrationConfigEntryWfData defaultValue;
        private final List<FlowMigrationConfigEntryWfData> mapping;

        public FlowMigrationConfigWfData(@JsonProperty("default") FlowMigrationConfigEntryWfData defaultValue, @JsonProperty("mapping") List<FlowMigrationConfigEntryWfData> mapping) {
            this.defaultValue = defaultValue;
            this.mapping = ImmutableList.copyOf(mapping);
        }

        public FlowMigrationConfig toConfig() {
            return new FlowMigrationConfigImpl(defaultValue == null ? null : defaultValue.toXpdlTarget(), mapping.stream().collect(toMap(FlowMigrationConfigEntryWfData::getSource, FlowMigrationConfigEntryWfData::toXpdlTarget)));
        }

    }

    public static class FlowMigrationConfigEntryWfData {

        private final FlowMigrationXpdlTargetType type;
        private final String content, target, source;

        public FlowMigrationConfigEntryWfData(@JsonProperty("type") String type, @JsonProperty("content") String content, @JsonProperty("source") String source, @JsonProperty("target") String target) {
            this.type = parseEnum(type, FlowMigrationXpdlTargetType.class);
            this.content = content;
            this.source = source;
            this.target = target;
        }

        public String getSource() {
            return source;
        }

        public FlowMigrationXpdlTarget toXpdlTarget() {
            return new FlowMigrationXpdlTargetImpl(type, content == null ? null : newDataSource(content), target);
        }

    }

}
