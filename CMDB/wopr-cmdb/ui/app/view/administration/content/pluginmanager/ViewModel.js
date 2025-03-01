Ext.define('CMDBuildUI.view.administration.content.pluginmanager.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-pluginmanager-view',

    data: {
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        isPatchesAvailable: false,
        recalculateConfigs: true,
        pluginConfigs: {}
    },

    formulas: {
        setActions: {
            bind: '{action}',
            get: function (action) {
                this.set('actions.view', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },

        pluginLabel: {
            get: function () {
                return CMDBuildUI.locales.Locales.administration.plugin.plugin;
            }
        },

        setPluginConfigs: {
            bind: {
                thePlugin: '{thePlugin}',
                recalculateConfigs: '{recalculateConfigs}'
            },
            get: function (data) {
                const thePlugin = data.thePlugin;
                const pluginConfigs = {};
                if (data.recalculateConfigs && thePlugin && !Ext.isEmpty(thePlugin.get("configs"))) {
                    this.set("recalculateConfigs", false);
                    Ext.Object.each(thePlugin.get("configs"), function (key, value, myself) {
                        if (key !== "_model") {
                            pluginConfigs[key.replace(/\./g, '__DOT__')] = value;
                        }
                    });
                    this.set("pluginConfigs", pluginConfigs);
                }
            }
        }
    }

});
