Ext.define('CMDBuildUI.view.widgets.presetfromcard.Panel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.widgets.presetfromcard.PanelController',
        'CMDBuildUI.view.widgets.presetfromcard.PanelModel'
    ],

    alias: 'widget.widgets-presetfromcard-panel',
    controller: 'widgets-presetfromcard-panel',
    viewModel: {
        type: 'widgets-presetfromcard-panel'
    },

    mixins: [
        'CMDBuildUI.view.widgets.Mixin'
    ],

    /**
     * @cfg {String} theWidget.ClassName
     * Class or Process name
     */

    /**
     * @cfg {Object} theWidget._Filter_ecql
     * eCQL filter definition.
     */

    /**
     * @cfg {Object} theWidget.AttributeMapping
     * Mapping definition to populate process data from card.
     * A string `ProcessAttribute1=CardAttribute1,ProcessAttributeX=CardAttributeY`.
     */

    layout: "fit",

    tbar: [{
        xtype: 'textfield',
        name: 'search',
        width: 250,

        emptyText: CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search,
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search'
        },
        reference: 'searchtext',
        itemId: 'searchtext',
        cls: 'management-input',
        bind: {
            hidden: '{!canFilter}'
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
        }
    }],

    fbar: [{
        xtype: 'button',
        ui: 'management-action',
        itemId: 'applybtn',
        text: CMDBuildUI.locales.Locales.common.actions.apply,
        disabled: true,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.apply'
        },
        bind: {
            disabled: '{!selection}'
        },
        autoEl: {
            'data-testid': 'widgets-presetfromcard-apply'
        }
    }, {
        xtype: 'button',
        ui: 'secondary-action',
        itemId: 'closebtn',
        text: CMDBuildUI.locales.Locales.common.actions.close,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.close'
        },
        autoEl: {
            'data-testid': 'widgets-presetfromcard-close'
        }
    }]
});