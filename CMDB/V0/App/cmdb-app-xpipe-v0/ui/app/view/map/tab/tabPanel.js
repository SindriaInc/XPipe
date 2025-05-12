
Ext.define('CMDBuildUI.view.map.tab.tabPanel', {
    extend: 'Ext.tab.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.tabPanelController',
        'CMDBuildUI.view.map.tab.tabPanelModel'

    ],

    alias: 'widget.map-tab-tabpanel',
    statics: {
        tabIndex: {
            'map-tab-cards-navigationtree': 0,
            'map-tab-cards-list': 1,
            'map-tab-cards-card': 2,
            'map-tab-cards-layers': 3,
            'map-tab-cards-legend': 4
        }
    },

    config: {

        objectType: undefined,
        objectTypeName: undefined,
        objectId: undefined,

        theObject: undefined,
        /**
         * calculated starting from objectType and objectTypeName
        */
        layerStore: {
            $value: undefined,
            evented: true
        },

        navigationTree: {
            $value: null,
            evented: true
        },

        attach_nav_tree_collection: {
            $value: undefined,
            evented: true
        },

        geoValues: {
            $value: Ext.create('Ext.data.Store', {
                model: 'CMDBuildUI.model.gis.GeoValue',
                proxy: {
                    type: 'baseproxy'
                }
            })
        },

        drawmode: undefined
    },
    reference: 'map-tab-tabpanel',
    publishes: [
        'objectType',
        'objectTypeName',
        'objectId',
        'theObject',
        'navigationTree',
        'drawmode',
        'attach_nav_tree_collection'
    ],

    twoWayBindable: [
        'theObject', //maybe this ca be deleted
        "objectId",
        'drawmode',
        'layerStore'
    ],
    bind: {
        activeTab: '{activeItemCalculation}',
        layerStore: '{layerStore}'
    },

    controller: 'map-tab-tabpanel',
    viewModel: {
        type: 'map-tab-tabpanel'
    },

    mixins: [
        'CMDBuildUI.view.map.Mixin'
    ],

    ui: 'managementlighttabpanel',

    deferredRender: false,
    collapseDirection: 'left',

    items: [{
        xtype: 'map-tab-cards-navigationtree',
        title: CMDBuildUI.locales.Locales.gis.tree,
        localized: {
            title: 'CMDBuildUI.locales.Locales.gis.tree'
        },
        bind: {
            objectId: '{map-tab-tabpanel.objectId}',
            objectType: '{map-tab-tabpanel.objectType}',
            objectTypeName: '{map-tab-tabpanel.objectTypeName}',
            attach_nav_tree_collection: '{map-tab-tabpanel.attach_nav_tree_collection}'
        },
        tabConfig: {
            hidden: true,
            bind: {
                hidden: '{hiddenMap-tab-cards-navigationtree}',
                disabled: '{disableMap-map-tab-cards-navigationtree}'
            }
        }
    }, {
        xtype: 'map-tab-cards-list',
        title: CMDBuildUI.locales.Locales.gis.list,
        localized: {
            title: 'CMDBuildUI.locales.Locales.gis.list'
        },
        bind: {
            store: '{cards}',
            objectId: '{map-tab-tabpanel.objectId}' //is two way bindable
        },
        tabConfig: {
            bind: {
                disabled: '{disableMap-map-tab-cards-list}'
            }
        }
    }, {
        xtype: 'map-tab-cards-card',
        title: CMDBuildUI.locales.Locales.gis.card,
        localized: {
            title: 'CMDBuildUI.locales.Locales.gis.card'
        },
        tabConfig: {
            bind: {
                disabled: '{disableMap-tab-cards-card}'
            },
            listeners: {
                disable: function (tab, eOpts) {
                    if (tab.getRefOwner().activeTab == tab) {
                        tab.tabBar.getRefOwner().setActiveTab(1);
                    }
                },
                enable: function (tab, eOpts) {
                    // tab.getRefOwner().getRefOwner().setActiveTab(CMDBuildUI.view.map.tab.tabPanel.tabIndex['map-tab-cards-card']);
                }
            }
        }
    }, {
        xtype: 'map-tab-cards-layers',
        title: CMDBuildUI.locales.Locales.gis.layers,
        localized: {
            title: 'CMDBuildUI.locales.Locales.gis.layers'
        },
        tabConfig: {
            bind: {
                disabled: '{disableMap-map-tab-cards-layers}'
            }
        }
    }, {
        xtype: 'map-tab-cards-legend',
        title: CMDBuildUI.locales.Locales.thematism.legend,
        localized: {
            title: 'CMDBuildUI.locales.Locales.thematism.legend'
        },
        bind: {
            theThematism: '{map-container.theThematism}',
            highlightselected: '{map-container.highlightselected}'
        },
        tabConfig: {
            hidden: true,
            bind: {
                hidden: '{hidden-map-tab-cards-legend}',
                disabled: '{disableMap-map-tab-cards-legend}'
            },
            listeners: {
                hide: function (tab, eOpts) {
                    if (tab.active) {
                        //if this tab is currently active, sets as active the first tab available
                        var items = tab.getRefOwner().getRefItems();

                        for (var i = 0, item; i < items.length; i++) {
                            item = items[i];
                            if (item.isVisible()) {
                                tab.getRefOwner().getRefOwner().setActiveTab(i);
                                return;
                            }
                        }
                    }
                },
                show: function (tab, eOpts) {
                    //set itself as active on show events
                    tab.getRefOwner().getRefOwner().setActiveTab(CMDBuildUI.view.map.tab.tabPanel.tabIndex['map-tab-cards-legend']);
                }
            }
        }
    }],

    refreshBtnClick: function (view) {
        this.fireEventArgs('refreshbtnclick', [this]);
    }
});
