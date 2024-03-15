Ext.define('CMDBuildUI.view.administration.content.setup.elements.AuditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-audit',

    data: {
        actions: {
            add: false,
            edit: false,
            view: true
        }
    },

    formulas: {
        action: {
            bind: {
                isView: '{actions.view}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}'
            },
            get: function (data) {
                if (data.isView) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },

        trackingModes: function () {
            return [{
                value: 'always',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.always
            }, {
                value: 'on_error',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.onerror
            }];
        }
    },

    stores: {
        trackingModesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{trackingModes}',
            autoDestroy: true,
            pageSize: 0
        }
    }

});