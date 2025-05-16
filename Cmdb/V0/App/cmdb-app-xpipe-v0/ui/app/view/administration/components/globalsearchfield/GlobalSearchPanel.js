Ext.define('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchPanel', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchPanelController',
        'CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchPanelModel'
    ],
    alias: 'widget.admin-globalsearchpanel',
    controller: 'administration-components-globalsearchfield-globalsearchpanel',

    viewConfig: {
        getRowClass: function (record) {
            if (record.get("depth") > 1) {
                return "small-text";
            }
            return "";
        }
    },
    viewModel: {
        type: 'administration-components-globalsearchfield-globalsearchpanel'
    },
    floating: true,
    width: 300,
    maxHeight: '400',
    itemId: 'resulttreepanel',
    cls: 'treepanel-noexpander',

    rootVisible: false,
    emptyText: CMDBuildUI.locales.Locales.administration.globalsearch.emptyText.noresults,
    localized: {
        emptyText: 'CMDBuildUI.locales.Locales.administration.globalsearch.emptyText.noresults'
    },
    config: {
        responseData: null,
        objectType: null,
        subType: null
    },
    bind: {
        store: '{searchPanelStore}'
    },
    bbar: ['->', {
        xtype: 'button',
        ui: 'administration-secondary-action-small',
        text: CMDBuildUI.locales.Locales.administration.globalsearch.emptyText.close,        
        handler: function (button) {            
            this.up('#resulttreepanel').destroy();
        }
    }]    

});