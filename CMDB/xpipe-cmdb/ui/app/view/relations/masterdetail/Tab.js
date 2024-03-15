
Ext.define('CMDBuildUI.view.relations.masterdetail.Tab', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.relations.masterdetail.TabController',
        'CMDBuildUI.view.relations.masterdetail.TabModel'
    ],

    alias: 'widget.relations-masterdetail-tab',
    controller: 'relations-masterdetail-tab',
    viewModel: {
        type: 'relations-masterdetail-tab'
    },

    config: {
        /**
         * @cfg {String} targetType
         */
        targetType: null,

        /**
         * @cfg {String} targetTypeName
         */
        targetTypeName: null,

        /**
         * @cfg {CMDBuildUI.model.domains.Domain} domain
         */
        domain: null,

        /**
         * @cfg {readOnly} 
         * 
         * Set to `true` to shwow details tabs in read-only mode
         */
        readOnly: false
    },

    /**
     * @cfg {CMDBuildUI.model.classes.Class|CMDBuildUI.model.processes.Process} targetTypeName
     */
    targetTypeObject: null,

    /**
     * @event targetdataupdated 
     * Fires when target data has been updated.
     * @param {CMDBuildUI.view.relations.masterdetail.Tab} view
     * @param {CMDBuildUI.model.classes.Class|CMDBuildUI.model.processes.Process} targetTypeObject
     * @param {CMDBuildUI.model.classes.Card} targetTypeModel
     * @param {CMDBuildUI.model.domains.Domain} domain
     * @param {Object} eOpts
     */

    publishes: [
        'targetType',
        'targetTypeName',
        'domain'
    ],

    layout: 'fit',

    bind: {
        title: '{title}',
        targetType: '{targetType}',
        targetTypeName: '{targetTypeName}',
        domain: '{domain}'
    },

    tabConfig: {
        width: 150,
        textAlign: 'left'
    },

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.relations.adddetail,
        reference: 'adddetailbtn',
        itemId: 'adddetailbtn',
        iconCls: 'x-fa fa-plus',
        ui: 'management-action-small',
        hidden: true,
        bind: {
            text: '{addbutton.text}',
            disabled: '{addbutton.disabled}',
            hidden: '{addbutton.hidden}'
        },
        autoEl: {
            'data-testid': 'relations-masterdetail-tab-addbtn'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.adddetail'
        }
    }, {
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText: CMDBuildUI.locales.Locales.common.actions.searchtext,
        itemId: 'searchtext',
        cls: 'management-input',
        bind: {
            value: '{search.value}'
        },
        listeners: {
            specialkey: 'onSearchSpecialKey'
        },
        triggers: {
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                handler: 'onSearchSubmit'
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear'
            }
        },
        autoEl: {
            'data-testid': 'relations-masterdetail-tab-searchtext'
        },
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.common.actions.searchtext'
        }
    }],

    /**
     * This method is called when targetTypeName is changed.
     * @param {String} newValue
     * @param {String} oldValue
     */
    updateTargetTypeName: function (newValue, oldValue) {
        this.targetTypeObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(newValue);
    },

    /**
     * @return {CMDBuildUI.model.classes.Class|CMDBuildUI.model.processes.Process}
     */
    getTargetTypeObject: function () {
        return this.targetTypeObject;
    }

});
