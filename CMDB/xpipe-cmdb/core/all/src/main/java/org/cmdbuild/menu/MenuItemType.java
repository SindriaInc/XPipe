package org.cmdbuild.menu;

public enum MenuItemType {

    CLASS,
    DASHBOARD,
    PROCESS,
    FOLDER,
    SYSTEM_FOLDER,
    REPORT_CSV,
    REPORT_PDF,
    REPORT_ODT,
    REPORT_XML,
    VIEW,
    CUSTOM_PAGE,
    ROOT,
    NAVTREE,
    GEOATTRIBUTE;

    public boolean isReport() {
        return equals(REPORT_CSV) || equals(REPORT_ODT) || equals(REPORT_PDF) || equals(REPORT_XML);
    }

}
