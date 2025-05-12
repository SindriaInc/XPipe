
(function () {
    var elementId = 'CMDBuildAdministrationContentDashboardsView';
    Ext.define('CMDBuildUI.view.administration.content.dashboards.PropertiesTab', {
        extend: 'Ext.panel.Panel',

        alias: 'widget.administration-content-dashboards-propertiestab',

        requires: [
            'CMDBuildUI.view.administration.content.dashboards.PropertiesTabController',
            'CMDBuildUI.view.administration.content.dashboards.PropertiesTabModel'
        ],

        controller: 'administration-content-dashboards-propertiestab',
        viewModel: {
            type: 'administration-content-dashboards-propertiestab'
        },
        id: elementId,
        statics: {
            elementId: elementId
        },
        loadMask: true,
        defaults: {
            textAlign: 'left',
            scrollable: true
        },
        config: {
            showCard: true
        },
        layout: 'card',
        items: []
    });
})();