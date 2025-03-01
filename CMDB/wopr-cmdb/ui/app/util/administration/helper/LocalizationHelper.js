Ext.define('CMDBuildUI.util.administration.helper.LocalizationHelper', {
    singleton: true,


    getLocaleKeyOfClassDescription: function (theObjectTypeName) {
        return Ext.String.format('class.{0}.description', theObjectTypeName);
    },
    getLocaleKeyOfClassHelp: function (theObjectTypeName) {
        return Ext.String.format('class.{0}.help', theObjectTypeName);
    },
    getLocaleKeyOfDashboardDescription: function (dashboardCode) {
        return Ext.String.format('dashboard.{0}.description', dashboardCode);
    },
    getLocaleKeyOfDashboardChartDescription: function (dashboardCode, chartId) {
        return Ext.String.format('dashboard.{0}.charts.{1}.description', dashboardCode, chartId);
    },
    getLocaleKeyOfDashboardChartCategoryAxis: function (dashboardCode, chartId) {
        return Ext.String.format('dashboard.{0}.charts.{1}.categoryAxisLabel', dashboardCode, chartId);
    },
    getLocaleKeyOfDashboardChartLabelField: function (dashboardCode, chartId) {
        return Ext.String.format('dashboard.{0}.charts.{1}.labelField', dashboardCode, chartId);
    },
    getLocaleKeyOfDashboardChartValueAxis: function (dashboardCode, chartId) {
        return Ext.String.format('dashboard.{0}.charts.{1}.valueAxisLabel', dashboardCode, chartId);
    },
    getLocaleKeyOfDashboardChartParameterName: function (dashboardCode, chartId, index) {
        return Ext.String.format('dashboard.{0}.charts.{1}.dsp.{2}.name', dashboardCode, chartId, index);
    },

    getLocaleKeyOfClassAttributeDescription: function (theObjectTypeName, theAttributeName) {
        return Ext.String.format('attributeclass.{0}.{1}.description', theObjectTypeName, theAttributeName);
    },
    getLocaleKeyOfGisAttributeClass: function (theObjectTypeName, theGisAttributeName) {
        return Ext.String.format('gisattributeclass.{0}.{1}.description', theObjectTypeName, theGisAttributeName);
    },
    getLocaleKeyOfLayerMenuItemDescription: function (group, itemId) {
        return Ext.String.format('menuitem.{0}.{1}.description', group, itemId);
    },
    getLocaleKeyOfAttributeFkMasterDetail: function (theObjectTypeName, theAttributeName) {
        return Ext.String.format('attributeclass.{0}.{1}.masterdetaillabel', theObjectTypeName, theAttributeName);
    },
    getLocaleKeyOfClassGroupDescription: function (className, groupName) {
        return Ext.String.format('attributegroupclass.{0}.{1}.description', className, groupName);
    },
    getLocaleKeyOfClassContextMenuItem: function (className, contextMenuName) {
        return Ext.String.format('contextmenu.{0}.{1}.description', className, contextMenuName);
    },
    getLocaleKeyOfClassFormWidgetItem: function (className, formWidgetId) {
        return Ext.String.format('widget.{0}.{1}.description', className, formWidgetId);
    },
    getLocaleKeyOfProcessDescription: function (theProcessName) {
        return this.getLocaleKeyOfClassDescription(theProcessName);
    },
    getLocaleKeyOfProcessHelp: function (theProcessName) {
        return this.getLocaleKeyOfClassHelp(theProcessName);
    },
    getLocalKeyOfProcessActivityDescription: function (theProcessName, theActivityName) {
        return Ext.String.format('activity.{0}.{1}.description', theProcessName, theActivityName);
    },

    getLocalkeyOfProcessWidgetDescription: function (theProcessName, theActivityName, theWidgetName) {
        return Ext.String.format('widget.{0}.{1}.{2}.description', theProcessName, theActivityName, theWidgetName);
    },

    getLocaleKeyOfProcessAttributeDescription: function (theObjectTypeName, theAttributeName) {
        return Ext.String.format('class.{0}.{1}.description', theObjectTypeName, theAttributeName);
    },

    getLocaleKeyOfDomainDescription: function (domainName) {
        return Ext.String.format('domain.{0}.description', domainName);
    },

    getLocaleKeyOfDomainDirectDescription: function (domainName) {
        return Ext.String.format('domain.{0}.directdescription', domainName);
    },

    getLocaleKeyOfDomainInverseDescription: function (domainName) {
        return Ext.String.format('domain.{0}.inversedescription', domainName);
    },

    getLocaleKeyOfDomainMasterDetail: function (domainName) {
        return Ext.String.format('domain.{0}.masterdetaillabel', domainName);
    },

    getLocaleKeyOfDomainAttributeDescription: function (domainName, attributeName) {
        return Ext.String.format('attributedomain.{0}.{1}.description', domainName, attributeName);
    },

    getLocaleKeyOfViewDescription: function (viewName) {
        return Ext.String.format('view.{0}.description', viewName);
    },

    getLocaleKeyOfSearchFiltreDescription: function (className, filterName) {
        return Ext.String.format('filter.{0}.{1}.description', className, filterName);
    },

    getLocaleKeyOfLookupValueDescription: function (lookupTypeName, lookupValueCode) {
        return Ext.String.format('lookup.{0}.{1}.description', lookupTypeName, lookupValueCode);
    },

    getLocaleKeyOfReportDescription: function (reportName) {
        return Ext.String.format('report.{0}.description', reportName);
    },
    getLocaleKeyOfReportAttributeDescription: function (reportName, attributeName) {
        return Ext.String.format('class.{0}.{1}.description', reportName, attributeName);
    },

    getLocaleKeyOfMenuItemDescription: function (group, device, itemId) {
        return Ext.String.format('menuitem.{0}_{1}.{2}.description', group, device, itemId);
    },

    getLocaleKeyOfCustomPageDescription: function (custompageName) {
        return Ext.String.format('custompage.{0}.description', custompageName);
    },

    getLocaleKeyOfCustomComponentDescription: function (itemId) {
        return Ext.String.format('customcomponent.{0}.description', itemId);
    },

    getLocaleKeyOfScheduleDescription: function (scheduleCode) {
        return Ext.String.format('schedule.{0}.description', scheduleCode);
    },

    getLocaleKeyOfScheduleExtDescription: function (scheduleCode) {
        return Ext.String.format('schedule.{0}.content', scheduleCode);
    },

    getLocaleKeyOfNavigationTreeDescription: function (navigationTreeName) {
        return Ext.String.format('navtree.{0}.description', navigationTreeName);
    },
    getLocaleKeyOfGroupDescription: function (groupId) {
        return Ext.String.format('role.{0}.description', groupId);
    },
    getLocaleKeyOfViewGroupingsDescription: function (viewName, groupingName) {
        return Ext.String.format('attributegroupview.{0}.{1}.description', viewName, groupingName);
    },
    getLocaleKeyOfViewAttributeDescription: function (viewName, attributeAlias) {
        return Ext.String.format('attributeview.{0}.{1}.description', viewName, attributeAlias);
    },
    getLocaleKeyOfSignatureDescription: function (signatureCode) {
        return Ext.String.format('emailsignature.{0}.description', signatureCode);
    },
    getLocaleKeyOfSignatureContent: function (signatureCode) {
        return Ext.String.format('emailsignature.{0}.content_html', signatureCode);
    },
    getLocaleKeyOfNotificationDescription: function (templateCode) {
        return Ext.String.format('notification.{0}.description', templateCode);
    },
    getLocaleKeyOfNotificationSubject: function (templateCode) {
        return Ext.String.format('notification.{0}.subject', templateCode);
    },
    getLocaleKeyOfNotificationBody: function (templateCode) {
        return Ext.String.format('notification.{0}.body', templateCode);
    },
    getLocaleKeyOfDatasetDescription: function (datasetName) {
        return Ext.String.format('offline.{0}.description', datasetName);
    }
});