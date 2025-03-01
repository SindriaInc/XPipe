/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.security.RolesAllowed;
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
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_OFFLINE_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.modeldiff.data.ModelConfiguration;
import org.cmdbuild.modeldiff.dataset.data.DataDataset;
import org.cmdbuild.offline.Offline;
import org.cmdbuild.offline.OfflineDataImpl;
import org.cmdbuild.offline.OfflineDataImpl.OfflineDataImplBuilder;
import org.cmdbuild.offline.OfflineService;
import org.cmdbuild.offline.loader.OfflineLoaderService;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.service.rest.v3.serializationhelpers.OfflineSerializationHelper;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

/**
 *
 * @author ataboga
 */
@Path("offline/")
@Produces(APPLICATION_JSON)
public class OfflineWs {

    private final OfflineService offlineService;
    private final OfflineSerializationHelper helper;
    private final OfflineLoaderService offlineLoaderService;

    public OfflineWs(OfflineService offlineService, OfflineSerializationHelper helper, OfflineLoaderService offlineLoaderService) {
        this.offlineService = checkNotNull(offlineService);
        this.helper = checkNotNull(helper);
        this.offlineLoaderService = checkNotNull(offlineLoaderService);
    }

    @GET
    @Path("")
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed) {
        List<Offline> allOffline = isAdminViewMode(viewMode) ? offlineService.getAll() : offlineService.getActiveForCurrentUser();
        return response(allOffline.stream().map(helper.serializeOffline(detailed)).collect(toList()));
    }

    @GET
    @Path("/{offlineCode}")
    public Object read(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("offlineCode") String offlineCode) {
        Offline offline = offlineService.getByCode(offlineCode);
        Map<String, Object> serializeDetailedOffline = helper.serializeDetailedOffline(offline);
        if (isAdminViewMode(viewMode)) {
            return response(serializeDetailedOffline);
        } else {
            WsModelConfiguration dataModel = new WsModelConfiguration(offlineLoaderService.getDataModel(offlineCode));
            dataModel.setMasterClass((String) serializeDetailedOffline.get("masterClass"));
            return response(dataModel);
        }
    }

    @POST
    @Path("")
    @RolesAllowed(ADMIN_OFFLINE_MODIFY_AUTHORITY)
    public Object create(WsOfflineData data) {
        Offline offline = offlineService.create(data.toOfflineData().build());
        return response(helper.serializeDetailedOffline(offline));
    }

    @PUT
    @Path("/{offlineCode}")
    @RolesAllowed(ADMIN_OFFLINE_MODIFY_AUTHORITY)
    public Object update(@PathParam("offlineCode") String offlineCode, WsOfflineData data) {
        Offline offline = offlineService.update(data.toOfflineData().withCode(offlineCode).build());
        return response(helper.serializeDetailedOffline(offline));
    }

    @DELETE
    @Path("/{offlineCode}")
    @RolesAllowed(ADMIN_OFFLINE_MODIFY_AUTHORITY)
    public Object delete(@PathParam("offlineCode") String offlineCode) {
        offlineService.delete(offlineCode);
        return success();
    }

    @POST
    @Path("/{offlineCode}/lock")
    public Object lock(@PathParam("offlineCode") String offlineCode) {
        return helper.aquireLockOffline(offlineCode);
    }

    @DELETE
    @Path("/{offlineCode}/unlock")
    public Object releaseLock(@PathParam("offlineCode") String offlineCode) {
        return helper.releaseLockOffline(offlineCode);
    }

    protected static class WsOfflineData extends DataDataset {

        public final Boolean isActive;
        public final String masterClass;

        protected WsOfflineData(
                @JsonProperty("active") Boolean isActive,
                @JsonProperty("masterClass") String masterClass
        ) {
            this.isActive = firstNotNull(isActive, true);
            this.masterClass = masterClass;
        }

        protected OfflineDataImplBuilder toOfflineData() {
            ObjectNode metadataNode = (new ObjectMapper()).createObjectNode();
            metadataNode.putPOJO("classes", super.classes);
            metadataNode.putPOJO("processes", super.processes);
            metadataNode.putPOJO("views", super.views);
            metadataNode.put("masterClass", masterClass);
            return OfflineDataImpl.builder().withCode(super.getName()).withDescription(super.getDescription()).withMetadata(toJson(metadataNode)).withEnabled(isActive);
        }
    }

    protected static class WsModelConfiguration extends ModelConfiguration {

        public String masterClass;

        public WsModelConfiguration(ModelConfiguration modelConfiguration) {
            super(modelConfiguration.id, modelConfiguration.name);
            classes = modelConfiguration.classes;
            processes = modelConfiguration.processes;
            views = modelConfiguration.views;
            lookups = modelConfiguration.lookups;
            dmsModels = modelConfiguration.dmsModels;
            dmsCategoryLookups = modelConfiguration.dmsCategoryLookups;
        }

        public void setMasterClass(String masterClass) {
            this.masterClass = masterClass;
        }
    }
}
