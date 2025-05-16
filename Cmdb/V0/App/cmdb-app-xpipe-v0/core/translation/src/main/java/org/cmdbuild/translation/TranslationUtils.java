/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import static java.lang.String.format;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeGroupInfo;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.gis.GisAttribute;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class TranslationUtils {

    public static String classDescriptionTranslationCode(Classe classe) {
        return format("class.%s.description", classe.getName());
    }

    public static String classDescriptionPluralTranslationCode(Classe classe) {
        return format("class.%s.descriptionplural", classe.getName());
    }

    public static String classHelpTranslationCode(Classe classe) {
        return format("class.%s.help", classe.getName());
    }

    public static String attributeDescriptionTranslationCode(Attribute attribute) {
        return format("attribute%s.%s.%s.description", attribute.getOwner().isView() ? "view" : serializeEnum(attribute.getOwner().getEtType()), attribute.getOwner().getName(), attribute.getName());
    }

    public static String gisAttributeDescriptionTranslationCode(GisAttribute attribute) {
        return format("gisattributeclass.%s.%s.description", attribute.getOwnerClassName(), attribute.getLayerName());
    }

    public static String attributeFkMasterDetailDescriptionTranslationCode(Attribute attribute) {
        return format("attribute%s.%s.%s.masterdetaillabel", serializeEnum(attribute.getOwner().getEtType()), attribute.getOwner().getName(), attribute.getName());
    }

    public static String attributeGroupDescriptionTranslationCode(EntryType owner, AttributeGroupInfo attributeGroup) {
        return format("attributegroup%s.%s.%s.description", serializeEnum(owner.getEtType()), owner.getName(), attributeGroup.getName());
    }

    public static String viewAttributeDescriptionTranslationCode(String owner, String name) {
        return format("attributeview.%s.%s.description", owner, name);
    }

    public static String viewAttributeGroupDescriptionTranslationCode(String owner, String name) {
        return format("attributegroupview.%s.%s.description", owner, name);
    }

    public static String domainDirectDescriptionTranslationCode(String domain) {
        return format("domain.%s.directdescription", checkNotBlank(domain));
    }

    public static String domainInverseDescriptionTranslationCode(String domain) {
        return format("domain.%s.inversedescription", checkNotBlank(domain));
    }

    public static String domainMasterDetailDescriptionTranslationCode(String domain) {
        return format("domain.%s.masterdetaillabel", checkNotBlank(domain));
    }

    public static String filterDescriptionTranslationCode(String className, String filterCode) {
        return format("filter.%s.%s.description", checkNotBlank(className), checkNotBlank(filterCode));
    }

    public static String functionDescriptionTranslationCode(String functionName, String attributeName) {
        return format("function.%s.%s.description", checkNotBlank(functionName), checkNotBlank(attributeName));
    }

    public static String lookupDescriptionTranslationCode(String lookupType, String lookupCode) {
        return format("lookup.%s.%s.description", checkNotBlank(lookupType, "lookup type cannot be null"), checkNotBlank(lookupCode, "lookup code cannot be null"));
    }

    public static String classWidgetDescriptionTranslationCode(String owner, String widgetId) {
        return format("widget.%s.%s.description", checkNotBlank(owner), checkNotBlank(widgetId));
    }

    public static String workflowWidgetDescriptionTranslationCode(String process, String taskId, String widgetId) {
        return format("widget.%s.%s.%s.description", checkNotBlank(process), checkNotBlank(taskId), checkNotBlank(widgetId));
    }

    public static String taskDescriptionTranslationCode(String process, String taskId) {
        return format("activity.%s.%s.description", checkNotBlank(process), checkNotBlank(taskId));
    }

    public static String taskInstructionsTranslationCode(String process, String taskId) {
        return format("activity.%s.%s.instructions", checkNotBlank(process), checkNotBlank(taskId));
    }

    public static String roleDescriptionTranslationCode(String roleCode) {
        return format("role.%s.description", checkNotBlank(roleCode));
    }

    public static String menuitemDescriptionTranslationCode(String code, String menuCode) {
        return format("menuitem.%s.%s.description", checkNotBlank(menuCode), checkNotBlank(code));
    }

    public static String navtreeDescriptionTranslationCode(String code) {
        return format("navtree.%s.description", checkNotBlank(code));
    }

    public static String navtreeItemDescriptionTranslationCode(String code, String item) {
        return format("navtree.%s.item.%s.description", checkNotBlank(code), checkNotBlank(item));
    }

    public static String navtreeItemSubclassDescriptionTranslationCode(String code, String item, String subclass) {
        return format("navtree.%s.item.%s.subclass.%s.description", checkNotBlank(code), checkNotBlank(item), checkNotBlank(subclass));
    }

    public static String viewDescriptionTranslationCode(String viewCode) {
        return format("view.%s.description", checkNotBlank(viewCode));
    }

    public static String viewDescriptionPluralTranslationCode(String viewCode) {
        return format("view.%s.descriptionplural", checkNotBlank(viewCode));
    }

    public static String reportDescriptionTranslationCode(String reportCode) {
        return format("report.%s.description", checkNotBlank(reportCode));
    }

    public static String reportAttributeDescriptionTranslationCode(String reportCode, String attrCode) {
        return format("report.%s.attribute.%s.description", checkNotBlank(reportCode), checkNotBlank(attrCode));
    }

    public static String customPageDescriptionTranslationCode(String customPageCode) {
        return format("custompage.%s.description", checkNotBlank(customPageCode));
    }

    public static String dashboardDescriptionTranslationCode(String dashboardCode) {
        return format("dashboard.%s.description", checkNotBlank(dashboardCode));
    }

    public static String dashboardChartDescriptionTranslationCode(String dashboardCode, String chartId) {
        return format("dashboard.%s.charts.%s.description", checkNotBlank(dashboardCode), checkNotBlank(chartId));
    }

    public static String dashboardChartValueAxisLabelTranslationCode(String dashboardCode, String chartId) {
        return format("dashboard.%s.charts.%s.valueAxisLabel", checkNotBlank(dashboardCode), checkNotBlank(chartId));
    }

    public static String dashboardChartCategoryAxisLabelTranslationCode(String dashboardCode, String chartId) {
        return format("dashboard.%s.charts.%s.categoryAxisLabel", checkNotBlank(dashboardCode), checkNotBlank(chartId));
    }

    public static String dashboardChartCategoryLabelFieldTranslationCode(String dashboardCode, String chartId) {
        return format("dashboard.%s.charts.%s.labelField", checkNotBlank(dashboardCode), checkNotBlank(chartId));
    }

    public static String dashboardChartDataSourceParameterTranslationCode(String dashboardCode, String chartId, String index) {
        return format("dashboard.%s.charts.%s.dsp.%s.name", checkNotBlank(dashboardCode), checkNotBlank(chartId), checkNotBlank(index));//TODO improve this, rename `name` -> `description`        
    }

    public static String contextMenuDescriptionTranslationCode(String contextMenuCode) {
        return format("contextmenu.%s.description", checkNotBlank(contextMenuCode));
    }

    public static String calendarTriggerDescriptionTranslationCode(String triggerCode) {
        return format("schedule.%s.description", checkNotBlank(triggerCode));
    }

    public static String calendarTriggerContentTranslationCode(String triggerCode) {
        return format("schedule.%s.content", checkNotBlank(triggerCode));
    }

    public static String contextMenuLabelTranslationCode(String ownerName, String contextMenuLabel) {
        return format("contextmenu.%s.%s.description", checkNotBlank(ownerName), checkNotBlank(contextMenuLabel));
    }

    public static String emailSignatureDescriptionTranslationCode(String code) {
        return format("emailsignature.%s.description", checkNotBlank(code));
    }

    public static String emailSignatureContentHtmlTranslationCode(String code) {
        return format("emailsignature.%s.content_html", checkNotBlank(code));
    }

    public static String notificationTemplateDescriptionTranslationCode(String code) {
        return format("notification.%s.description", checkNotBlank(code));
    }

    public static String notificationTemplateSubjectTranslationCode(String code) {
        return format("notification.%s.subject", checkNotBlank(code));
    }

    public static String notificationTemplateBodyTranslationCode(String code) {
        return format("notification.%s.body", checkNotBlank(code));
    }

}
