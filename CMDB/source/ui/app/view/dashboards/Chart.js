Ext.define('CMDBuildUI.view.dashboards.Chart', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.dashboards.ChartController',
        'CMDBuildUI.view.dashboards.ChartModel'
    ],

    alias: 'widget.dashboards-chart',
    controller: 'dashboards-chart',
    viewModel: {
        type: 'dashboards-chart'
    },

    ui: 'managementdashboard',

    bind: {
        title: '{title}'
    },

    config: {
        /**
         * @cfg {Boolean} showInPopup
         */
        showInPopup: false
    },

    collapsible: true,
    margin: "15 15 0 15",

    tools: [{
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
        itemId: 'openInPopupBtn',
        tooltip: CMDBuildUI.locales.Locales.dashboards.tools.openinpopup,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.dashboards.tools.openinpopup'
        },
        bind: {
            hidden: '{openinpopupbtn.hidden}',
            disabled: '{toolsdisabled}',
            userCls: 'management-tool'
        }
    }, {
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('edit', 'regular'),
        cls: 'active',
        itemId: 'showHideParamsBtn',
        enableToggle: true,
        hidden: true,
        bind: {
            hidden: '{showhideparamsbtn.hidden}',
            disabled: '{toolsdisabled}',
            tooltip: '{paramsToolTip}',
            userCls: 'management-tool'
        }
    }, {
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
        itemId: 'showHideTableBtn',
        enableToggle: true,
        hidden: true,
        bind: {
            hidden: '{showhidetablebtn.hidden}',
            disabled: '{toolsdisabled}',
            tooltip: '{tableToolTip}',
            userCls: 'management-tool'
        }
    }, {
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
        itemId: 'refreshBtn',
        tooltip: CMDBuildUI.locales.Locales.dashboards.tools.reload,
        bind: {
            hidden: '{refreshbtn.hidden}',
            disabled: '{toolsdisabled}',
            userCls: 'management-tool'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.dashboards.tools.reload'
        }
    }, {
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
        itemId: 'downloadBtn',
        tooltip: CMDBuildUI.locales.Locales.dashboards.tools.download,
        bind: {
            hidden: '{downloadbtn.hidden}',
            disabled: '{toolsdisabled}',
            userCls: 'management-tool'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.dashboards.tools.download'
        }
    }]
});