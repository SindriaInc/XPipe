Ext.define('CMDBuildUI.view.administration.content.bim.projects.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.bim.projects.GridController',
        'CMDBuildUI.view.administration.content.bim.projects.GridModel'
    ],

    alias: 'widget.administration-content-bim-projects-grid',
    controller: 'administration-content-bim-projects-grid',
    viewModel: {
        type: 'administration-content-bim-projects-grid'
    },
    itemId: 'bimProjectsGrid',

    forceFit: true,
    loadMask: true,
    bind: {
        store: '{projects}'
    },

    viewConfig: {
        markDirty: false
    },

    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',

        widget: {
            xtype: 'administration-content-bim-projects-card-viewinrow',
            autoHeight: true,
            ui: 'administration-tabandtools',
            bind: {},
            viewModel: {}
        }
    }],

    columns: [{
            text: CMDBuildUI.locales.Locales.administration.common.labels.name,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
            },
            dataIndex: 'name',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            dataIndex: 'description',
            align: 'left'
        }, {
            text: CMDBuildUI.locales.Locales.administration.bim.lastcheckin,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.bim.lastcheckin'
            },
            dataIndex: 'lastCheckin',
            align: 'left',
            renderer: function (value) {
                return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
            }

        }, {
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            disabled: true,
            xtype: 'checkcolumn',
            dataIndex: 'active'
        }
    ],

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.gis.layersorder);
        this.callParent(arguments);
    }
});