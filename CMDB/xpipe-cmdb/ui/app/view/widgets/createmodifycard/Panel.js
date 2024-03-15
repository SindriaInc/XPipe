
Ext.define('CMDBuildUI.view.widgets.createmodifycard.Panel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.widgets.createmodifycard.PanelController',
        'CMDBuildUI.view.widgets.createmodifycard.PanelModel'
    ],

    mixins: [
        'CMDBuildUI.view.widgets.Mixin',
        'CMDBuildUI.mixins.grids.AddButtonMixin'
    ],

    alias: 'widget.widgets-createmodifycard-panel',
    controller: 'widgets-createmodifycard-panel',
    viewModel: {
        type: 'widgets-createmodifycard-panel'
    },

    config: {
        form: null
    },

    /**
     * @cfg {String} ButtonLabel
     * Label for the widgetButton
     */

    /**
     * @cfg {Boolean} theWidget.ReadOnly
     * If true disables create/modify
     */

    /**
     * @cfg {String} theWidget.ClassName
     * ClassName to select the card preset from
     */

    /**
     * @cfg {String} theWidget.ObjId
     * Card Id to modify
     */

    /**
     * @cfg {String} theWidget.Reference
     * Attribute name reference of the card to modify
     */

    layout: 'fit',
    autoScroll: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.classes.cards.addcard,
        reference: 'addcardbtn',
        itemId: 'addcardbtn',
        iconCls: 'x-fa fa-plus',
        ui: 'management-action',
        disabled: true,
        autoEl: {
            'data-testid': 'selection-popup-addcardbtn'
        },
        bind: {
            text: '{addbtn.text}',
            disabled: '{addbtn.disabled}',
            hidden: '{addbtn.hidden}'
        }
    }],

    /**
     * removes the form in configuration
     */
    removeForm: function () {
        var form = this.getForm();

        if (form.isComponent) {
            form.destroy();
            this.setForm(null);
        }
    }
});