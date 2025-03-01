Ext.define('CMDBuildUI.view.administration.content.localizations.imports.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-imports-view',
    control: {
        '#cancelBtn': {
            click: 'onCancelButtonClick'
        },
        '#importBtn': {
            click: 'onImportButtonClick'
        }
    },

    onCancelButtonClick: function (button, e, eopts) {
        this.getView().up().close();
    },

    onImportButtonClick: function (button, e, eopts) {
        var me = this;
        button.setDisabled(true);
        CMDBuildUI.util.File.showLoader(true);
        var inputFile = me.getView().down('#addfileattachment').extractFileInput();
        var separatorCombobox = me.getView().down('#localizationImportSeparator');
        var url = Ext.String.format('{0}/translations/import?separator={1}',
            CMDBuildUI.util.Config.baseUrl,
            separatorCombobox.getValue());
        CMDBuildUI.util.administration.File.upload('POST', new FormData(), inputFile, url, function (success, error) {            
            CMDBuildUI.util.File.showLoader(false);
            if (button) {
                button.setDisabled(false);
            }
            if (success) {
                me.getView().up().close();
                CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-localizations-localization-view', {
                    viewModel: {
                        data: {
                            actions: {
                                view: true,
                                edit: false,
                                add: false
                            }
                        }
                    }
                });
            }
        });
    }
});