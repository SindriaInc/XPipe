/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import java.io.IOException;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
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
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_BIM_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_BIM_VIEW_AUTHORITY;
import org.cmdbuild.bim.BimObject;
import org.cmdbuild.bim.BimProject;
import org.cmdbuild.bim.BimProjectExt;
import org.cmdbuild.bim.BimProjectExtImpl;
import org.cmdbuild.bim.BimProjectImpl;
import org.cmdbuild.bim.BimProjectImpl.BimProjectImplBuilder;
import org.cmdbuild.bim.BimService;
import static org.cmdbuild.bim.utils.BimConstants.IFC_CONTENT_TYPE;
import org.cmdbuild.common.beans.CardIdAndClassName;
import static org.cmdbuild.common.beans.CardIdAndClassNameImpl.card;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import org.cmdbuild.utils.ifc.utils.IfcUtils;
import org.cmdbuild.config.BimConfiguration;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("bim/projects/")
@Produces(APPLICATION_JSON)
public class BimProjectWs {

    private final BimService service;
    private final BimConfiguration config;

    public BimProjectWs(BimService bim, BimConfiguration config) {
        this.service = checkNotNull(bim);
        this.config = checkNotNull(config);
    }

    @GET
    @Path("")
    public Object getAll() {
        return response(service.getAllProjectsAndObjects().stream().sorted(Ordering.natural().onResultOf(BimProjectExt::getName)).map(this::serializeProjectAndObject));
    }

    @GET
    @Path("{projectId}/values/{globalId}")
    public Object getValue(@PathParam("projectId") Long projectId, @PathParam("globalId") String globalId, @QueryParam("if_exists") @DefaultValue(FALSE) Boolean ifExists) {
        BimObject value = service.getBimObjectForProjectGlobalIdOrNull(service.getProjectById(projectId), globalId);
        if (ifExists && value == null) {
            return response(map("exists", false));
        }
        checkNotNull(value, "bim value not found for gid =< %s >", globalId);
        return response(map(
                "_id", value.getId(),
                "ownerType", value.getOwnerClassId(),
                "ownerId", value.getOwnerCardId(),
                "projectId", value.getProjectId(),
                "globalId", value.getGlobalId()
        ).accept((m) -> {
            if (ifExists) {
                m.put("exists", true);
            }
        }));
    }

    @GET
    @Path("{id}")
    public Object getOne(@PathParam("id") Long id) {
        BimProjectExt projectExt = service.getProjectExt(id);
        return response(serializeProjectAndObject(projectExt));
    }

    @POST
    @Path("")
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_BIM_MODIFY_AUTHORITY)
    public Object createProjectWithFile(@Multipart(value = FILE, required = false) DataHandler dataHandler, WsProjectData data) {
        if (dataHandler == null) {
            return response(serializeProjectAndObject(service.createProjectExt(data.toBimProject().build(), data.toOwnerOrNull())));
        } else {
            String schema;
            BimProjectExt project;
            switch (config.getViewer()) {
                case BV_BIMSERVER -> {
                    schema = IfcUtils.detectSchema(toDataSource(dataHandler)).name();
                    project = service.createProjectExt(new BimProjectExtImpl(data.toBimProject().withIfcFormat(schema).build(), data.toOwnerOrNull()));
                    service.uploadIfcFile(project.getId(), dataHandler, null);
                    project = service.getProjectExt(project.getId());
                }
                case BV_XEOKIT -> {
                    project = service.createProjectExt(new BimProjectExtImpl(data.toBimProject().build(), data.toOwnerOrNull()));
                    service.uploadXktFile(project.getId(), dataHandler, true);
                    project = service.getProjectExt(project.getId());
                }
                default ->
                    throw unsupported("unsupported bim viewer = %s", config.getViewer());
            }
            return response(serializeProjectAndObject(project));
        }
    }

    @PUT
    @Path("{id}")
    @RolesAllowed(ADMIN_BIM_MODIFY_AUTHORITY)
    public Object update(@PathParam("id") Long id, @Multipart(value = FILE, required = false) DataHandler dataHandler, WsProjectData data) {
        service.updateProjectExt(data.toBimProject().withId(id).build(), data.toOwnerOrNull());
        if (dataHandler != null) {
            switch (config.getViewer()) {
                case BV_BIMSERVER ->
                    service.uploadIfcFile(id, dataHandler, null);
                case BV_XEOKIT ->
                    service.uploadXktFile(id, dataHandler, false);
                default ->
                    throw unsupported("unsupported bim viewer = %s", config.getViewer());
            }
        }
        return response(serializeProjectAndObject(service.getProjectExt(id)));
    }

    @GET
    @Path("{id}/file")
    public DataHandler downloadIfcFile(@PathParam("id") Long id, @QueryParam("ifcFormat") @Nullable String ifcFormat, @QueryParam("bimFormat") String bimFormat) {
        return switch (bimFormat) {
            case "ifc" ->
                service.downloadIfcFile(id, ifcFormat);
            case "xkt" ->
                service.downloadXktFile(id);
            default ->
                throw unsupported("Invalid bim format %s", bimFormat);
        };
    }

    @POST
    @Path("{id}/file")
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_BIM_MODIFY_AUTHORITY)
    public Object uploadIfcFile(@PathParam("id") Long id, @Multipart(FILE) DataHandler dataHandler) throws IOException {
        switch (config.getViewer()) {
            case BV_BIMSERVER ->
                service.uploadIfcFile(id, dataHandler, null);
            case BV_XEOKIT ->
                service.uploadXktFile(id, dataHandler, false);
            default ->
                throw unsupported("unsupported bim viewer = %s", config.getViewer());
        }
        return success();
    }

    @POST
    @Path("{id}/convert/xkt")
    @RolesAllowed(ADMIN_BIM_MODIFY_AUTHORITY)
    public Object convertIfcToXkt(@PathParam("id") Long id) {
        BimProject convertIfcProjectToXkt = service.convertIfcProjectToXkt(id);
        service.copyBimserverIfcToDatabase(id);
        return response(serializeProject(convertIfcProjectToXkt));
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed(ADMIN_BIM_MODIFY_AUTHORITY)
    public Object delete(@PathParam("id") Long id) {
        service.deleteProject(id);
        return success();
    }

    private FluentMap<String, Object> serializeProject(BimProject p) {
        return map(
                "_id", p.getId(),
                "parentId", p.getParentId(),
                "name", p.getName(),
                "description", p.getDescription(),
                "lastCheckin", toIsoDateTime(p.getLastCheckin()),
                "projectId", p.getProjectId(),
                "active", p.isActive(),
                "_can_convert", p.getXktFile() == null
        );
    }

    private Object serializeProjectAndObject(BimProjectExt projectAndObject) {
        return serializeProject(projectAndObject).accept(m -> {
            if (projectAndObject.hasOwner()) {
                m.put(
                        "ownerClass", projectAndObject.getOwner().getClassName(),
                        "ownerCard", projectAndObject.getOwner().getId());
            }
        });
    }

    public static class WsProjectData {

        private final String name, description, importMapping, projectId, ownerClass, ifcFormat;
        private final Boolean active;
        private final Long parentId, ownerCard;

        public WsProjectData(
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("importMapping") String importMapping,
                @JsonProperty("projectId") String projectId,
                @JsonProperty("parentId") Long parentId,
                @JsonProperty("ownerClass") String ownerClass,
                @JsonProperty("ifcFormat") String ifcFormat,
                @JsonProperty("ownerCard") Long ownerCard,
                @JsonProperty("active") Boolean active) {
            this.projectId = projectId;
            this.name = checkNotBlank(name);
            this.description = description;
            this.ownerClass = ownerClass;
            this.importMapping = importMapping;
            this.active = firstNonNull(active, true);
            this.parentId = parentId;
            this.ownerCard = ownerCard;
            this.ifcFormat = ifcFormat;
        }

        public BimProjectImplBuilder toBimProject() {
            return BimProjectImpl.builder()
                    .withName(name)
                    .withDescription(description)
                    .withActive(active)
                    .withParentId(parentId)
                    .withImportMapping(importMapping)
                    .withIfcFormat(ifcFormat)
                    .withProjectId(projectId);
        }

        @Nullable
        public CardIdAndClassName toOwnerOrNull() {
            if (isNotBlank(ownerClass) && isNotNullAndGtZero(ownerCard)) {
                return card(ownerClass, ownerCard);
            } else {
                return null;
            }

        }

    }
}
