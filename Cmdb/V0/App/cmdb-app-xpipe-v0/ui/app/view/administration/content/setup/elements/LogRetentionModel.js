Ext.define('CMDBuildUI.view.administration.content.setup.elements.LogRetentionModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-logretention',
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
        isDefault: {
            bind: {
                rules: '{theSetup.org__DOT__cmdbuild__DOT__database__DOT__cleanup_rules}',
                defaultMode: '{defaultMode}'
            },
            get: function (data) {
                return data.rules === data.defaultMode;
            }
        },
        mode: {
            bind: '{isDefault}',
            get: function (isDefault) {
                return isDefault ? 'default' : 'custom';
            },
            set: function (value) {
                if (typeof value != 'string') {
                    return;
                }
                value = (value === 'default') ? this.get('defaultMode') : CMDBuildUI.view.administration.content.setup.elements.LogRetention.modes.custom;
                this.set('theSetup.org__DOT__cmdbuild__DOT__database__DOT__cleanup_rules', value);
            }
        },
        modesData: {
            get: function () {
                return [{
                    value: 'default',
                    label: CMDBuildUI.locales.Locales.administration.common.labels['default']
                }, {
                    value: 'custom',
                    label: CMDBuildUI.locales.Locales.administration.common.strings.custom
                }];
            }
        }
    },
    stores: {
        modesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{modesData}'
        }
    }

});