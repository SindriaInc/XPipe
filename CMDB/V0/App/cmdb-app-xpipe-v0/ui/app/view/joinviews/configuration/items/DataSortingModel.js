Ext.define('CMDBuildUI.view.joinviews.configuration.items.DataSortingModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.joinviews-configuration-items-datasorting',
    data: {
        selectableAttributesLoaded: false
    },
    formulas: {
        defaultOrders: function () {
            return CMDBuildUI.model.views.JoinViewSorter.getDefaultOrders();
        },
        defaultOrderEmptyRecord: function () {
            return Ext.create('CMDBuildUI.model.views.JoinViewSorter', {direction: 'ASC'});
        },
        selectblesAtributesFilter: {
            bind: {
                bindTo: '{theView.sorter}',
                deep: true
            },
            get: function (storeStore) {
                var me = this;
                return [function (item) {
                    return !storeStore.findRecord('property', item.get('expr'), 0, false, true);
                }];
            }
        },
        selectablesAtributesDataManager: {
            bind: {                
                attributes: '{selectedAttributes}',
                sorter: '{theView.sorter}'
            },
            get: function (data) {
                var me = this;
                var getData = function (store) {
                    var selectablesAttributes = [];                                    
                    Ext.Array.forEach(data.attributes, function (item) {                        
                        if (!data.sorter.findRecord('property', item.get('expr'), 0, false, true)) {
                            selectablesAttributes.push(item);
                        }
                    });
                    Ext.Array.forEach(data.sorter.getRange(), function (item) {                                          
                        if (!Ext.Array.findBy(selectablesAttributes, function(attribute){                            
                            return item.get('property') === attribute.get('name');
                        })) {                            
                            data.sorter.remove(item);
                        }
                    });
                    me.set('selectablesAtributesData', selectablesAttributes);                    
                };
                getData();
                data.sorter.on('datachanged', getData);
            }
        }
    },
    stores: {
        selectablesAttributes: {
            model: 'CMDBuildUI.model.views.JoinViewAttribute',
            data: '{selectablesAtributesData}',
            grouper: {
                property: '_attributeClassAlias'
            }
        },
        defaultOrderDirectionsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            data: '{defaultOrders}'
        },

        defaultOrderStoreNew: {
            model: 'CMDBuildUI.model.views.JoinViewSorter',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{defaultOrderEmptyRecord}'
        }
    }

});