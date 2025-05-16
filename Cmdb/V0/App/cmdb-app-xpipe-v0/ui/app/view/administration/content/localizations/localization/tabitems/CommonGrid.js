Ext.define('CMDBuildUI.view.administration.content.localizations.localization.tabitems.CommonGrid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.localizations.localization.tabitems.CommonGridController',
        'CMDBuildUI.view.administration.content.localizations.localization.tabitems.CommonGridModel'
    ],
    alias: 'widget.administration-content-localizations-localization-tabitems-commongrid',
    controller: 'administration-content-localizations-localization-tabitems-commongrid',
    viewModel: {
        type: 'administration-content-localizations-localization-tabitems-commongrid'
    },

    forceFit: false,
    scrollable: true,

    plugins: [{
        pluginId: 'cellediting',
        ptype: 'cellediting',
        clicksToEdit: 1,
        listeners: {
            edit: 'editedCell'
        }
    }],
    viewConfig: {
        preserveScrollOnReload: true
    },
    bind: {
        store: '{localizationsStore}'
    },
    config: {
        section: null
    },

    columns: [],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
                xtype: 'component',
                flex: 1
            },
            {
                text: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.saveBtn,
                ui: 'administration-action-small',
                listeners: {
                    click: 'onSaveBtnClick'
                }
            },
            {
                text: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.cancelBtn,
                ui: 'administration-secondary-action-small',
                listeners: {
                    click: 'onCancelBtnClick'
                }
            }
        ]
    }]
});