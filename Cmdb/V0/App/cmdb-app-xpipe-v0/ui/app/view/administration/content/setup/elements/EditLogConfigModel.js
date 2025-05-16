Ext.define('CMDBuildUI.view.administration.content.setup.elements.EditLogConfigModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-editlogconfig',
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
        logSettingValues: {
            get: function () {
                return CMDBuildUI.model.administration.LogSetting.getLevels();
            }
        }
    },
    stores: {
        configKeysStore: {
            model: 'CMDBuildUI.model.administration.LogSetting',
            proxy: {
                type: 'baseproxy',
                url: Ext.String.format("{0}/system/loggers", CMDBuildUI.util.Config.baseUrl),
                extraParams: {
                    includeLoggersWithoutLevel: true
                }
            },
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0
        },
        logSettingValuesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{logSettingValues}',
            autoDestroy: true,
            pageSize: 0
        }
    }

});