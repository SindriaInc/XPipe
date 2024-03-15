Ext.define('CMDBuildUI.view.dms.attachment.Create', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.dms.Util',
        'CMDBuildUI.view.dms.attachment.CreateController',
        'CMDBuildUI.view.dms.attachment.CreateModel'
    ],
    mixins: [
        'CMDBuildUI.view.dms.attachment.Mixin'
    ],

    alias: 'widget.dms-attachment-create',
    controller: 'dms-attachment-create',
    viewModel: {
        type: 'dms-attachment-create'
    },

    publishes: [
        'objectType',
        'objectTypeName',
        'objectId',
        'attachmentId',
        'DMSCategoryTypeName',
        'DMSCategoryDescription',
        'DMSCategoryValue',
        'DMSModelClassName',
        'DMSClass',
        'DMSModelClass',
        'theObject'
    ],
    reference: 'dms-attachment-create',

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.create,
    bind: {
        DMSClass: '{DMSClassCalculation}'
    },

    items: [{
        xtype: 'toolbar',
        cls: 'fieldset-toolbar',
        items: [{
            xtype: 'tbfill'
        }, CMDBuildUI.view.dms.Util.getHelpTool({
            bind: {
                helpValue: '{dms-attachment-create.DMSClass.help}'
            }
        })]
    }, {
        xtype: 'form',
        itemId: 'formpanel'
    }],
    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        ui: 'management-action-small',
        autoEl: {
            'data-testid': 'dms-create-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        handler: 'onSaveButton'
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        reference: 'cancelbtn',
        ui: 'secondary-action-small',
        autoEl: {
            'data-testid': 'dms-create-cancel'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        handler: 'onCancelButton'
    }],

    scrollable: true
});