package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.api.TenantInfo;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.isPaged;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import jakarta.annotation.security.RolesAllowed;

import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MULTITENANT_CONFIG_PROPERTY_MODE;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MULTITENANT_CONFIG_PROPERTY_TENANT_CLASS;
import org.cmdbuild.auth.multitenant.config.MultitenantMode;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_SYSCONFIG_MODIFY_AUTHORITY;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.auth.multitenant.config.MultitenantMode.MTM_DB_FUNCTION;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Path("tenants/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ACCESS_AUTHORITY)
public class TenantWs {

    private final OperationUserSupplier operationUserSupplier;
    private final MultitenantConfiguration configuration;
    private final MultitenantService service;
    private final CoreConfiguration coreConfig;

    public TenantWs(OperationUserSupplier operationUserSupplier, MultitenantConfiguration configuration, MultitenantService service, CoreConfiguration coreConfig) {
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.configuration = checkNotNull(configuration);
        this.service = checkNotNull(service);
        this.coreConfig = checkNotNull(coreConfig);
    }

    @GET
    @Path("")
    public Object getAll(@Nullable @QueryParam(LIMIT) Integer limit, @Nullable @QueryParam(START) Integer offset) {
        checkArgument(configuration.isMultitenantEnabled(), "multitenant is not enabled");
        List<TenantInfo> list = list(service.getAllActiveTenants()).filter(t -> operationUserSupplier.getUser().getLoginUser().getAvailableTenantContext().getAvailableTenantIds().contains(t.getId()));
        long total;
        if (isPaged(offset, limit)) {
            PagedElements<TenantInfo> paged = PagedElements.paged(list, offset, limit);
            total = paged.totalSize();
            list = paged.elements();
        } else {
            total = list.size();
        }
        return response(list.stream().map((t) -> map("_id", t.getId(), "description", t.getDescription())).collect(toList()), total);
    }

    @POST
    @Path("configure")
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object configureMultitenant(WsTenantConfig configData) {
        checkCanEdit();
        switch (configData.multitenantMode) {
            case MTM_DISABLED:
                service.disableMultitenant();
                return success();
            case MTM_CMDBUILD_CLASS:
                service.enableMultitenantClassMode(configData.tenantClass);
                return success();
            case MTM_DB_FUNCTION:
                service.enableMultitenantFunctionMode();
                return success();
            default:
                throw new IllegalArgumentException("unsupported multitenant mode = " + configData.multitenantMode);
        }
    }

    private void checkCanEdit() { //TODO duplicate code from system config
        checkArgument(coreConfig.allowConfigUpdateViaWs(), "CM_CUSTOM_EXCEPTION: system configuration update is disabled for this instance (demo mode)");//TODO check message
    }

    public static class WsTenantConfig {

        public final MultitenantMode multitenantMode;
        public final String tenantClass;

        public WsTenantConfig(
                @JsonProperty(MULTITENANT_CONFIG_PROPERTY_MODE) String multitenantMode,
                @JsonProperty(MULTITENANT_CONFIG_PROPERTY_TENANT_CLASS) String tenantClass) {
            this.multitenantMode = parseEnum(multitenantMode, MultitenantMode.class);
            this.tenantClass = trimToNull(tenantClass);
        }

    }

}
