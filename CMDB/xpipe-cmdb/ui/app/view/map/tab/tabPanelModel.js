Ext.define('CMDBuildUI.view.map.tab.tabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-tab-tabpanel',
    data: {
        activeItemCalculation: undefined
    },

    formulas: {
        'activeItemCalculation': {
            bind: {
                navigationtree: '{map-tab-tabpanel.navigationTree}'
            },
            get: function (data) {
                if (data.navigationtree) {
                    return CMDBuildUI.view.map.tab.tabPanel.tabIndex['map-tab-cards-navigationtree'];
                }
                return CMDBuildUI.view.map.tab.tabPanel.tabIndex['map-tab-cards-list'];
            }
        },

        //navigation tree
        'disableMap-map-tab-cards-navigationtree': {
            bind: {
                drawmode: '{map-tab-tabpanel.drawmode}'
            },
            get: function (data) {
                if (Ext.isBoolean(data.drawmode)) {
                    return data.drawmode;
                }
            }
        },
        'hiddenMap-tab-cards-navigationtree': {
            bind: {
                navigationtree: '{map-tab-tabpanel.navigationTree}'
            },
            get: function (data) {
                return !data.navigationtree;
            }
        },

        //list
        'disableMap-map-tab-cards-list': {
            bind: {
                drawmode: '{map-tab-tabpanel.drawmode}'
            },
            get: function (data) {
                if (Ext.isBoolean(data.drawmode)) {
                    return data.drawmode;
                }
            }
        },

        // card
        'disableMap-tab-cards-card': {
            bind: {
                objectId: '{map-tab-tabpanel.objectId}'
            },
            get: function (data) {
                return this.get("contextmenu.multiselection.enabled") ? true : !Ext.isNumeric(data.objectId);
            }
        },

        //layers
        'disableMap-map-tab-cards-layers': {
            bind: {
                drawmode: '{map-tab-tabpanel.drawmode}'
            },
            get: function (data) {
                if (Ext.isBoolean(data.drawmode)) {
                    return data.drawmode;
                }
            }
        },

        //legend
        'disableMap-map-tab-cards-legend': {
            bind: {
                drawmode: '{map-tab-tabpanel.drawmode}'
            },
            get: function (data) {
                if (Ext.isBoolean(data.drawmode)) {
                    return data.drawmode;
                }
            }
        },
        'hidden-map-tab-cards-legend': {
            bind: {
                thematismId: '{map-container.thematismId}'
            },
            get: function (data) {
                return Ext.isEmpty(data.thematismId);
            }
        },

        'updateObjectId': {
            bind: {
                objectId: '{map-tab-tabpanel.objectId}'
            },
            get: function (data) {
                this.getView().setObjectId(data.objectId);
            }
        },

        'updateDrawMode': {
            bind: {
                drawmode: '{map-tab-tabpanel.drawmode}'
            },
            get: function (data) {
                if (Ext.isBoolean(data.drawmode)) {
                    this.getView().setDrawmode(data.drawmode);
                }
            }
        }
    },

    _last_load: null
});
