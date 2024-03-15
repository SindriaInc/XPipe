
Ext.define('CMDBuildUI.view.boot.patches.Panel', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.boot.patches.PanelController',
        'CMDBuildUI.view.boot.patches.PanelModel'
    ],

    alias: 'widget.boot-patches-panel',
    controller: 'boot-patches-panel',
    viewModel: {
        type: 'boot-patches-panel'
    },

    forceFit: true,
    title: CMDBuildUI.locales.Locales.patches.patches,
    localized: {
        title: 'CMDBuildUI.locales.Locales.patches.patches'
    },
    sortableColumns: false,
    enableColumnHide: false,
    loadMask: true,

    columns: [{
        text: CMDBuildUI.locales.Locales.patches.name,
        localized: {
            text: 'CMDBuildUI.locales.Locales.patches.name'
        },
        dataIndex: 'name'
    }, {
        text: CMDBuildUI.locales.Locales.patches.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.patches.description'
        },
        dataIndex: 'description'
    }, {
        text: CMDBuildUI.locales.Locales.patches.category,
        localized: {
            text: 'CMDBuildUI.locales.Locales.patches.category'
        },
        dataIndex: 'category'
    }],

    bind: {
        store: '{patches}'
    },

    buttons: [{
        text: CMDBuildUI.locales.Locales.patches.apply,
        localized:{
            text: 'CMDBuildUI.locales.Locales.patches.apply'
        },
        itemId: 'btnApply',
        ui: 'management-action',
        autoEl: {
            'data-testid': 'patcches-btnapply'
        }
    }]
});
