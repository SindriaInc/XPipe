Ext.define('CMDBuildUI.view.bim.xeokit.tab.ObjectsTree', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.bim.xeokit.tab.ObjectsTreeController',
        'CMDBuildUI.view.bim.xeokit.tab.ObjectsTreeModel'
    ],

    mixins: [
        'CMDBuildUI.view.bim.xeokit.Mixin'
    ],

    alias: 'widget.bim-xeokit-tab-objectstree',
    controller: 'bim-xeokit-tab-objectstree',
    viewModel: {
        type: 'bim-xeokit-tab-objectstree'
    },

    bind: {
        store: '{objectsTreeStore}'
    },

    rootVisible: false,
    singleExpand: true,
    hideHeaders: true,

    checkPropagation: 'down',

    layout: 'fit',

    columns: [{
        xtype: 'treecolumn',
        dataIndex: 'text',
        flex: 1
    }, {
        xtype: 'actioncolumn',
        width: 30,
        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
            if (record.data.leaf) {
                return Ext.baseCSSPrefix + 'fa fa-arrow-right'
            }
        },
        handler: function (view, rowIndex, colIndex, item, e, record) {
            var container = view.up().getContainer(),
                viewCard = container.getCardTab(),
                propertiesTab = container.getPropertiesTab(),
                viewer = container.getViewer(),
                scene = viewer.scene,
                objectsSelected = scene.selectedObjects;
            if (!Ext.Object.isEmpty(objectsSelected)) {
                Ext.Object.getValues(objectsSelected)[0].selected = false;
            }
            view.setSelection(record);
            scene.setObjectsSelected(record.get("entityId"), true);
            var entity = Ext.Array.findBy(Ext.Object.getValues(scene.objects), function (entity, index) {
                return entity.id == record.get("entityId");
            });
            if (entity) {
                if (!entity.mappingInfo) {
                    CMDBuildUI.util.bim.Util.getRelatedCard(viewer.projectId, entity.id, function (data) {
                        entity.mappingInfo = data;
                        viewCard.setEntity(entity);
                    });
                } else {
                    viewCard.setEntity(entity);
                }
                propertiesTab.setEntity(entity);
                container.getLayerTab().fireEvent('objectselected', entity);
                viewer.cameraFlight.flyTo(entity);
                return false;
            } else {
                propertiesTab.setEntity();
            }
        },
        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
            if (record.data.leaf) {
                return CMDBuildUI.locales.Locales.bim.tree.arrowTooltip
            }
        },
        isDisabled: function (view, rowIndex, colIndex, item, record) {
            return !record.data.leaf
        }
    }]

});