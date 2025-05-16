Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.ValuesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-lookuptypes-tabitems-values-values',
    data: {
        theValue: {}
    },
    formulas: {
        getSelectedValue: {
            bind: '{theValue}',
            get: function (theValue) {
                if (theValue) {
                    return theValue;
                }
            }
        },
        allValuesProxy: {
            bind: '{theValue.name}',
            get: function (objectTypeName) {
                if (objectTypeName) {
                    return {
                        url: Ext.String.format("/lookup_types/{0}/values", CMDBuildUI.util.Utilities.stringToHex(objectTypeName)),
                        type: 'baseproxy'
                    };
                }
            }
        }
    }
});
