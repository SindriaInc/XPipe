package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static jakarta.ws.rs.core.MediaType.WILDCARD;
import static java.util.Collections.singleton;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_SYSCONFIG_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_SYSCONFIG_VIEW_AUTHORITY;
import org.cmdbuild.config.api.ConfigDefinition;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.config.api.GlobalConfigService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("plugin/config")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_SYSCONFIG_VIEW_AUTHORITY)
public class PluginConfigWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GlobalConfigService configService;

    public PluginConfigWs(GlobalConfigService configService) {
        this.configService = checkNotNull(configService);
    }

    @GET
    @Path("")
    public Object getSystemConfigAll(@QueryParam("detailed") @DefaultValue(FALSE) Boolean detailed) {
        logger.debug("get system config");
        Map<String, String> storedConfigs = configService.getStoredConfigAsMap();
        if (detailed) {
            return response(set(configService.getConfigDefinitions().keySet()).with(storedConfigs.keySet()).stream().sorted(Comparator.naturalOrder()).map((k) -> {
                ConfigDefinition configDefinition = configService.getConfigDefinitionOrNull(k);
                boolean hasValue = storedConfigs.containsKey(k);
                FluentMap map = map("hasDefinition", configDefinition != null, "hasValue", hasValue);
                if (configDefinition != null) {
                    map.put("description", configDefinition.getDescription(),
                            "default", configDefinition.getDefaultValue(),
                            "oneof", configDefinition.getEnumValues(),
                            "category", serializeEnum(configDefinition.getCategory()),
                            "location", serializeEnum(configDefinition.getLocation()),
                            "modular", serializeEnum(configDefinition.getModular()),
                            "module", configDefinition.getModuleNamespace());
                }
                if (hasValue) {
                    map.put("value", storedConfigs.get(k));
                }
                return Pair.of(k, map);
            }).collect(toMap(Pair::getLeft, Pair::getRight)));
        } else {
            return response(map(storedConfigs));
        }
    }

    @GET
    @Path("/{key}")
    public Object getSystemConfigValue(@PathParam("key") String key, @QueryParam("include_default") @DefaultValue(TRUE) Boolean includeDefault) {
        String value;
        if (includeDefault) {
            value = configService.getStringOrDefault(key);
        } else {
            value = configService.getString(key);
        }
        return response(value);
    }

    @PUT
    @Path("/{key}")
    @Consumes(TEXT_PLAIN)
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object updateSystemConfigValue(@PathParam("key") String key, @Nullable String value, @QueryParam("encrypt") Boolean encrypt) {
        checkNotProtected(singleton(key));
        if (equal(value, "default")) {
            deleteSystemConfigValue(key);
        } else {
            logger.info("update system config for key =< {} > value =< {} >", key, value);
            configService.putString(key, value, firstNonNull(encrypt, false));
        }
        return success();
    }

    @PUT
    @Path("/_MANY")
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object updateSystemConfigValues(Map<String, String> data) {
        checkNotProtected(data.keySet());
        logger.info("update system config with data = {}", data);
        configService.putStrings(data);
        return success();
    }

    @DELETE
    @Path("/{key}")
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object deleteSystemConfigValue(@PathParam("key") String key) {
        checkNotProtected(singleton(key));
        logger.info("delete system config by key = {}", key);
        configService.delete(key);
        return success();
    }

    @POST
    @Path("/reload")
    @Consumes(WILDCARD)
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object reloadConfig() {
        logger.info("reload config");
        configService.reload();
        return success();
    }

    private void checkNotProtected(Set<String> keys) {
        keys = keys.stream().filter(configService::isProtected).collect(toSet());
        checkArgument(keys.isEmpty(), "you are not allowed to manually update these protected config params = %s : operation not allowed", keys);
    }

}
