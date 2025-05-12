Ext.define('CMDBuildUI.view.map.tab.cards.LayersModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-tab-cards-layers',

    formulas: {
        filterUpdate: {
            bind: {
                zoom: '{map-container.zoom}',
                layerStore: '{layersTreeStore}'
            },
            get: function (data) {
                if (data.zoom && data.layerStore) {
                    data.layerStore.addFilter([{
                        id: CMDBuildUI.view.map.Map.filterZoomMaxId,
                        property: 'zoomMax',
                        operator: '>=',
                        value: data.zoom
                    }, {
                        id: CMDBuildUI.view.map.Map.filterZoomMinId,
                        property: 'zoomMin',
                        operator: '<=',
                        value: data.zoom
                    }]);
                }
            }
        }
    },

    stores: {
        layersTreeStore: {
            type: 'tree',
            proxy: 'memory',
            root: '{treeRootItem}',
            listeners: {
                update: 'onLayersTreeStoreUpdate'
            }
        }
    }
});