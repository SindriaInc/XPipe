
Ext.define('CMDBuildUI.view.dms.attachment.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.dms.Util',
        'CMDBuildUI.view.dms.attachment.ViewController',
        'CMDBuildUI.view.dms.attachment.ViewModel'
    ],
    mixins: [
        'CMDBuildUI.view.dms.attachment.Mixin'
    ],

    alias: 'widget.dms-attachment-view',
    controller: 'dms-attachment-view',
    viewModel: {
        type: 'dms-attachment-view'
    },

    publishes: [
        'objectType',
        'objectTypeName',
        'objectId',
        'attachmentId',
        'DMSCategoryTypeName',
        'DMSCategoryValue',
        'DMSModelClassName',
        'DMSClass',
        'DMSModelClass',
        'theObject'
    ],
    reference: 'dms-attachment-view',

    bind: {
        DMSClass: '{DMSClassCalculation}'
    },

    items: [{
        xtype: 'form',
        itemId: 'formpanel'
    }],

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
    scrollable: 'y'
});
