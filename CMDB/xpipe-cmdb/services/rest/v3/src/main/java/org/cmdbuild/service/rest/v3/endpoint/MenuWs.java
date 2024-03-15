package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.util.Collections.emptyList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
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
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.menu.Menu;
import org.cmdbuild.menu.MenuItemType;
import org.cmdbuild.menu.MenuService;
import org.cmdbuild.menu.MenuTreeNode;
import org.cmdbuild.menu.MenuTreeNodeImpl;
import org.cmdbuild.service.rest.v3.serializationhelpers.MenuSerializationHelper;
import static org.cmdbuild.service.rest.v3.serializationhelpers.MenuSerializationHelper.MENU_ITEM_TYPE_WS_MAP;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import javax.annotation.security.RolesAllowed;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_MENUS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_MENUS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.menu.MenuInfo;
import org.cmdbuild.menu.MenuType;
import static org.cmdbuild.menu.MenuType.MT_NAVMENU;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE;
import org.cmdbuild.ui.TargetDevice;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

@Path("menu/")
@Produces(APPLICATION_JSON)
public class MenuWs {

    private final MenuService menuService;
    private final MenuSerializationHelper helper;

    public MenuWs(MenuService menuService, MenuSerializationHelper helper) {
        this.menuService = checkNotNull(menuService);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path("")
    @RolesAllowed(ADMIN_MENUS_VIEW_AUTHORITY)
    public Object readAll(@QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed, @QueryParam(TYPE) @Nullable String type) {
        List<MenuInfo> allMenuInfos = menuService.getAllMenuInfos();
        if (isNotBlank(type)) {
            MenuType menuType = parseEnumOrNull(type, MenuType.class);
            allMenuInfos = allMenuInfos.stream().filter(m -> m.getType().equals(menuType)).collect(toList());
        }
        return response(allMenuInfos.stream().map(detailed ? (m) -> helper.serializeDetailedMenu(menuService.getMenuById(m.getId())) : (m) -> helper.serializeBasicMenu(m)).collect(toList()));
    }

    @GET
    @Path("/{menuId}")
    @RolesAllowed(ADMIN_MENUS_VIEW_AUTHORITY)
    public Object read(@PathParam("menuId") Long menuId) {
        Menu menu = menuService.getMenuById(menuId);
        return helper.menuResponse(menu);
    }

    @GET
    @Path("/gismenu")
    public Object getGeoMenu() {
        Menu gisMenu = menuService.getGisMenu();
        if (gisMenu != null) {
            return helper.menuResponse(gisMenu);
        } else {
            return success();
        }
    }

    @POST
    @Path("")
    @RolesAllowed(ADMIN_MENUS_MODIFY_AUTHORITY)
    public Object create(MenuRootNodeWsBean data, @QueryParam("regenerateNodeCodes") @DefaultValue(TRUE) Boolean regenerateNodeCodes) {
        Menu menu = menuService.create(data.groupName, toMenuTreeNode(data, regenerateNodeCodes), data.targetDevice, data.type);
        return helper.menuResponse(menu);
    }

    @PUT
    @Path("/{menuId}")
    @RolesAllowed(ADMIN_MENUS_MODIFY_AUTHORITY)
    public Object update(@PathParam("menuId") Long menuId, MenuRootNodeWsBean data) {
        Menu menu = menuService.update(menuId, toMenuTreeNode(data, false), data.targetDevice);
        return helper.menuResponse(menu);
    }

    @DELETE
    @Path("/{menuId}")
    @RolesAllowed(ADMIN_MENUS_MODIFY_AUTHORITY)
    public Object delete(@PathParam("menuId") Long menuId) {
        menuService.delete(menuId);
        return success();
    }

    private MenuTreeNode toMenuTreeNode(MenuRootNodeWsBean data, boolean regenerateNodeCodes) {
        return MenuTreeNodeImpl.buildRoot(data.children.stream().map((n) -> toMenuTreeNode(n, regenerateNodeCodes)).collect(toList()));
    }

    private MenuTreeNode toMenuTreeNode(MenuNodeWsBean data, boolean regenerateNodeCodes) {
        return MenuTreeNodeImpl.builder()
                .withCode(regenerateNodeCodes ? randomId() : data.code)
                .withDescription(data.objectDescription)
                .withTarget(data.target)
                .withType(data.menuType)
                .withChildren(data.children.stream().map((n) -> toMenuTreeNode(n, regenerateNodeCodes)).collect(toList()))
                .build();
    }

    public static class MenuRootNodeWsBean {

        public final String groupName;
        public final List<MenuNodeWsBean> children;
        public final TargetDevice targetDevice;
        public final MenuType type;

        public MenuRootNodeWsBean(
                @JsonProperty("device") String targetDevice,
                @JsonProperty("group") String groupName,
                @JsonProperty("type") String menuType,
                @JsonProperty("children") List<MenuNodeWsBean> children) {
            this.groupName = checkNotBlank(groupName);
            this.children = firstNonNull(children, emptyList());
            this.type = parseEnumOrDefault(menuType, MT_NAVMENU);
            this.targetDevice = parseEnumOrDefault(targetDevice, TD_DEFAULT);
        }

    }

    public static class MenuNodeWsBean {

        public final MenuItemType menuType;
        public final String target, objectDescription, code;
        public final List<MenuNodeWsBean> children;

        public MenuNodeWsBean(
                @JsonProperty("menuType") String menuType,
                @JsonProperty("objectTypeName") String target,
                @JsonProperty("_id") String code,
                @JsonProperty("objectDescription") String objectDescription,
                @JsonProperty("children") List<MenuNodeWsBean> children) {
            this.menuType = checkNotNull(MENU_ITEM_TYPE_WS_MAP.inverse().get(checkNotBlank(menuType)), "unknown menu type = '%s'", menuType);
            this.target = target;
            this.objectDescription = objectDescription;
            this.code = firstNonNull(emptyToNull(code), randomId());
            this.children = firstNonNull(children, emptyList());
        }

    }

}
