package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
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
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_NAVTREES_MODIFY_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.inner.EcqlExpressionImpl;
import org.cmdbuild.ecql.utils.EcqlUtils;
import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.navtree.NavTreeImpl;
import org.cmdbuild.navtree.NavTreeImpl.NavTreeDataImplBuilder;
import org.cmdbuild.navtree.NavTreeNode;
import org.cmdbuild.navtree.NavTreeNodeImpl;
import org.cmdbuild.navtree.NavTreeNodeSubclassViewMode;
import org.cmdbuild.navtree.NavTreeService;
import org.cmdbuild.navtree.NavTreeType;
import static org.cmdbuild.navtree.NavTreeType.NT_DEFAULT;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.service.rest.v3.endpoint.NavTreeWs.TreeMode.TREE;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.json.CmJsonUtils.collectionType;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMap;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrDefault;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

@Path("domainTrees")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class NavTreeWs {

    private final NavTreeService service;
    private final ObjectTranslationService translationService;

    public NavTreeWs(NavTreeService service, ObjectTranslationService translationService) {
        this.service = checkNotNull(service);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(FILTER) String filterStr, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        List<NavTree> list = isAdminViewMode(viewMode) ? service.getAll() : service.getAllActive();
        if (filter.hasFilter()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            list = AttributeFilterProcessor.<NavTree>builder()
                    .withKeyToValueFunction((key, tree) -> {
                        return switch (checkNotBlank(key)) {
                            case "targetClass" ->
                                tree.getData().getTargetClassName();
                            case "type" ->
                                serializeEnum(tree.getType());
                            default ->
                                throw new IllegalArgumentException("invalid attribute filter key = " + key);
                        };
                    })
                    .withFilter(filter.getAttributeFilter()).build().filter(list);
        }
        return response(paged(list, offset, limit).map((tree) -> map(
                "_id", tree.getName(),
                "description", tree.getDescription(),
                "_description_translation", translationService.translateNavtreeDescription(tree.getName(), tree.getDescription()),
                "active", tree.getActive(),
                "type", serializeEnum(tree.getType()))));
    }

    @GET
    @Path("{treeId}/")
    public Object read(@PathParam("treeId") String id, @QueryParam("treeMode") @DefaultValue("flat") String treeMode) {
        NavTree root = service.getTree(id);
        return response(serializeTree(root, parseEnum(treeMode, TreeMode.class)));
    }

    @POST
    @Path("")
    @RolesAllowed(ADMIN_NAVTREES_MODIFY_AUTHORITY)
    public Object create(WsTreeData data) {
        NavTree tree = data.toTreeNode().build();
        tree = service.create(tree);
        return response(serializeTree(tree, TREE));
    }

    @PUT
    @Path("{treeId}")
    @RolesAllowed(ADMIN_NAVTREES_MODIFY_AUTHORITY)
    public Object update(@PathParam("treeId") String id, WsTreeData data) {
        NavTree tree = data.toTreeNode().withName(id).build();
        tree = service.update(tree);
        return response(serializeTree(tree, TREE));
    }

    @DELETE
    @Path("{treeId}")
    @RolesAllowed(ADMIN_NAVTREES_MODIFY_AUTHORITY)
    public Object delete(@PathParam("treeId") String id) {
        service.removeTree(id);
        return success();
    }

    @POST
    @Path("{treeId}/fixDirections")
    @RolesAllowed(ADMIN_NAVTREES_MODIFY_AUTHORITY)
    public Object fixNavtreeDirections(@PathParam("treeId") String id) {
        service.fixDirections(id);
        return success();
    }

    private FluentMap serializeTree(NavTree root, TreeMode mode) {
        List nodes = switch (mode) {
            case FLAT ->
                root.getData().getThisNodeAndAllDescendants().stream().map(n -> serializeNode(root, n)).collect(toList());
            case TREE ->
                singletonList(serializeNodeAndDescendants(root, root.getData()));
            default ->
                throw new IllegalArgumentException();
        };
        return map(
                "_id", root.getName(),
                "name", root.getName(),
                "description", root.getDescription(),
                "_description_translation", translationService.translateNavtreeDescription(root.getName(), root.getDescription()),
                "active", root.getActive(),
                "type", serializeEnum(root.getType()),
                "nodes", nodes
        );
    }

    private FluentMap serializeNodeAndDescendants(NavTree tree, NavTreeNode node) {
        return serializeNode(tree, node).with("nodes", node.getChildNodes().stream().map(n -> serializeNodeAndDescendants(tree, n)).collect(toList()));
    }

    private FluentMap serializeNode(NavTree tree, NavTreeNode node) {
        return map(
                "_id", node.getId(),
                "filter", node.getTargetFilter(),
                "targetClass", node.getTargetClassName(),
                "recursionEnabled", node.getEnableRecursion(),
                "domain", node.getDomainName(),
                "showOnlyOne", node.getShowOnlyOne(),
                "subclassViewMode", serializeEnum(node.getSubclassViewMode()),
                "subclassViewShowIntermediateNodes", node.getSubclassViewShowIntermediateNodes(),
                "description", node.getTargetClassDescription(),
                "_description_translation", translationService.translateNavtreeItemDescription(tree.getName(), node.getId(), node.getTargetClassDescription())
        ).accept(m -> {
            if (node.hasFilter()) {
                EcqlBindingInfo ecqlBindingInfo = EcqlUtils.getEcqlBindingInfoForExpr(new EcqlExpressionImpl(node.getTargetFilter()));
                m.put("ecqlFilter", map(
                        "id", EcqlUtils.buildNavTreeEcqlId(tree.getName(), node.getId()),
                        "bindings", map("server", ecqlBindingInfo.getServerBindings(), "client", ecqlBindingInfo.getClientBindings())
                ));
            }
            node.getSubclassDescriptions().forEach((k, v) -> {
                m.put(format("subclass_%s_description", k), nullToEmpty(v));
                m.put(format("_subclass_%s_description_translation", k), translationService.translateNavtreeItemSubclassDescription(tree.getName(), node.getId(), k, nullToEmpty(v)));
            });
        }).skipNullValues().with(
                "parent", node.getParentId(),
                "direction", isBlank(node.getDomainName()) ? null : (node.getDirect() ? "_1" : "_2"),
                "subclassFilter", emptyToNull(Joiner.on(",").join(node.getSubclassFilter()))
        ).then();
    }

    enum TreeMode {
        FLAT, TREE
    }

    public static class WsTreeData {

        private final String name, description;
        private final WsTreeNodeData data;
        private final boolean active;
        private final NavTreeType type;

        public WsTreeData(@JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("nodes") List<WsTreeNodeData> nodes,
                @JsonProperty("active") boolean active,
                @JsonProperty("type") String type) {
            this.name = checkNotBlank(name, "nav tree name cannot be null");
            this.description = nullToEmpty(description);
            this.data = checkNotNull(getOnlyElement(nodes, null), "a nav tree must have a root node");
            this.active = active;
            this.type = parseEnumOrDefault(type, NT_DEFAULT);
        }

        public NavTreeDataImplBuilder toTreeNode() {
            return NavTreeImpl.builder()
                    .withName(name)
                    .withDescription(description)
                    .withData(data.toTreeNode())
                    .withActive(active)
                    .withType(type);
        }

    }

    public static class WsTreeNodeData {

        private final String id;
        private final String filter, targetClass, description, domain, direction;
        private final Boolean recursionEnabled, showOnlyOne, subclassViewShowIntermediateNodes;
        private final List<WsTreeNodeData> nodes;
        private final NavTreeNodeSubclassViewMode subclassViewMode;
        private final List<String> subclassFilter;
        private final Map<String, String> subclassDescriptions;

        @JsonCreator
        public WsTreeNodeData(Map<String, Object> values) {
            this.id = toStringOrNull(values.get("_id"));
            this.filter = toStringOrNull(values.get("filter"));
            this.targetClass = toStringOrNull(values.get("targetClass"));
            this.description = toStringOrNull(values.get("description"));
            this.domain = toStringOrNull(values.get("domain"));
            this.direction = toStringOrDefault(values.get("direction"), "_1");
            this.recursionEnabled = toBooleanOrNull(values.get("recursionEnabled"));
            this.showOnlyOne = toBooleanOrNull(values.get("showOnlyOne"));
            this.nodes = fromJson(toJson(values.get("nodes")), collectionType(WsTreeNodeData.class));
            this.subclassViewMode = parseEnumOrNull(toStringOrNull(values.get("subclassViewMode")), NavTreeNodeSubclassViewMode.class);
            this.subclassViewShowIntermediateNodes = toBooleanOrNull(values.get("subclassViewShowIntermediateNodes"));
            this.subclassFilter = toListOfStrings(toStringOrNull(values.get("subclassFilter")));
            this.subclassDescriptions = map(unflattenMap(values, "subclass")).mapValues(CmStringUtils::toStringOrNull).mapKeys(k -> k.replaceFirst("_description$", "")).immutable();
        }

        public NavTreeNode toTreeNode() {
            return NavTreeNodeImpl.builder()
                    .withId(firstNotBlank(id, randomId()))
                    .withTargetFilter(filter)
                    .withTargetClassName(targetClass)
                    .withTargetClassDescription(description)
                    .withDomainName(domain)
                    .withDirection(parseDirection(direction) ? RD_DIRECT : RD_INVERSE)
                    .withEnableRecursion(recursionEnabled)
                    .withShowOnlyOne(showOnlyOne)
                    .withSubclassViewMode(subclassViewMode)
                    .withSubclassViewShowIntermediateNodes(subclassViewShowIntermediateNodes)
                    .withSubclassFilter(subclassFilter)
                    .withSubclassDescriptions(subclassDescriptions)
                    .withChildNodes(nodes.stream().map(WsTreeNodeData::toTreeNode).collect(toImmutableList()))
                    .build();
        }

    }

    private static boolean parseDirection(String direction) {
        return switch (nullToEmpty(direction)) {
            case "_1" ->
                true;
            case "_2" ->
                false;
            default ->
                throw new IllegalArgumentException("invalid direction = " + direction);
        };
    }

}
