Ext.define('CMDBuildUI.util.administration.helper.LocalizationHelper', {
    singleton: true,


    getLocaleKeyOfClassDescription: function (theObjectTypeName) {
        return Ext.String.format('{0}.{1}.description', 'class', theObjectTypeName);
    },
    getLocaleKeyOfClassHelp: function (theObjectTypeName) {
        return Ext.String.format('class.{0}.help', theObjectTypeName);
    },
    getLocaleKeyOfDashboardDescription: function (dashboardCode) {
        return Ext.String.format('{0}.{1}.description', 'dashboard', dashboardCode);
    },
    getLocaleKeyOfDashboardChartDescription: function (dashboardCode, chartId) {
        return Ext.String.format('{0}.{1}.charts.{2}.description', 'dashboard', dashboardCode, chartId);
    },
    getLocaleKeyOfDashboardChartCategoryAxis: function (dashboardCode, chartId) {
        return Ext.String.format('{0}.{1}.charts.{2}.categoryAxisLabel', 'dashboard', dashboardCode, chartId);
    },
    getLocaleKeyOfDashboardChartLabelField: function (dashboardCode, chartId) {
        return Ext.String.format('{0}.{1}.charts.{2}.labelField', 'dashboard', dashboardCode, chartId);
    },
    getLocaleKeyOfDashboardChartValueAxis: function (dashboardCode, chartId) {
        return Ext.String.format('{0}.{1}.charts.{2}.valueAxisLabel', 'dashboard', dashboardCode, chartId);
    },
    getLocaleKeyOfDashboardChartParameterName: function (dashboardCode, chartId, index) {
        return Ext.String.format('{0}.{1}.charts.{2}.dsp.{3}.name', 'dashboard', dashboardCode, chartId, index);
    },

    getLocaleKeyOfClassAttributeDescription: function (theObjectTypeName, theAttributeName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'attributeclass', theObjectTypeName, theAttributeName);
    },
    getLocaleKeyOfGisAttributeClass: function (theObjectTypeName, theGisAttributeName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'gisattributeclass', theObjectTypeName, theGisAttributeName);
    },
    getLocaleKeyOfLayerMenuItemDescription: function (group, itemId) {
        return Ext.String.format('menuitem.{0}.{1}.description', group, itemId);
    },
    getLocaleKeyOfAttributeFkMasterDetail: function (theObjectTypeName, theAttributeName) {
        return Ext.String.format('{0}.{1}.{2}.masterdetaillabel', 'attributeclass', theObjectTypeName, theAttributeName);
    },
    getLocaleKeyOfClassGroupDescription: function (className, groupName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'attributegroupclass', className, groupName);
    },
    getLocaleKeyOfClassContextMenuItem: function (className, contextMenuName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'contextmenu', className, contextMenuName);
    },
    getLocaleKeyOfClassFormWidgetItem: function (className, formWidgetId) {
        return Ext.String.format('{0}.{1}.{2}.description', 'widget', className, formWidgetId);
    },
    getLocaleKeyOfProcessDescription: function (theProcessName) {
        return this.getLocaleKeyOfClassDescription(theProcessName);
    },
    getLocaleKeyOfProcessHelp: function (theProcessName) {
        return this.getLocaleKeyOfClassHelp(theProcessName);
    },
    getLocalKeyOfProcessActivityDescription: function (theProcessName, theActivityName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'activity', theProcessName, theActivityName);
    },

    getLocalkeyOfProcessWidgetDescription: function (theProcessName, theActivityName, theWidgetName) {
        return Ext.String.format('{0}.{1}.{2}.{3}.description', 'widget', theProcessName, theActivityName, theWidgetName);
    },

    getLocaleKeyOfProcessAttributeDescription: function (theObjectTypeName, theAttributeName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'class', theObjectTypeName, theAttributeName);
    },

    getLocaleKeyOfDomainDescription: function (domainName) {
        return Ext.String.format('{0}.{1}.description', 'domain', domainName);
    },

    getLocaleKeyOfDomainDirectDescription: function (domainName) {
        return Ext.String.format('{0}.{1}.directdescription', 'domain', domainName);
    },

    getLocaleKeyOfDomainInverseDescription: function (domainName) {
        return Ext.String.format('{0}.{1}.inversedescription', 'domain', domainName);
    },

    getLocaleKeyOfDomainMasterDetail: function (domainName) {
        return Ext.String.format('{0}.{1}.masterdetaillabel', 'domain', domainName);
    },

    getLocaleKeyOfDomainAttributeDescription: function (domainName, attributeName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'attributedomain', domainName, attributeName);
    },

    getLocaleKeyOfViewDescription: function (viewName) {
        return Ext.String.format('{0}.{1}.description', 'view', viewName);
    },

    getLocaleKeyOfSearchFiltreDescription: function (className, filterName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'filter', className, filterName);
    },

    getLocaleKeyOfLookupValueDescription: function (lookupTypeName, lookupValueCode) {
        return Ext.String.format('{0}.{1}.{2}.description', 'lookup', lookupTypeName, lookupValueCode);
    },

    getLocaleKeyOfReportDescription: function (reportName) {
        return Ext.String.format('{0}.{1}.description', 'report', reportName);
    },
    getLocaleKeyOfReportAttributeDescription: function (reportName, attributeName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'class', reportName, attributeName);
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
    }


});