Ext.define('CMDBuildUI.view.administration.content.dashboards.card.Card', {
    extend: 'Ext.form.Panel',
    alias: 'widget.view-administration-content-dashboards-card',

    requires: [
        'CMDBuildUI.view.administration.content.dashboards.card.CardController',
        'CMDBuildUI.view.administration.content.dashboards.card.CardModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],
    controller: 'view-administration-content-dashboards-card',
    viewModel: {
        type: 'view-administration-content-dashboards-card'
    },
    bubbleEvents: [
        'itemcreated',
        'itemupdated',
        'cancelcreated',
        'cancelupdating'
    ],
    publishes: [
        'action',
        'theDashboard'
    ],
    twoWayBindable: {
        action: null,
        theDashboard: null
    },

    modelValidation: true,
    config: {
        // action: null,
        theDashboard: null,
        data: {}
    },
    bind: {
        theDashboard: '{theDashboard}',
        // userCls: '{formModeCls}', // this is used for hide label localzation icon in `view` mode        
        hidden: '{hideForm}'
    },
    layout: {
        type: 'container',
        reserveScrollbar: true
    },
    hidden: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,
    ui: 'administration-formpagination',
    cls: 'administration tab-hidden',

    dockedItems: [{
        dock: 'top',
        xtype: 'container',
        bind: {
            hidden: '{hideForm}'
        },
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true,
                activeToggle: true,
                delete: true
            }, 'dashboards', 'theDashboard')
        }]
    }, {
        xtype: 'toolbar',
        itemId: 'bottomtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
    }, {
        xtype: 'toolbar',
        itemId: 'bottomviewtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.edit|| !actions.view && !showInPopup}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getCloseButton ? CMDBuildUI.util.administration.helper.FormHelper.getCloseButton() : []
    }]
});