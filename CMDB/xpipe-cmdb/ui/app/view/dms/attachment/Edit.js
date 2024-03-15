
Ext.define('CMDBuildUI.view.dms.attachment.Edit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.dms.Util',
        'CMDBuildUI.view.dms.attachment.EditController',
        'CMDBuildUI.view.dms.attachment.EditModel'
    ],
    mixins: [
        'CMDBuildUI.view.dms.attachment.Mixin'
    ],

    alias: 'widget.dms-attachment-edit',
    controller: 'dms-attachment-edit',
    viewModel: {
        type: 'dms-attachment-edit'
    },

    publishes: [
        'objectType',
        'objectTypeName',
        'objectId',
        'attachmentId',
        'DMSCategoryTypeName',
        'DMSCategoryValue',
        'DMSModelClassName',
        'DMSModelClass',
        'DMSClass',
        'theObject'
    ],
    reference: 'dms-attachment-edit',

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.update,

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
                helpValue: '{dms-attachment-edit.DMSClass.help}'
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
            'data-testid': 'dms-edit-save'
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
            'data-testid': 'dms-edit-cancel'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        handler: 'onCancelButton'
    }],

    scrollable: true,

    updateTheObject: function (theObject, oldValue) {
        if (theObject) {
            theObject.addLock().then(function (success) {
                if (!this.destroyed && !success) {
                    this.closePanel();
                }
            }, Ext.emptyFn, Ext.emptyFn, this);
        }
    },

    closePanel: function () {
        var upPanel = this.up("panel");

        upPanel.fireEvent('popupcancel');
        upPanel.close();
    }
});
