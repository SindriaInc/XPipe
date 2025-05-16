Ext.define('CMDBuildUI.view.administration.content.setup.elements.LogsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-logs',
    data: {

        disabledTabs: {
            log: false,
            retention: false
        }
    },

    formulas: {

        configManager: function () {
            var me = this;
            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(
                function (configs) {
                    if (!me.destroyed) {
                        configs.forEach(function (key) {
                            me.set(Ext.String.format('theSetup.{0}', key._key), (key.hasValue) ? key.value : key.default);
                        });
                    }
                }
            );
        }
    }
});