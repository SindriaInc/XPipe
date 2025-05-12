Ext.define('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchFieldController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-globalsearchfield-globalsearchfield',

    privates: {
        resultPanel: null
    },
    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onGlobalSearchSubmit: function (field, newValue, oldValue) {
        var me = field;
        var value = field.getValue();
        if (me.resultPanel) {
            me.resultPanel.destroy();
            me.resultPanel = null;
        }
        var objectType = me.getObjectType();
        var subType = me.getSubType();
        var url, filter;

        switch (objectType) {            
            case 'widget':
            case 'contextmenu':                
                    filter = '&filter={"query":"' + value + '"}';                
                url = Ext.String.format('{0}/search/components/{1}?{2}', CMDBuildUI.util.Config.baseUrl, objectType, encodeURI(filter));
                break;

            case 'dmsmodels':
                url = Ext.String.format('{0}/search/dms/models?query={1}', CMDBuildUI.util.Config.baseUrl, value);
                break;
            case 'dmscategories':
                url = Ext.String.format('{0}/search/dms/categories?query={1}', CMDBuildUI.util.Config.baseUrl, value);
                break;
            case 'etlgates':
                url = Ext.String.format('{0}/search/etl/gates?query={1}', CMDBuildUI.util.Config.baseUrl, value);
                break;
            case 'templates':
                filter = '&filter={"query":"' + value + '","attribute":{"simple":{"value":["csv","xls","xlsx"],"operator":"IN","attribute":"fileFormat","parameterType":"fixed"}}}';
                url = Ext.String.format('{0}/search/etl/templates?{1}', CMDBuildUI.util.Config.baseUrl, encodeURI(filter));
                break;
            default:
                filter = '&filter={"query":"' + value + '"}';
                url = Ext.String.format('{0}/search/{1}?{2}', CMDBuildUI.util.Config.baseUrl, objectType, encodeURI(filter));
                break;
        }
        if (value) {
            var responseData = [];
            Ext.Ajax.request({
                url: url,
                method: 'GET',
                callback: function (request, success, response) {
                    var parsedResponse = Ext.JSON.decode(response.responseText);
                    responseData = parsedResponse.data;
                    me.resultPanel = Ext.create('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchPanel', {
                        y: field.getY() + field.getHeight(),
                        x: field.getX(),
                        responseData: responseData,
                        objectType: objectType,
                        subType: subType
                    });
                    me.resultPanel.show();
                    field.focus();
                }
            });
        }

    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.onGlobalSearchSubmit(field);
        }
    },

    onDestroy: function () {
        var resultPanel = this.getView().resultPanel;
        if (resultPanel && !resultPanel.destroyed) {
            resultPanel.destroy();
        }
    }

});