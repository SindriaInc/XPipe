Ext.define('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-globalsearchfield-globalsearchpanel',
    formulas: {
        responseData: function(){
            return this.getView().getResponseData();
        }
    },
    stores: {
        searchPanelStore: {
            type: 'tree',
            defaultRootProperty: 'items',
            fields: [{
                    name: 'text',
                    mapping: 'description'
                }, {
                    name: 'iconCls',
                    calculate: function () {
                        return 'empty';
                    }
                }, {
                    name: 'leaf',
                    calculate: function () {
                        return false;
                    }
                }, {
                    name: 'expanded',
                    calculate: function () {
                        return true;
                    }
                }, {
                    name: 'isChild',
                    type: 'boolean'
                }, {
                    name: 'subType',
                    calculate: function () {
                        return 'empty';
                    }
                },
                'name',
                'description',
                'type',
                'items',
                'url', 'tab'
            ],
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },

            root: {
                expanded: true,
                items: '{responseData}'
            }
        }
    }
});