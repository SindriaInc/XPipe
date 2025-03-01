package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
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
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.cleanup.ViewType;
import static org.cmdbuild.cleanup.ViewType.VT_JOIN;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.contextmenu.ContextMenuService;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryTypeType;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import static org.cmdbuild.dao.utils.CmSorterUtils.parseSorter;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.formstructure.FormStructure;
import org.cmdbuild.formstructure.FormStructureImpl;
import org.cmdbuild.formstructure.FormStructureService;
import org.cmdbuild.report.SysReportService;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ContextMenuSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ContextMenuSerializationHelper.WsClassDataContextMenuItem;
import org.cmdbuild.service.rest.common.serializationhelpers.ViewSerializer;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.nullableToJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.unkey;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewImpl;
import org.cmdbuild.view.ViewImpl.ViewImplBuilder;
import org.cmdbuild.view.ViewService;
import static org.cmdbuild.view.ViewService.ATTR_DESCR_INHERITED_FROM;
import org.cmdbuild.view.join.JoinAttribute;
import org.cmdbuild.view.join.JoinAttributeGroup;
import org.cmdbuild.view.join.JoinAttributeGroupImpl;
import org.cmdbuild.view.join.JoinAttributeImpl;
import org.cmdbuild.view.join.JoinElement;
import org.cmdbuild.view.join.JoinElementImpl;
import org.cmdbuild.view.join.JoinType;
import org.cmdbuild.view.join.JoinViewConfig;
import org.cmdbuild.view.join.JoinViewConfigImpl;
import org.cmdbuild.view.join.JoinViewPrivilegeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("views/")
@Produces(APPLICATION_JSON)
public class ViewWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ViewService viewService;
    private final ViewSerializer viewSerializer;
    private final ObjectTranslationService translationService;
    private final SysReportService reportService;
    private final AttributeTypeConversionService attributeHelper;
    private final FormStructureService formStructureService;
    private final OperationUserSupplier userStore;
    private final DaoService daoService;
    private final ContextMenuService contextMenuService;
    private final ContextMenuSerializationHelper contextMenuSerializationHelper;

    public ViewWs(
            ViewService viewService,
            ViewSerializer viewSerializer,
            ObjectTranslationService translationService,
            SysReportService reportService,
            AttributeTypeConversionService attributeHelper,
            FormStructureService formStructureService,
            OperationUserSupplier userStore,
            DaoService daoService,
            ContextMenuService contextMenuService,
            ContextMenuSerializationHelper contextMenuSerializationHelper) {
        this.viewService = checkNotNull(viewService);
        this.viewSerializer = checkNotNull(viewSerializer);
        this.translationService = checkNotNull(translationService);
        this.reportService = checkNotNull(reportService);
        this.attributeHelper = checkNotNull(attributeHelper);
        this.formStructureService = checkNotNull(formStructureService);
        this.userStore = checkNotNull(userStore);
        this.daoService = checkNotNull(daoService);
        this.contextMenuService = checkNotNull(contextMenuService);
        this.contextMenuSerializationHelper = checkNotNull(contextMenuSerializationHelper);
    }

    @GET
    @Path(EMPTY)
    public Object getMany(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed, @QueryParam("shared") @DefaultValue(FALSE) Boolean sharedOnly) {
        logger.debug("list all views");
        List<View> views = isAdminViewMode(viewMode) ? viewService.getAllSharedViews() : viewService.getActiveViewsForCurrentUser();
        logger.trace("processing views = {}", views);
        return response(views.stream().map(v -> detailed ? viewSerializer.serializeDetailedView(v) : viewSerializer.serializeView(v)).collect(toList()));
    }

    @GET
    @Path("{viewId}")
    public Object getOne(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("viewId") String viewId) {
        View view = isAdminViewMode(viewMode) ? viewService.getSharedByName(viewId) : viewService.getForCurrentUserByNameOrId(viewId);
        return response(viewSerializer.serializeDetailedView(view));
    }

    @GET
    @Path("{viewId}/attributes")
    public Object getAttributes(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("viewId") String viewId) {
        View view = isAdminViewMode(viewMode) ? viewService.getSharedByName(viewId) : viewService.getForCurrentUserByNameOrId(viewId);
        Collection<Attribute> attributesForView = viewService.getAttributesForView(view);
        return response(attributesForView.stream().map(a -> attributeHelper.serializeAttributeType(a).accept(m -> {
            if (a.getMetadata().hasValue(ATTR_DESCR_INHERITED_FROM)) {
                List<String> info = unkey(a.getMetadata().get(ATTR_DESCR_INHERITED_FROM));
                m.put("_description_translation", translationService.translateAttributeDescription(daoService.getType(parseEnum(info.get(0), EntryTypeType.class), info.get(1)).getAttribute(info.get(2))));//TODO improve this
            }
        })).collect(toList()));
    }

    @POST
    @Path("")
    public Object create(WsViewData data) {
        View view = viewService.createForCurrentUser(data.toView().accept(setCurrentUser(data)).build());
        formStructureService.setFormForView(view, data.getFormStructure());
        contextMenuService.updateContextMenuItemsForView(view, contextMenuSerializationHelper.toContextMenuItems(data.getContextMenuItems()));
        return response(viewSerializer.serializeDetailedView(view));
    }

    @PUT
    @Path("{viewId}")
    public Object update(@PathParam("viewId") String viewId, WsViewData data) {
        View view = viewService.updateForCurrentUser(data.toView().accept(setViewId(viewId)).accept(setCurrentUser(data)).build());//TODO use viewId ???
        formStructureService.setFormForView(view, data.getFormStructure());
        contextMenuService.updateContextMenuItemsForView(view, contextMenuSerializationHelper.toContextMenuItems(data.getContextMenuItems()));
        return response(viewSerializer.serializeDetailedView(view));
    }

    @DELETE
    @Path("{viewId}")
    public Object delete(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("viewId") String viewId) {
        viewService.delete((isAdminViewMode(viewMode) ? viewService.getSharedByName(viewId) : viewService.getForCurrentUserByNameOrId(viewId)).getId());
        return success();
    }

    @GET
    @Path("{viewId}/print/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printView(@PathParam("viewId") String viewId, @QueryParam(FILTER) String filterStr, @QueryParam(SORT) String sort, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(EXTENSION) String extension, @QueryParam("attributes") String attributes) {
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder().withFilter(filterStr).withSorter(sort).withPaging(offset, limit).withAttrs(isBlank(attributes) ? (List) null : fromJson(attributes, LIST_OF_STRINGS)).build();
        return reportService.executeViewReport(viewService.getForCurrentUserByNameOrId(viewId), reportExtFromString(extension), queryOptions);
    }

    private Consumer<ViewImplBuilder> setViewId(String viewId) {
        return (b) -> {
            if (NumberUtils.isCreatable(viewId)) {
                b.withId(toLong(viewId));
            } else {
                b.withName(viewId);
            }
        };
    }

    private Consumer<ViewImplBuilder> setCurrentUser(WsViewData data) {
        return (b) -> {
            if (!data.shared) {
                b.withUserId(userStore.getUser().getLoginUser().getId());
            }
        };
    }

    public static class WsViewData {

        private final String name;
        private final String description;
        private final String sourceClassName;
        private final String sourceFunction;
        private final ViewType type;
        private final Boolean active, shared;
        private final String masterClass, masterClassAlias;
        private final CmdbFilter filter;
        private final CmdbSorter sorter;
        private final List<WsJoinElement> joinElements;
        private final List<WsJoinAttribute> attributes;
        private final List<WsJoinAttributeGroup> attributeGroups;
        private final JsonNode formStructure;
        private final JoinViewPrivilegeMode privilegeMode;
        private final List<WsClassDataContextMenuItem> contextMenuItems;

        public WsViewData(
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("sourceClassName") String sourceClassName,
                @JsonProperty("sourceFunction") String sourceFunction,
                @JsonProperty("filter") String filter,
                @JsonProperty("active") Boolean active,
                @JsonProperty("shared") Boolean shared,
                @JsonProperty("type") String type,
                @JsonProperty("masterClass") String masterClass,
                @JsonProperty("masterClassAlias") String masterClassAlias,
                @JsonProperty("sorter") JsonNode sorter,
                @JsonProperty("join") List<WsJoinElement> joinElements,
                @JsonProperty("attributes") List<WsJoinAttribute> attributes,
                @JsonProperty("attributeGroups") List<WsJoinAttributeGroup> attributeGroups,
                @JsonProperty("formStructure") JsonNode formStructure,
                @JsonProperty("privilegeMode") String privilegeMode,
                @JsonProperty("contextMenuItems") List<WsClassDataContextMenuItem> contextMenuItems) {
            this.name = checkNotBlank(name);
            this.description = description;
            this.sourceClassName = sourceClassName;
            this.sourceFunction = sourceFunction;
            this.filter = parseFilter(filter);
            this.active = active;
            this.shared = shared;
            this.type = parseEnum(type, ViewType.class);
            this.masterClass = masterClass;
            this.masterClassAlias = masterClassAlias;
            this.sorter = parseSorter(nullableToJson(sorter));
            this.joinElements = nullToEmpty(joinElements);
            this.attributes = attributes;
            this.attributeGroups = nullToEmpty(attributeGroups);
            this.formStructure = formStructure;
            this.contextMenuItems = contextMenuItems;
            this.privilegeMode = parseEnumOrNull(privilegeMode, JoinViewPrivilegeMode.class);
        }

        public ViewImplBuilder toView() {
            return ViewImpl.builder()
                    .withName(name)
                    .withDescription(description)
                    .withSourceClass(sourceClassName)
                    .withSourceFunction(sourceFunction)
                    .withType(type)
                    .withActive(active)
                    .withShared(shared)
                    .accept(b -> {
                        switch (type) {
                            case VT_JOIN ->
                                b.withJoinConfig(toJoinConfig());
                            default ->
                                b.withFilter(serializeFilter(filter));
                        }
                    });
        }

        public JoinViewConfig toJoinConfig() {
            return JoinViewConfigImpl.builder()
                    .withFilter(filter)
                    .withMasterClass(masterClass)
                    .withMasterClassAlias(masterClassAlias)
                    .withSorter(sorter)
                    .withAttributes(list(attributes).map(WsJoinAttribute::toJoinAttribute))
                    .withAttributeGroups(list(attributeGroups).map(WsJoinAttributeGroup::toJoinAttributeGroup))
                    .withJoinElements(list(joinElements).map(WsJoinElement::toJoinElement))
                    .withPrivilegeMode(privilegeMode)
                    .build();
        }

        @Nullable
        public FormStructure getFormStructure() {
            return formStructure == null ? null : new FormStructureImpl(toJson(formStructure));
        }

        @Nullable
        public List<WsClassDataContextMenuItem> getContextMenuItems() {
            return firstNotNull(contextMenuItems, list());
        }
    }

    public static class WsJoinElement {

        private final String source, domain, targetType, domainAlias, targetAlias;
        private final RelationDirection direction;
        private final JoinType joinType;

        public WsJoinElement(
                @JsonProperty("source") String source,
                @JsonProperty("domain") String domain,
                @JsonProperty("targetType") String targetType,
                @JsonProperty("domainAlias") String domainAlias,
                @JsonProperty("targetAlias") String targetAlias,
                @JsonProperty("direction") String direction,
                @JsonProperty("joinType") String joinType) {
            this.source = source;
            this.domain = domain;
            this.targetType = targetType;
            this.domainAlias = domainAlias;
            this.targetAlias = targetAlias;
            this.direction = parseEnum(direction, RelationDirection.class);
            this.joinType = parseEnum(joinType, JoinType.class);
        }

        public JoinElement toJoinElement() {
            return JoinElementImpl.builder()
                    .withDirection(direction)
                    .withDomain(domain)
                    .withDomainAlias(domainAlias)
                    .withJoinType(joinType)
                    .withSource(source)
                    .withTargetAlias(targetAlias)
                    .withTargetType(targetType)
                    .build();
        }

    }

    public static class WsJoinAttribute {

        private final String expr, name, description, group;
        private final Boolean showInGrid, showInReducedGrid;

        public WsJoinAttribute(
                @JsonProperty("expr") String expr,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("group") String group,
                @JsonProperty("showInGrid") Boolean showInGrid,
                @JsonProperty("showInReducedGrid") Boolean showInReducedGrid) {
            this.expr = expr;
            this.name = name;
            this.description = description;
            this.group = group;
            this.showInGrid = showInGrid;
            this.showInReducedGrid = showInReducedGrid;
        }

        public JoinAttribute toJoinAttribute() {
            return JoinAttributeImpl.builder()
                    .withExpr(expr)
                    .withDescription(description)
                    .withGroup(group)
                    .withName(name)
                    .withShowInGrid(showInGrid)
                    .withShowInReducedGrid(showInReducedGrid)
                    .build();
        }
    }

    public static class WsJoinAttributeGroup {

        private final String name, description, defaultDisplayMode;

        public WsJoinAttributeGroup(@JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("defaultDisplayMode") String defaultDisplayMode) {
            this.name = name;
            this.description = description;
            this.defaultDisplayMode = defaultDisplayMode;
        }

        public JoinAttributeGroup toJoinAttributeGroup() {
            return JoinAttributeGroupImpl.builder().withName(name).withDescription(description).withDefaultDisplayMode(defaultDisplayMode).build();
        }
    }
}
