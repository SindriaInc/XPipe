Ext.define('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchField', {
    extend: 'Ext.form.field.Text',

    requires: [
        'CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchFieldController',
        'CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchFieldModel'
    ],
    alias: 'widget.admin-globalsearchfield',
    controller: 'administration-components-globalsearchfield-globalsearchfield',
    viewModel: {
        type: 'administration-components-globalsearchfield-globalsearchfield'
    },
    name: 'search',
    width: 350,
    cls: 'administration-input',
    reference: 'globalsearchtext',
    itemId: 'globalsearchtext',
    bind: {
        emptyText: '{emptyText}',
        hidden: '{!canFilter}'
    },
    config: {
        /**
         * @cfg {string} Mandatory es: view/class/process/domain...
         */
        objectType: null,
        subType: null
    },
    listeners: {
        beforedestroy: 'onDestroy',
        specialkey: 'onSearchSpecialKey'
    },

    autoEl: {
        'data-testid': 'administration-grid-localsearchfield-input'
    },
    initComponent: function () {
        var view = this;
        this.setTriggers({
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                itemId: 'globalsearchsubmit',
                handler: view.getController().onGlobalSearchSubmit,
                autoEl: {
                    'data-testid': 'administration-grid-localsearchfield-input-search-trigger'
                }
            },
            clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
        });
        this.callParent(arguments);

    }
});