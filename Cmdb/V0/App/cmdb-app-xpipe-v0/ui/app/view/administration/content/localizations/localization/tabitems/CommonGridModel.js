Ext.define('CMDBuildUI.view.administration.content.localizations.localization.tabitems.CommonGridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-localizations-localization-tabitems-commongrid',
    data: {
        section: null
    },
    formulas: {
        localizationModel: {
            bind: {
                section: '{section}',
                languages: '{translationsstore.languagesList}'
            },
            get: function (data) {

                if (data.section && data.languages) {

                    var languagesFields = [];
                    var activeLangs = data.languages.split(',');
                    Ext.Array.forEach(activeLangs, function (item) {
                        var newField = {
                            name: item,
                            mapping: 'values.' + item,
                            persist: false,
                            type: "string"
                        };
                        languagesFields.push(newField);
                    });

                    var model = Ext.define(null, {
                        extend: 'CMDBuildUI.model.localizations.LocalizationByCode',
                        fields: languagesFields
                    });

                    return model;
                }
            }
        }
    },

    stores: {
        localizationsStore: {
            model: '{localizationModel}',
            proxy: {
                type: 'baseproxy',
                url: '/translations/by-code',
                extraParams: {
                    languages: '{translationsstore.languagesList}',
                    includeRecordsWithoutTranslation: true,
                    section: '{section}'
                }
            },
            sorters: ['element', 'type', 'default'],
            listeners: {
                load: 'onstoreLoaded'
            },
            autoLoad: '{translationsstore.autoload}',
            autoDestroy: true,
            pageSize: 0
        }
    }

});