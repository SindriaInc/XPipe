Ext.define('CMDBuildUI.view.administration.content.localizations.localization.tabitems.CommonGridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-localization-tabitems-commongrid',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        this.getViewModel().set('section', view.getSection());
        Ext.asap(function () {
            CMDBuildUI.util.Utilities.showLoader(true, view);
        });
    },

    onCancelBtnClick: function (button, e, eOpts) {
        var grid = this.getView();
        var vm = this.getViewModel();
        vm.set('actions.view', true);
        this.getView().getColumns().forEach(function (column) {
            if (!column.locked) {
                column.setEditor(false);
            }
        });
        vm.set('actions.view', true);
        vm.set('actions.edit', false);
        grid.getStore().reload();
        vm.getParent().toggleEnableTabs();
    },
    onSaveBtnClick: function (button, e, eOpts) {
        var grid = this.getView();
        var vm = this.getViewModel();
        vm.set('actions.view', true);
        vm.set('actions.edit', false);
        vm.getParent().toggleEnableTabs();
        grid.getColumns().forEach(function (column) {
            if (!column.locked) {
                column.setEditor(false);
            }
        });

        var modifiedRecords = vm.get('localizationsStore').getModifiedRecords();
        var requestsCount = 0;
        modifiedRecords.forEach(function (record) {
            var data = {};
            var languges = record.get('values');
            Ext.Object.each(languges, function (key, value, myself) {

                data[key] = value;
            });
            var code = record.get('code');
            requestsCount++;
            Ext.Ajax.request({
                url: Ext.String.format('{0}/translations/{1}', CMDBuildUI.util.Config.baseUrl, code),
                method: 'PUT',
                jsonData: data,
                callback: function () {
                    requestsCount--;
                    if (requestsCount === 0) {
                        grid.getStore().reload();
                    }
                }
            });
        });
    },


    onstoreLoaded: function (store, records) {
        var grid = this.getView();
        var vm = this.getViewModel();

        var languagesStore = vm.get('languages');
        if (languagesStore.isLoaded()) {

            var languageRecords = languagesStore.getRange();

            var columns = [{
                text: CMDBuildUI.locales.Locales.administration.localizations.element,
                dataIndex: 'element',
                align: 'left',
                locked: true,
                width: 150,
                minWidth: 150
            }, {
                text: CMDBuildUI.locales.Locales.administration.localizations.type,
                dataIndex: 'type',
                align: 'left',
                locked: true,
                width: 150,
                minWidth: 150
            }, {
                text: CMDBuildUI.locales.Locales.administration.common.labels.code,
                dataIndex: 'code',
                align: 'left',
                locked: true,
                hidden: true,
                width: 150,
                minWidth: 150
            }, {
                text: CMDBuildUI.locales.Locales.administration.localizations.defaulttranslation,
                dataIndex: 'default',
                align: 'left',
                locked: true,
                width: 150,
                minWidth: 150
            }];

            if (languageRecords.length > 4) {
                grid.forceFit = false;
            }
            languageRecords.forEach(function (record) {
                var lang = record.get('description');
                var code = record.get('code');
                var flag = '<img width="20px" style="vertical-align:middle;margin-right:5px" src="resources/images/flags/' + code + '.png" />';
                var minWidth = ((grid.getWidth() - 600) / languageRecords.length) - (languageRecords.length + 6);
                if (minWidth < 150) {
                    minWidth = 150;
                }
                columns.push({
                    text: flag + lang,
                    dataIndex: code,
                    align: 'left',
                    locked: false,
                    width: languageRecords.length > 4 ? 150 : minWidth
                });
            });                        
            grid.reconfigure(store, columns);
            CMDBuildUI.util.Utilities.showLoader(false, grid);
        }
    },

    editedCell: function (editor, context, eOpts) {
        var me = this;
        var field = context.field;
        var modvalue = context.value;
        var store = me.getViewModel().get('localizationsStore');
        var key = context.record.get('code');

        var res = store.findRecord('code', key);
        if (res && res.get('values')[field] !== modvalue) {
            res.crudState = 'U';
            res.dirty = true;
            res.get('values')[field] = modvalue;
        }
    }

});