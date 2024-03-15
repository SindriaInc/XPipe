Ext.define('CMDBuildUI.view.administration.content.localizations.configuration.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-configuration-view',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.localizations.view} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        CMDBuildUI.util.administration.helper.ConfigHelper.getConfig('org__DOT__cmdbuild__DOT__core__DOT__login_languages').then(function (config) {
            if (!me.destroyed) {
                me.loginLanguanges = config.length ? config.split(',') : [];
                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                me.doEnabledLanguages();
            }
        });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.localizations.button} view
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, eOpts) {
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.settingDisabled(false);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.localizations.button} view
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, eOpts) {
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        this.settingDisabled(true);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.localizations.button} view
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var activelanguagesArray = [];

        vm.get('activelanguages').getRange().forEach(function (item) {
            activelanguagesArray.push(item.get('code'));
        });

        var config = {
            'org__DOT__cmdbuild__DOT__core__DOT__language': vm.get('defaultlanguage'),
            'org__DOT__cmdbuild__DOT__core__DOT__languageprompt': vm.get('languageprompt'),
            'org__DOT__cmdbuild__DOT__core__DOT__enabled_languages': activelanguagesArray.join(','),
            'org__DOT__cmdbuild__DOT__core__DOT__login_languages': me.loginLanguanges.join(',')
        };
        CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(config, null, null, me).then(function () {
            if (!vm.destroyed) {
                button.setDisabled(false);
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            }
        });

        me.settingDisabled(true);
    },

    /**
     * @param {boolean} view
     */
    settingDisabled: function (view) {

        this._enabledItems.forEach(function (item) {
            item.setReadOnly(view);
        });
        this._loginItems.forEach(function (item) {
            item.setReadOnly(view);
        });
    },

    onstoreActiveLoaded: function () {
        var view = this.getView();
        var vm = this.getViewModel();
        var activeLanguagesStore = vm.getStore('activelanguages');
        var defaultLanguageCombo = view.lookup('defaultLanguageCombo');
        defaultLanguageCombo.bindStore(activeLanguagesStore);
    },

    onActiveLanugesDatachanged: function (storeData) {
        var me = this;
        var view = me.getView();
        var vm = me.getViewModel();

        var languages = [];
        var languagescheckboxGroup = view.lookup('loginlanguagescheckboxGroup');
        languagescheckboxGroup.removeAll();
        var activelanguages = me.loginLanguanges;
        Ext.Array.forEach(storeData.getRange(), function (record) {
            var lang = record.get('description');
            var code = record.get('code');
            if (Ext.Array.contains(activelanguages, code)) {

                record.set('loginactive', true);
            }
            var flag = '<img width="20px" style="vertical-align:middle;margin-right:5px" src="resources/images/flags/' + code + '.png" />';
            languages.push({
                boxLabel: flag + lang,
                language: lang,
                value: record.get('loginactive'),
                readOnly: vm.get('actions.view'),
                config: {
                    record: record
                },
                bind: {
                    disabled: Ext.String.format('{defaultlanguage == "{0}"}', code)
                },
                listeners: {
                    change: function (checkbox, newValue, oldValue, eOpts) {
                        var language = checkbox.config.record.get('code');
                        if (newValue) {
                            Ext.Array.push(me.loginLanguanges, language);
                        } else {
                            Ext.Array.remove(me.loginLanguanges, language);
                        }
                    }
                }
            });
        });

        this.sortLanguages(languages);
        this._loginItems = languagescheckboxGroup.add(languages);

    },
    /**
     * @param {array} storeRecords
     */
    createLanguagesArray: function (storeRecords) {
        var activelanguages = [];
        storeRecords.forEach(function (language) {
            activelanguages.push(language.get('code'));
        });
        return activelanguages;
    },

    privates: {
        loginLanguanges: [],


        doEnabledLanguages: function () {
            var me = this;
            var view = me.getView();
            var vm = me.getViewModel();
            var languagesStore = vm.getStore('languages');
            languagesStore.load({
                callback: function (languagesStoreRecords) {
                    var activeLanguagesStore = vm.getStore('activelanguages');
                    activeLanguagesStore.load({
                        callback: function (activeLanguagesStoreRecords) {
                            var languages = [];
                            var languagescheckboxGroup = view.lookup('languagescheckboxGroup');
                            var activelanguages = me.createLanguagesArray(activeLanguagesStoreRecords);
                            languagesStoreRecords.forEach(function (record) {
                                var lang = record.get('description');
                                var code = record.get('code');
                                if (Ext.Array.contains(activelanguages, code)) {
                                    record.set('active', true);
                                }
                                var flag = '<img width="20px" style="vertical-align:middle;margin-right:5px" src="resources/images/flags/' + code + '.png" />';
                                languages.push({
                                    boxLabel: flag + lang,
                                    language: lang,
                                    value: record.get('active'),
                                    readOnly: true,
                                    bind: {
                                        disabled: Ext.String.format('{defaultlanguage === "{0}"}', code)
                                    },
                                    config: {
                                        record: record
                                    },
                                    listeners: {
                                        change: function (checkbox, newValue, oldValue, eOpts) {
                                            var language = checkbox.config.record;
                                            if (newValue) {
                                                activeLanguagesStore.add(language);

                                            } else {
                                                activeLanguagesStore.remove(language);
                                                Ext.Array.remove(me.loginLanguanges, language.get('code'));
                                            }
                                        }
                                    }
                                });
                            });
                            me.sortLanguages(languages);
                            me._enabledItems = languagescheckboxGroup.add(languages);
                            me.onstoreActiveLoaded();
                        }
                    });
                }
            });
        },

        sortLanguages: function (languages) {
            Ext.Array.sort(languages, function (a, b) {
                var lanA = a.language.toLowerCase();
                var lanB = b.language.toLowerCase();
                if (lanA < lanB) {
                    return -1;
                }
                if (lanA > lanB) {
                    return 1;
                }
                return 0;
            });
        }
    }

});