/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.serializationhelpers;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.emptyToNull;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.menu.Menu;
import org.cmdbuild.menu.MenuInfo;
import org.cmdbuild.menu.MenuItemType;
import static org.cmdbuild.menu.MenuItemType.CLASS;
import static org.cmdbuild.menu.MenuItemType.CUSTOM_PAGE;
import static org.cmdbuild.menu.MenuItemType.DASHBOARD;
import static org.cmdbuild.menu.MenuItemType.FOLDER;
import static org.cmdbuild.menu.MenuItemType.GEOATTRIBUTE;
import static org.cmdbuild.menu.MenuItemType.NAVTREE;
import static org.cmdbuild.menu.MenuItemType.PROCESS;
import static org.cmdbuild.menu.MenuItemType.REPORT_CSV;
import static org.cmdbuild.menu.MenuItemType.REPORT_ODT;
import static org.cmdbuild.menu.MenuItemType.REPORT_PDF;
import static org.cmdbuild.menu.MenuItemType.REPORT_XML;
import static org.cmdbuild.menu.MenuItemType.ROOT;
import static org.cmdbuild.menu.MenuItemType.SYSTEM_FOLDER;
import static org.cmdbuild.menu.MenuItemType.VIEW;
import org.cmdbuild.menu.MenuTreeNode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.cmdbuild.translation.TranslationService;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MenuSerializationHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public final static BiMap<MenuItemType, String> MENU_ITEM_TYPE_WS_MAP = HashBiMap.create(map(
            CLASS, "class",
            GEOATTRIBUTE, "geoattribute",
            DASHBOARD, "dashboard",
            PROCESS, "processclass",
            FOLDER, "folder",
            SYSTEM_FOLDER, "system_folder",
            REPORT_CSV, "reportcsv",
            REPORT_PDF, "reportpdf",
            REPORT_ODT, "reportodt",
            REPORT_XML, "reportxml",
            VIEW, "view",
            CUSTOM_PAGE, "custompage",
            ROOT, "root",
            NAVTREE, serializeEnum(NAVTREE)));

    private final TranslationService translationService;
    private final DaoService dao;
    private final GisService gisService;

    public MenuSerializationHelper(TranslationService translationService, DaoService dao, GisService gisService) {
        this.translationService = checkNotNull(translationService);
        this.dao = checkNotNull(dao);
        this.gisService = checkNotNull(gisService);
    }

    public FluentMap serializeMenu(Menu item) {
        return new MenuSerializer(item, false).serializeMenuTree();
    }

    public Object serializeFlatUserMenu(Menu item) {
        return new MenuSerializer(item, true).serializeMenuFlat();
    }

    public FluentMap serializeUserMenu(Menu item) {
        return new MenuSerializer(item, true).serializeMenuTree();
    }

    public Object menuResponse(Menu menu) {
        return response(serializeDetailedMenu(menu));
    }

    public Map serializeBasicMenu(MenuInfo menu) {
        return map("_id", menu.getId(), "group", menu.getGroup(), "device", serializeEnum(menu.getTargetDevice()), "type", serializeEnum(menu.getType()));
    }

    public Object serializeDetailedMenu(Menu menu) {
        return map(serializeMenu(menu)).with(serializeBasicMenu(menu));
    }

    private class MenuSerializer {

        private final Menu menu;
        private final boolean forUser;

        public MenuSerializer(Menu menu, boolean forUser) {
            this.menu = checkNotNull(menu);
            this.forUser = forUser;
        }

        public FluentMap serializeMenuTree() {
            return serializeMenuItemAndChilds(menu.getRootNode());
        }

        public Object serializeMenuFlat() {
            return menu.getRootNode().getDescendantsAndSelf().stream().map(this::serializeMenuItem).collect(toList());
        }

        private FluentMap serializeMenuItem(MenuTreeNode item) {
            return map(
                    "_id", item.getCode(),
                    "menuType", checkNotNull(MENU_ITEM_TYPE_WS_MAP.get(item.getType())),
                    "objectDescription", item.getDescription()
            ).accept(m -> {
                if (forUser) {
                    m.put(
                            "objectDescription", item.getActualDescription(),
                            "_objectDescription_translation", item.hasOwnDescription() ? translationService.translateMenuitemDescription(item.getCode(), menu.getCode(), item.getDescription()) : getTargetDescriptionTranslation(item.getType(), item.getTarget(), item.getTargetDescription())
                    );
                } else {
                    m.put(
                            "objectDescription", item.getDescription(),
                            "_objectDescription_translation", translationService.translateMenuitemDescription(item.getCode(), menu.getCode(), item.getDescription()),
                            "_actualDescription", item.getActualDescription(),
                            "_actualDescription_translation", item.hasOwnDescription() ? translationService.translateMenuitemDescription(item.getCode(), menu.getCode(), item.getDescription()) : getTargetDescriptionTranslation(item.getType(), item.getTarget(), item.getTargetDescription()),
                            "_targetDescription", item.getTargetDescription(),
                            "_targetDescription_translation", getTargetDescriptionTranslation(item.getType(), item.getTarget(), item.getTargetDescription())
                    );
                }
            }).skipNullValues().with("objectTypeName", emptyToNull(item.getTarget())).then();
        }

        private FluentMap serializeMenuItemAndChilds(MenuTreeNode item) {
            AtomicInteger index = new AtomicInteger(-1);
            return serializeMenuItem(item).with("children", item.getChildren().stream().map((i) -> serializeMenuItemAndChilds(i).with("index", index.incrementAndGet())).collect(toList()));
        }

        @Nullable
        private String getTargetDescriptionTranslation(MenuItemType type, String code, @Nullable String description) {
            return switch (type) {
                case CLASS, PROCESS ->
                    translationService.translateClassDescription(dao.getClasse(code));
                case CUSTOM_PAGE ->
                    translationService.translateCustomPageDesciption(code, description);
                case DASHBOARD ->
                    translationService.translateDashboardDescription(code, description);
                case NAVTREE ->
                    translationService.translateNavtreeDescription(code, description);
                case REPORT_CSV, REPORT_ODT, REPORT_PDF, REPORT_XML ->
                    translationService.translateReportDesciption(code, description);
                case VIEW ->
                    translationService.translateViewDesciption(code, description);
                case GEOATTRIBUTE ->
                    translationService.translateGisAttributeDescription(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(Splitter.on(".").splitToList(code).get(0), Splitter.on(".").splitToList(code).get(1)));
                default ->
                    description;
            };
        }
    }
}
