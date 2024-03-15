package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import org.cmdbuild.report.SysReportService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import javax.annotation.security.RolesAllowed;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_VIEW_AUTHORITY;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;

@Path("{a:classes|processes}/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ClassPrintWs {

    private final UserClassService classService;
    private final SysReportService reportService; 

    public ClassPrintWs(UserClassService classService, SysReportService reportService) {
        this.classService = checkNotNull(classService);
        this.reportService = checkNotNull(reportService);
    }
 
    @GET
    @Path("{" + CLASS_ID + "}/print/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printClassReport(@PathParam(CLASS_ID) String classId, WsQueryOptions wsQueryOptions, @QueryParam(EXTENSION) String extension, @QueryParam("attributes") String attributes) {
        Classe classe = classService.getUserClass(classId);
        return reportService.executeUserClassReport(classe, reportExtFromString(extension), buildQueryOptions(classe, wsQueryOptions, attributes));
    } 

    @GET
    @Path("{" + CLASS_ID + "}/print_schema/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printClassSchemaReport(@PathParam(CLASS_ID) String classId, @PathParam("file") String fileName, @QueryParam(EXTENSION) String extension) {
        return reportService.executeClassSchemaReport(classService.getUserClass(classId), reportExtFromString(firstNotBlank(extension, FilenameUtils.getExtension(fileName))));
    }

    @GET
    @Path("print_schema/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_CLASSES_VIEW_AUTHORITY)
    public DataHandler printSchemaReport(@PathParam(CLASS_ID) String classId, @PathParam("file") String fileName, @QueryParam(EXTENSION) String extension) {
        return reportService.executeSchemaReport(reportExtFromString(firstNotBlank(extension, FilenameUtils.getExtension(fileName))));
    }

    public static DaoQueryOptions buildQueryOptions(Classe classe, WsQueryOptions wsQueryOptions, @Nullable String attributes) {
        List<String> attrs = isBlank(attributes) ? null : fromJson(attributes, LIST_OF_STRINGS);
        return DaoQueryOptionsImpl.copyOf(wsQueryOptions.getQuery())
                .withAttrs(attrs)//TODO fix this
                .build().mapAttrNames(classe.getAliasToAttributeMap());
    }

}
