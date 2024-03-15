
Ext.define('CMDBuildUI.view.bim.bimserver.ContainerBim', {
    extend: 'Ext.container.Container',
    requires: [
        'CMDBuildUI.view.bim.bimserver.ContainerBimController',
        'CMDBuildUI.view.bim.bimserver.ContainerBimModel'
    ],
    alias: 'widget.bim-bimserver-container',
    controller: 'bim-bimserver-containerbim',
    viewModel: {
        type: 'bim-bimserver-containerbim'
    },

    config: {
        projectId: null,
        selectedId: null,
        hiddenNodes: undefined,
        treeStore: undefined,
        hiddenTypes: undefined,
        layerStore: undefined,
        loadMask: undefined
    },
    reference: 'bim-bimserver-containerbim',
    publishes: [
        'selectedId',
        'hiddenNodes',
        'treeStore',
        'hiddenTypes',
        'layerStore'
    ],

    layout: 'border',
    items: [{ //TODO: Set a maxWidth
        region: 'west',
        width: '33%',
        split: true,
        collapsible: false,
        xtype: 'tabpanel',
        layout: 'fit',
        ui: 'managementlighttabpanel',
        deferredRender: false,
        items: [{
            xtype: 'bim-bimserver-tab-cards-tree',
            title: CMDBuildUI.locales.Locales.bim.tree.label,
            localized: {
                title: 'CMDBuildUI.locales.Locales.bim.tree.label'
            },
            bind: {
                selectedId: '{bim-bimserver-containerbim.selectedId}',
                hiddenNodes: '{bim-bimserver-containerbim.hiddenNodes}',
                store: '{bim-bimserver-containerbim.treeStore}'
            }
        }, {
            xtype: 'bim-bimserver-tab-cards-layers',
            title: CMDBuildUI.locales.Locales.bim.layers.label,
            localized: {
                title: 'CMDBuildUI.locales.Locales.bim.layers.label'
            },
            bind: {
                hiddenTypes: '{bim-bimserver-containerbim.hiddenTypes}',
                store: '{bim-bimserver-containerbim.layerStore}'
            }
        }, {
            xtype: 'bim-bimserver-tab-cards-cards',
            title: CMDBuildUI.locales.Locales.bim.card.label,
            localized: {
                title: 'CMDBuildUI.locales.Locales.bim.card.label'
            },
            itemId: 'bim-bimserver-tab-cards-card',
            disabled: true
        }, {
            xtype: 'bim-bimserver-tab-cards-ifcproperties',
            title: CMDBuildUI.locales.Locales.bim.ifcproperties.label,
            localized: {
                title: 'CMDBuildUI.locales.Locales.bim.ifcproperties.label'
            },
            disabled: true
        }]
    }, {
        reference: 'rightPanel',
        xtype: 'panel',
        region: 'center',
        layout: 'fit',
        html: '<div style="height: inherit;width: inherit;" id="divBim3DView"><canvas  style="height: inherit;width: inherit;"></canvas></div>', //divBim3DView
        fbar: [{
            xtype: 'toolbar',
            flex: 1,
            items: [{
                xtype: 'tbtext',
                html: CMDBuildUI.locales.Locales.bim.menu.camera + ":"
            }, {
                iconCls: 'cmdbuildicon-default-bim',
                tooltip: CMDBuildUI.locales.Locales.bim.menu.resetView,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.menu.resetView'
                },
                cls: 'management-tool',
                xtype: 'tool',
                handler: function () {
                    CMDBuildUI.util.bim.Viewer.defaultView();
                }
            }, {
                iconCls: 'cmdbuildicon-front-bim',
                tooltip: CMDBuildUI.locales.Locales.bim.menu.frontView,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.menu.frontView'
                },
                cls: 'management-tool',
                xtype: 'tool',
                handler: function () {
                    CMDBuildUI.util.bim.Viewer.frontView();
                }
            }, {
                iconCls: 'cmdbuildicon-side-bim',
                tooltip: CMDBuildUI.locales.Locales.bim.menu.sideView,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.menu.sideView'
                },
                cls: 'management-tool',
                xtype: 'tool',
                handler: function () {
                    CMDBuildUI.util.bim.Viewer.sideView();
                }
            }, {
                iconCls: 'cmdbuildicon-top-bim',
                tooltip: CMDBuildUI.locales.Locales.bim.menu.topView,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.menu.topView'
                },
                cls: 'management-tool',
                xtype: 'tool',
                handler: function () {
                    CMDBuildUI.util.bim.Viewer.topView();
                }
            }, {
                xtype: 'tbseparator'
            }, {
                xtype: 'tbtext',
                html: CMDBuildUI.locales.Locales.bim.menu.mod + ":",
                localized: {
                    html: 'CMDBuildUI.locales.Locales.bim.menu.mod'
                }
            }, {
                iconCls: 'x-fa fa-arrows',
                reference: 'mode',
                xtype: 'tool',
                itemId: 'bim-bimserver-containerbim-menu-mode',
                mode: 'rotate',
                tooltip: CMDBuildUI.locales.Locales.bim.menu.pan,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.menu.pan'
                },
                cls: 'management-tool'
            }, /* {
                xtype: 'tbseparator'
            }, {
                xtype: 'tool',
                iconCls: 'cmdbuildicon-orthographic',
                itemId: 'bim-bimserver-containerbim-menu-camera',
                cameraType: 'perspective',
                tooltip: CMDBuildUI.locales.Locales.bim.menu.orthographic,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.menu.orthographic'
                },
                cls: 'management-tool'
            }, */ {
                xtype: 'tbfill'
            }]
        }],
        controller: {
            control: {
                '#': {
                    afterrender: 'onAfterRender',
                    resize: 'onResize'
                }
            },
            onAfterRender: function () {
                var view = this.getView().up('bim-bimserver-container');

                // var pId = this.getView().getBubbleParent().projectId;
                this.getView().getBubbleParent().getController().onDivRendered(
                    view.getProjectId(),
                    view.getSelectedId());
            },
            /**
             * This function handles the resize of the canvas
             * @param panel the panel
             * @param height the height of the panel
             * @param width the width of the panel 
             */
            onResize: function (panel, height, width) {
                // CMDBuildUI.util.bim.SceneTree.resize(height, width);
            }
        }
    }],

    initComponent: function () {
        this.callParent(arguments);
        this.setHiddenTypes(Ext.util.Collection.create({
            keyFn: function (item) {
                return item.get('ifcName');
            }
        }));
        this.setLayerStore(Ext.create('Ext.data.Store', {
            model: 'CMDBuildUI.model.bim.Types',
            sorters: [{
                property: 'name',
                direction: 'ASC'
            }]
        }));

        this.setHiddenNodes(Ext.util.Collection.create({
            keyFn: function (item) {
                return item.get('oid');
            },
            grouper: {
                property: 'ifcName',
                groupFn: function (item) {
                    return item.get(this._property)
                }
            }
        }));
        this.setTreeStore(Ext.create('Ext.data.TreeStore', {
            model: 'CMDBuildUI.model.bim.Objects'
        }));

        this.setLoadMask(new Ext.LoadMask({
            target: this,
            count: 0
        }));
    }
});
