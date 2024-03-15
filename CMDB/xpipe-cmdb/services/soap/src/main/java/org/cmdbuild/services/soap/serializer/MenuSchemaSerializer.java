package org.cmdbuild.services.soap.serializer;

import static com.google.common.base.Objects.equal;
import java.util.ArrayList;
import java.util.List;

import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.services.soap.structure.MenuSchema;

import com.google.common.collect.Iterables;
import static java.lang.Math.toIntExact;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.graph.ClasseHierarchyService;
import org.cmdbuild.menu.MenuService;
import org.cmdbuild.services.soap.operation.DataAccessLogicHelper.PrivilegeType;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class MenuSchemaSerializer {

    private static final String PROCESS_SUPERCLASS = "superprocessclass";
    private static final String SUPERCLASS = "superclass";
    private static final String PROCESS = "processclass";
    private static final String CLASS = "class";
    private final OperationUser operationUser; 
    private final WorkflowService workflowLogic;
    private final MenuService menuStore;
    private final ClasseHierarchyService classeHierarchyService;

    public MenuSchemaSerializer( //
            final MenuService menuStore, //
            final OperationUser operationUser, // 
            final WorkflowService workflowLogic,
            final ClasseHierarchyService classeHierarchyService
    ) {
        this.operationUser = operationUser; 
        this.workflowLogic = workflowLogic;
        this.menuStore = menuStore;
        this.classeHierarchyService = classeHierarchyService;
    }

    public MenuSchema serializeVisibleClassesFromRoot(final Classe root) {
        MenuSchema menuSchema = new MenuSchema();
        menuSchema.setId(toIntExact(root.getId()));
        menuSchema.setDescription(root.getDescription());
        menuSchema.setClassname(root.getName());
        // FIXME: add metadata serialization... wth are metadata for a class?
        setMenuTypeFromTypeAndChildren(menuSchema, false, root.isSuperclass());
        menuSchema.setPrivilege((String) map(PrivilegeType.WRITE, "w", PrivilegeType.READ, "r", PrivilegeType.NONE, "-").get(getPrivilegeFor(root)));
        menuSchema.setDefaultToDisplay(isStartingClass(root));

        List<MenuSchema> children = new ArrayList<>();
        //throw new UnsupportedOperationException("TODO");
        if (Iterables.size(classeHierarchyService.getClasseHierarchy(root).getChildren()) > 0) {
            for (final Classe childClass : classeHierarchyService.getClasseHierarchy(root).getChildren()) {
                children.add(serializeVisibleClassesFromRoot(childClass));
            }
        }
        //classeHierarchyService.getClasseHierarchy(root).getDescendants().forEach((c) -> children.add(serializeVisibleClassesFromRoot(c)));
        menuSchema.setChildren(children.toArray(new MenuSchema[children.size()]));
        return menuSchema;

    }

    private void setMenuTypeFromTypeAndChildren(final MenuSchema schema, final boolean isProcess,
            final boolean isSuperclass) {
        String type;
        if (isSuperclass) {
            type = isProcess ? PROCESS_SUPERCLASS : SUPERCLASS;
        } else {
            type = isProcess ? PROCESS : CLASS;
        }
        schema.setMenuType(type);
    }

    private PrivilegeType getPrivilegeFor(final Classe cmClass) {
        if (operationUser.hasWriteAccess(cmClass)) {
            return PrivilegeType.WRITE;
        } else if (operationUser.hasReadAccess(cmClass)) {
            return PrivilegeType.READ;
        } else {
            return PrivilegeType.NONE;
        }
    }

    private boolean isStartingClass(final Classe cmClass) {
        if (operationUser.getDefaultGroupOrNull().hasStartingClass()) {
            return equal(operationUser.getDefaultGroupOrNull().getStartingClass(), cmClass.getName());
        } else {
            return false;
        }
    }

    public MenuSchema serializeMenuTree() {
        throw new UnsupportedOperationException();
//		final MenuJsonNode rootMenuItem = menuStore.getMenuForCurrentUser();
//		return serializeMenuTree(rootMenuItem);
    }

//	private MenuSchema serializeMenuTree(final MenuJsonNode rootMenuItem) {
//		final MenuSchema menuSchema = new MenuSchema();
//		menuSchema.setDescription(rootMenuItem.getDescription());
//		if (isReport(rootMenuItem) || isView(rootMenuItem) || isDashboard(rootMenuItem) || isCustomPage(rootMenuItem)) {
//			menuSchema.setId(rootMenuItem.getReferencedElementId().intValue());
//		} else if (rootMenuItem.getId() != null) {
//			menuSchema.setId(rootMenuItem.getId().intValue());
//		}
//		menuSchema.setMenuType(rootMenuItem.getType().getValue().toLowerCase());
//
//		if (isClass(rootMenuItem) || (isProcess(rootMenuItem) && isProcessUsable(rootMenuItem.getReferencedClassName()))) {
//			final Classe menuEntryClass = dataAccessLogic.findClass(rootMenuItem.getReferencedClassName());
//			menuSchema.setId(menuEntryClass.getId().intValue());
//			menuSchema.setDefaultToDisplay(isStartingClass(menuEntryClass));
//			menuSchema.setClassname(menuEntryClass.getName());
//			final PrivilegeType privilege = getPrivilegeFor(menuEntryClass);
//			menuSchema.setPrivilege(privilege.toString());
//		}
//
//		final List<MenuSchema> children = new ArrayList<MenuSchema>();
//		MenuSchema childMenuSchema = new MenuSchema();
//		for (final MenuJsonNode childMenuItem : rootMenuItem.getChildren()) {
//			childMenuSchema = serializeMenuTree(childMenuItem);
//			children.add(childMenuSchema);
//		}
//
//		menuSchema.setChildren(children.toArray(new MenuSchema[children.size()]));
//		return menuSchema;
//	}
    private boolean isProcessUsable(final String processClassName) {
        return workflowLogic.isWorkflowEnabledAndProcessRunnable(processClassName);
    }

//	private boolean isClass(final MenuJsonNode menuItem) {
//		return menuItem.getType().getValue().equals(MenuItemType.CLASS.getValue());
//	}
//
//	private boolean isProcess(final MenuJsonNode menuItem) {
//		return menuItem.getType().getValue().equals(MenuItemType.PROCESS.getValue());
//	}
//
//	private boolean isDashboard(final MenuJsonNode menuItem) {
//		return menuItem.getType().getValue().equals(MenuItemType.DASHBOARD.getValue());
//	}
//
//	private boolean isView(final MenuJsonNode menuItem) {
//		return menuItem.getType().getValue().equals(MenuItemType.VIEW.getValue());
//	}
//
//	private boolean isCustomPage(final MenuJsonNode menuItem) {
//		return MenuItemType.isCustomPage(menuItem.getType());
//	}
//
//	private boolean isFolder(final MenuJsonNode menuItem) {
//		return menuItem.getType().getValue().equals(MenuItemType.SYSTEM_FOLDER.getValue())
//				|| menuItem.getType().getValue().equals(MenuItemType.FOLDER.getValue());
//	}
//
//	private boolean isReport(final MenuJsonNode menuItem) {
//		final MenuItemType menuItemType = menuItem.getType();
//		return (menuItemType.getValue().equals(MenuItemType.REPORT_CSV.getValue())
//				|| menuItemType.getValue().equals(MenuItemType.REPORT_PDF.getValue())
//				|| menuItemType.getValue().equals(MenuItemType.REPORT_ODT.getValue()) || menuItemType.getValue()
//				.equals(MenuItemType.REPORT_XML.getValue()));
//	}
}
