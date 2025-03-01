Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.grid.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-lookuptypes-tabitems-values-grid-grid',

    data: {
        theValue: null
    },

    formulas: {
        lookupValuesProxy: {
            bind: '{theLookupType.name}',
            get: function (objectTypeName) {
                if (objectTypeName && !this.get('theLookupType').phantom) {
                    return {
                        url: Ext.String.format("/lookup_types/{0}/values", CMDBuildUI.util.Utilities.stringToHex(objectTypeName)),
                        type: 'baseproxy',
                        extraParams: {
                            active: false
                        }
                    };
                }
            }
        }
    },

    stores: {
        allValues: {
            model: 'CMDBuildUI.model.lookups.Lookup',
            proxy: '{lookupValuesProxy}',
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0,
            listeners: {
                load: 'onLoadLookupValues'
            },
            sorters: [{
                property: 'index',
                direction: 'ASC'
            }]
        }
    }
});