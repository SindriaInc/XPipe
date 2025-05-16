package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Ordering;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_PROCESSES_MODIFY_AUTHORITY;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FILTER_DEVICE;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FOR_USER;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_INCLUDE_INACTIVE_ELEMENTS;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_ADMIN;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.service.rest.v3.serializationhelpers.ProcessWsSerializationHelper;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.workflow.WorkflowCommonConst.RIVER;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.XpdlInfo;

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
        return helper.buildFullDetailExtendedResponse(isAdminViewMode ? classService.getExtendedClass(process.getName(), CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER) : classService.getExtendedClass(process.getName(), CQ_FILTER_DEVICE, CQ_FOR_USER)).accept(processSpecificDataMapConsumer(process, true));//TODO avoid new user service query

    }
}
