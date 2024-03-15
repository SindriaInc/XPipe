Ext.define('CMDBuildUI.view.administration.content.dashboards.card.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.view-administration-content-dashboards-card',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#closeBtn': {
            click: 'onCancelBtnClick'
        },
        '#addrowBtn': {
            click: 'onAddrowBtnClick'
        }
    },
    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        vm.bind({
            bindTo: {
                theDashboard: '{theDashboard}'
            }
        }, function (data) {
            vm.set('chartsData', data.theDashboard.get('charts'));
            vm.set('rowsData', data.theDashboard.get('layout').rows);
            // construct here the form        
            view.add(CMDBuildUI.view.administration.content.dashboards.card.FormHelper.getGeneralProperties('display', view));
            var layoutGrid = CMDBuildUI.view.administration.content.dashboards.card.FormHelper.getLayout('display', view);
            view.add(layoutGrid);

            view.down('#layoutcontainer').add([CMDBuildUI.util.administration.helper.GridHelper.getDragAndDropReorderGrid([{
                xtype: 'widgetcolumn',
                flex: 1,
                widget: {
                    xtype: 'administration-content-dashboards-card-builder-row'
                },
                plugins: [],
                variableRowHeight: true
            }])]);
        });
    },
    onAfterRender: function (view) {


    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.Utilities.showLoader(true);
        var me = this,
            form = me.getView(),
            vm = me.getViewModel(),
            grid = vm.get('grid'),
            theDashboard = vm.get('theDashboard');
        // set charts        
        var charts = [];
        theDashboard.charts().each(function (chart) {
            var _chart = chart.getData();
            var dataSourceParameters = [];
            delete chart.data.descriptionWithName;
            if (chart.dataSourceParameters) {
                Ext.Array.forEach(chart.dataSourceParameters().getRange(), function (parameter) {
                    if (Ext.isEmpty(parameter.get('fieldType')) && Ext.isEmpty(parameter.get('type'))) {
                        chart.dataSourceParameters().remove(parameter);
                    } else {
                        delete parameter.data._id;
                        delete parameter.data._name_translation;
                        dataSourceParameters.push(parameter.getData());
                    }
                });
            }
            _chart.dataSourceParameters = dataSourceParameters;
            charts.push(_chart);
        });

        var rows = [];
        vm.get('rows').each(function (row) {
            rows.push(row.getData());
        });
        theDashboard.set('layout', {
            rows: rows
        });
        theDashboard.set('charts', charts);
        theDashboard.save({
            success: function (record, operation) {
                me.saveDashboardDescriptionLocales(vm, record);
                me.saveDashboardChartLocalizedFieldLocales(vm, record);
                if (grid) {
                    grid.fireEventArgs('itemupdated', [record]);
                    form.container.component.fireEvent("closed");
                    CMDBuildUI.util.Utilities.showLoader(false);
                } else if (form.up('administration-maincontainer')) {
                    var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDashboardUrl(record.getId());
                    if (vm.get('action') === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
                        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                            function () {
                                CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                                CMDBuildUI.util.Utilities.showLoader(false);
                            }
                        );
                    } else {
                        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                        CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, record.get('description'), me);
                        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                        CMDBuildUI.util.Utilities.showLoader(false);
                    }
                }
            },
            failure: function () {
                CMDBuildUI.util.Utilities.showLoader(false);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = this.getViewModel();
        vm.get("theDashboard").reject(); // discard changes
        view.container.component.fireEvent("closed");
    },

    /**
     * On description translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickDescription: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theDashboard = vm.get('theDashboard');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardDescription(vm.get('actions.edit') ? theDashboard.get('name') : '.');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theDescriptionTranslation', vm.getParent());
    },

    /**
     * On extended description translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickExtDescription: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theDashboard = vm.get('theDashboard');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardExtDescription(vm.get('actions.edit') ? theDashboard.get('name') : '.');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theExtDescriptionTranslation', vm.getParent());
    },

    onEditBtnClick: function (button, event) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theDashboard = vm.get('theDashboard'),
            viewModel = {
                data: {
                    grid: vm.get('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                },
                links: {
                    theDashboard: {
                        type: 'CMDBuildUI.model.dashboards.Dashboard',
                        id: theDashboard.get('_id')
                    }
                }
            };
        view.up('administration-content-dashboards-tabpanel').getViewModel().toggleEnableTabs(0);
        var container = view.container.component;
        container.removeAll();
        container.add({
            xtype: 'view-administration-content-dashboards-card',
            viewModel: viewModel,
            data: {}
        });
    },

    onDeleteBtnClick: function (button, event) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            grid = vm.get('grid'),
            theDashboard = vm.get('theDashboard');
        Ext.Msg.alwaysOnTop = true;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Utilities.showLoader(true);
                    CMDBuildUI.util.Ajax.setActionId('delete-dashboard');
                    theDashboard.erase({
                        success: function (record, operation) {
                            if (grid) {
                                grid.fireEventArgs('reload', [record, 'delete']);
                            }
                            if (view.source) {
                                view.source.container.component.remove(view.source);
                            } else if (view.up('administration-maincontainer')) {
                                var apiHelper = CMDBuildUI.util.administration.helper.ApiHelper.client,
                                    nextUrl = apiHelper.getDashboardUrl(),
                                    recordUrl = apiHelper.getDashboardUrl(record.getId());

                                CMDBuildUI.util.Stores.loadDashboardsStore();
                                CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', recordUrl, nextUrl, me);
                            }
                            CMDBuildUI.util.Utilities.showLoader(false);
                            view.container.component.fireEvent("closed");
                        },
                        failure: function () {
                            CMDBuildUI.util.Utilities.showLoader(false);
                        }
                    });
                }
            }, this);
    },


    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */

    onToggleBtnClick: function (button, e, eOpts) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theDashboard = vm.get('theDashboard');
        theDashboard.set('active', !theDashboard.get('active'));

        me.onSaveBtnClick();
    },

    saveDashboardDescriptionLocales: function (vm, record) {
        if (vm.get('actions.add')) {
            var translations = [
                'theDescriptionTranslation'
            ];
            var keyFunction = [
                'getLocaleKeyOfDashboardDescription'
            ];
            Ext.Array.forEach(translations, function (item, index) {
                if (vm.get(item)) {
                    var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper[keyFunction[index]](record.get('name'));
                    vm.get(item).crudState = 'U';
                    vm.get(item).crudStateWas = 'U';
                    vm.get(item).phantom = false;
                    vm.get(item).set('_id', translationCode);
                    vm.get(item).save({
                        success: function (_translations, operation) {
                            CMDBuildUI.util.Logger.log(item + " localization was saved", CMDBuildUI.util.Logger.levels.debug);
                        }
                    });
                }
            });
        }
    },

    saveDashboardChartLocalizedFieldLocales: function (vm, record) {
        var localizationHelper = CMDBuildUI.util.administration.helper.LocalizationHelper;
        record.charts().each(function (chart) {
            chart.dataSourceParameters().each(function (parameter, index) {
                var parameterDescriptionLocaleVmObject = Ext.String.format('theChartParameterDescription_{0}_{1}', chart.get('_id'), index);
                if (vm.get(parameterDescriptionLocaleVmObject)) {
                    var parameterDescriptionTranslationCode = localizationHelper.getLocaleKeyOfDashboardChartParameterName(record.get('name'), chart.get('_id'), index);
                    vm.set(Ext.String.format('{0}._id', parameterDescriptionLocaleVmObject), parameterDescriptionTranslationCode);
                    vm.get(parameterDescriptionLocaleVmObject).save();
                }
            });

            var chartDescritpionVmObject = Ext.String.format('theChartDescription_{0}', chart.get('_id'));
            if (vm.get(chartDescritpionVmObject)) {
                var chartDescritpionTranslationCode = localizationHelper.getLocaleKeyOfDashboardChartDescription(record.get('name'), chart.get('_id'));
                vm.set(Ext.String.format('{0}._id', chartDescritpionVmObject), chartDescritpionTranslationCode);
                vm.get(chartDescritpionVmObject).save();
            }

            var categoryAxisTitleVmObject = Ext.String.format('theChartCategoryAxisTitle_{0}', chart.get('_id'));
            if (vm.get(categoryAxisTitleVmObject)) {
                var categoryAxisTitleTranslationCode = localizationHelper.getLocaleKeyOfDashboardChartCategoryAxis(record.get('name'), chart.get('_id'));
                vm.set(Ext.String.format('{0}._id', categoryAxisTitleVmObject), categoryAxisTitleTranslationCode);
                vm.get(categoryAxisTitleVmObject).save();
            }

            var valueAxisLabelVmObject = Ext.String.format('theChartValueAxisLabel_{0}', chart.get('_id'));
            if (vm.get(valueAxisLabelVmObject)) {
                var valueAxisLabelTranslationCode = localizationHelper.getLocaleKeyOfDashboardChartValueAxis(record.get('name'), chart.get('_id'));
                vm.set(Ext.String.format('{0}._id', valueAxisLabelVmObject), valueAxisLabelTranslationCode);
                vm.get(valueAxisLabelVmObject).save();
            }
            var labelFieldVmObject = Ext.String.format('theChartLabelField_{0}', chart.get('_id'));
            if (vm.get(labelFieldVmObject)) {
                var labelFieldTranslationCode = localizationHelper.getLocaleKeyOfDashboardChartLabelField(record.get('name'), chart.get('_id'));
                vm.set(Ext.String.format('{0}._id', labelFieldVmObject), labelFieldTranslationCode);
                vm.get(labelFieldVmObject).save();
            }
        });
    },

    onAddrowBtnClick: function (button, event, eOpts) {
        // add row with 2 empty columns
        var view = this.getView().down('#layoutcontainer');
        view.lookupViewModel().get('theDashboard.layout');
        var store = view.down('grid').getStore();
        store.add({
            columns: [{
                width: 0.5,
                charts: []
            }, {
                width: 0.5,
                charts: []
            }]
        });
    },

    onRowsStoreDatachanged: function (store) {
        Ext.suspendLayouts();
        var view = this.getView().down('#layoutcontainer');
        view.removeAll();
        view.add([CMDBuildUI.util.administration.helper.GridHelper.getDragAndDropReorderGrid([{
            xtype: 'widgetcolumn',
            flex: 1,
            widget: {
                xtype: 'administration-content-dashboards-card-builder-row'
            },
            plugins: [],
            variableRowHeight: true
        }])]);
        Ext.resumeLayouts();
    },

    moveUp: function (view, rowIndex, colIndex, item, e, record, row) {
        var store = record.store;
        var current = store.findRecord('id', record.get('id'));
        var currentIndex = store.findExact('id', record.get('id'), 0);
        store.remove(current, true);
        store.insert(currentIndex - 1, current);
    },

    moveDown: function (view, rowIndex, colIndex, item, e, record, row) {
        var store = record.store;
        var current = store.findRecord('id', record.get('id'));
        var currentIndex = store.findExact('id', record.get('id'), 0);
        store.remove(current, true);
        store.insert(currentIndex + 1, current);
    },

    deleteRow: function (view, rowIndex, colIndex, item, e, record, row) {
        var store = record.store;
        store.removeAt(rowIndex);
        this.onRowsStoreDatachanged();
    },

    addColumn: function (view, rowIndex, colIndex, item, e, record, row) {
        if (record.isModel) {
            var columns = record.get('columns');
            if (columns.length < 4) {
                columns.push({
                    charts: []
                });
                record.set('width', undefined);
                Ext.Array.forEach(columns, function (column) {
                    column.width = undefined;
                });
                record.set('columns', columns);
                this.onRowsStoreDatachanged();
            }
        }
    },

    removeColumn: function (view, rowIndex, colIndex, item, e, record, row) {
        if (record.isModel) {

            var columns = record.get('columns');
            if (columns.length > 1) {
                Ext.Array.forEach(columns, function (column) {
                    column.width = undefined;
                });
                var wasRemoved = false;
                for (var i = 0; i < columns.length; i++) {
                    var isEmpty = !columns[i].charts.length;
                    if (isEmpty) {
                        Ext.Array.removeAt(columns, i);
                        wasRemoved = true;
                        break;
                    }
                }
                if (!wasRemoved) {
                    CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.administration.dashboards.unabletoremovenonemptycolumn);
                    return;
                }
                record.set('width', undefined);
                record.set('columns', columns);
                this.onRowsStoreDatachanged();
            }
        }
    },

    onDrop: function (node, data, overModel, dropPosition, eOpts) {
        data.view.refresh();
        this.view.ownerGrid.refreshIndex(data.view.getStore());
    },

    columnsSizeBtnClick: function (button) {
        var me = button.up().ctrl;
        var values = button.value;
        var record = button.up().record;
        Ext.Array.forEach(record.get('columns'), function (item, index) {
            item.width = values[index];
        });
        me.onRowsStoreDatachanged();
    }
});