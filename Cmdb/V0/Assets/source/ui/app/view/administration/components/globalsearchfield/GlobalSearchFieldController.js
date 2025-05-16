Ext.define('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchFieldController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-globalsearchfield-globalsearchfield',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onDestroy',
            specialkey: 'onSearchSpecialKey'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchField} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.setTriggers({
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                itemId: 'globalsearchsubmit',
                handler: this.onGlobalSearchSubmit,
                autoEl: {
                    'data-testid': 'administration-grid-localsearchfield-input-search-trigger'
                }
            },
            clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
        });
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onGlobalSearchSubmit: function (field, newValue, oldValue) {
        const value = field.getValue();
        if (field.resultPanel) {
            field.resultPanel.destroy();
            field.resultPanel = null;
        }
        const objectType = field.getObjectType();
        const subType = field.getSubType();
        let url, filter;

        switch (objectType) {
            case 'widget':
            case 'contextmenu':
                filter = '&filter={"query":"' + value + '"}';
                url = Ext.String.format('{0}/search/components/{1}?{2}', CMDBuildUI.util.Config.baseUrl, objectType, encodeURI(filter));
                break;
            case 'busdescriptors':
                url = Ext.String.format('{0}/search/busdescriptors?query={1}', CMDBuildUI.util.Config.baseUrl, value);
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
            Ext.Ajax.request({
                url: url,
                method: 'GET',
                callback: function (request, success, response) {
                    const parsedResponse = Ext.JSON.decode(response.responseText);
                    field.resultPanel = Ext.create('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchPanel', {
                        y: field.getY() + field.getHeight(),
                        x: field.getX(),
                        objectType: objectType,
                        subType: subType,
                        viewModel: {
                            data: {
                                responseData: parsedResponse.data
                            }
                        }
                    });
                    field.resultPanel.show();
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

    /**
     *
     * @param {CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchField} view
     * @param {Object} eOpts
     */
    onDestroy: function (view, eOpts) {
        const resultPanel = view.resultPanel;
        if (resultPanel && !resultPanel.destroyed) {
            resultPanel.destroy();
        }
    },

    privates: {
        resultPanel: null
    }
});