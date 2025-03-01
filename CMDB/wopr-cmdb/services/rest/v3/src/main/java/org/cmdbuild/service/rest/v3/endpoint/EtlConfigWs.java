package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
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
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.dao.utils.CmFilterProcessingUtils.mapFilter;
import org.cmdbuild.etl.config.WaterwayDescriptorInfoExt;
import org.cmdbuild.etl.config.WaterwayDescriptorMeta;
import org.cmdbuild.etl.config.WaterwayDescriptorMetaImpl;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItemInfo;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.buildDescriptorDataAndParams;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.descriptorDataJsonToYaml;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("etl/configs/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
public class EtlConfigWs {

    private final WaterwayDescriptorService service;

    public EtlConfigWs(WaterwayDescriptorService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(WsQueryOptions wsQueryOptions, @QueryParam("includeMeta") @DefaultValue(FALSE) Boolean includeMeta) {
        return response(paged(list(service.getAllDescriptors()).map(e -> wsQueryOptions.isDetailed() ? serializeDetailedConfigFile(e, includeMeta) : serializeBasicConfigFile(e)).withOnly(mapFilter(wsQueryOptions.getQuery().getFilter())), wsQueryOptions.getQuery()));
    }

    @GET
    @Path("{code}/")
    public Object read(@PathParam("code") String code, @QueryParam("includeMeta") @DefaultValue(FALSE) Boolean includeMeta, @QueryParam("if_exists") @DefaultValue(FALSE) Boolean checkIfExists) {
        if (checkIfExists) {
            WaterwayDescriptorRecord configFile = service.getDescriptorOrNull(code);
            return response(configFile == null ? map("exists", false) : map(serializeDetailedConfigFile(configFile, includeMeta)).with("exists", true));
        } else {
            return response(serializeDetailedConfigFile(service.getDescriptor(code), includeMeta));
        }
    }

    @GET
    @Path("{code}/items")
    public Object readItems(WsQueryOptions wsQueryOptions, @PathParam("code") String code) {
        return response(paged(list(service.getAllItems()).filter(i -> equal(code, "_ALL") || equal(i.getDescriptorCode(), code)).map(e -> serializeItem(e)).withOnly(mapFilter(wsQueryOptions.getQuery().getFilter())), wsQueryOptions.getQuery()));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object create(@Multipart(value = FILE, required = false) DataHandler dataHandler, @Nullable WsConfigMeta meta, @QueryParam("overwriteIfExists") @DefaultValue(FALSE) Boolean overwriteIfExists) {
        DataSource data = dataHandler != null ? toDataSource(dataHandler) : newDataSource(checkNotBlank(meta.data, "missing configuration file payload"));
        return response(serializeDetailedConfigFile(service.createUpdateDescriptor(data, meta == null ? null : meta.toMeta(), overwriteIfExists), false));
    }

    @PUT
    @Path("{code}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object update(@PathParam("code") String code, @Multipart(value = FILE, required = false) DataHandler dataHandler, @Nullable WsConfigMeta meta) {
        DataSource data = dataHandler != null ? toDataSource(dataHandler) : (meta == null || isBlank(meta.data) ? null : newDataSource(meta.data));
        if (data == null) {
            service.updateDescriptorMeta(code, checkNotNull(meta, "missing config file meta").toMeta());
            return read(code, false, false);
        } else {
            return response(serializeDetailedConfigFile(service.createUpdateDescriptor(data, meta == null ? null : meta.toMeta()), false));
        }
    }

    @DELETE
    @Path("{code}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object delete(@PathParam("code") String code) {
        service.deleteDescriptor(code);
        return success();
    }

    private FluentMap<String, Object> serializeBasicConfigFile(WaterwayDescriptorInfoExt configFile) {
        return (FluentMap) map(
                "_id", configFile.getCode(),
                "code", configFile.getCode(),
                "description", configFile.getDescription(),
                "notes", configFile.getNotes(),
                "version", configFile.getVersion(),
                "enabled", configFile.isEnabled(),
                "valid", configFile.isValid(),
                "disabled", Joiner.on(",").join(configFile.getDisabledItems()),
                "params", configFile.getParams(),
                "tag", configFile.getTag()
        );
    }

    private FluentMap<String, Object> serializeDetailedConfigFile(WaterwayDescriptorInfoExt configFile, boolean includeMeta) {
        WaterwayDescriptorRecord configRecord = configFile instanceof WaterwayDescriptorRecord ? ((WaterwayDescriptorRecord) configFile) : service.getDescriptor(configFile.getCode());
        String data = descriptorDataJsonToYaml(configRecord.getData());
        if (includeMeta) {
            data = buildDescriptorDataAndParams(data, configRecord);
        }
        return serializeBasicConfigFile(configFile).with("data", data);
    }

    private Object serializeItem(WaterwayItemInfo i) {
        return map(
                "_id", i.getCode(),
                "code", i.getCode(),
                "type", serializeEnum(i.getType()),
                "enabled", i.isEnabled(),
                "description", i.getDescription(),
                "notes", i.getNotes(),
                "descriptor", i.getDescriptorKey()).skipNullValues().with(
                "subtype", i.getSubtype());
    }

    public static class WsConfigMeta {

        private final List<String> disabled;
        private final Map<String, String> params;
        private final boolean enabled;
        private final String data, code, description;

        public WsConfigMeta(@JsonProperty("disabled") String disabled,
                @JsonProperty("enabled") Boolean enabled,
                @JsonProperty("params") Map<String, String> params,
                @JsonProperty("data") String data,
                @JsonProperty("code") String code,
                @JsonProperty("description") String description) {
            this.disabled = toListOfStrings(disabled);
            this.enabled = firstNotNull(enabled, true);
            this.params = map(checkNotNull(params)).immutable();
            this.data = data;
            this.code = code;
            this.description = description;
        }

        public WaterwayDescriptorMeta toMeta() {
            return WaterwayDescriptorMetaImpl.builder().withEnabled(enabled).withDisabledItems(disabled).withParams(params).withCode(code).withDescription(description).build();
        }

    }

}
