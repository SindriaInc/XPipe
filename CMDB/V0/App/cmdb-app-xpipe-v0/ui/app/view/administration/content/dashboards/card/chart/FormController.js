Ext.define('CMDBuildUI.view.administration.content.dashboards.card.chart.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-card-chart-form',
    control: {
        '#dataSourceFuncktionName_input': {
            change: 'onDataSourceNameChange'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            theChart = vm.get('theChart');

        var parameters = [];
        theChart.dataSourceParameters().each(function (parameter) {
            if (!Ext.isEmpty(parameter.get('fieldType')) || !Ext.isEmpty(parameter.get('type'))) {
                parameters.push(parameter.getData());
            }
        });
        // clean hidden data
        Ext.Array.forEach(Ext.Object.getKeys(theChart.getData()), function (key) {
            if (vm.get(Ext.String.format('hiddenTypeProperty.{0}', key))) {
                delete theChart.data[key];
            }
        });
        theChart.set('dataSourceParameters', parameters);
        vm.get('owner').getController().onRowsStoreDatachanged();
        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
    },

    /**
     * 
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var ownerColumn = vm.get('ownerColumn');
        if (ownerColumn) {
            var chartId = vm.get('theChart._id');
            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
            ownerColumn.fireEventArgs('itemeditclick', [{
                data: chartId
            }, e, eOpts]);
        }
    },

    /**
     * 
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var ownerColumn = vm.get('ownerColumn');
        if (ownerColumn) {
            var chartId = vm.get('theChart._id');
            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
            ownerColumn.fireEventArgs('itemdeleteclick', [{
                data: chartId
            }, e, eOpts]);
        }
    },

    /**
     * 
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCloneBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var ownerColumn = vm.get('ownerColumn');
        if (ownerColumn) {
            var chartId = vm.get('theChart._id');
            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
            ownerColumn.fireEventArgs('itemcloneclick', [{
                data: chartId
            }, e, eOpts]);
        }
    },

    /**
     * 
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var ownerColumn = vm.get('ownerColumn');
        if (ownerColumn) {
            var chartId = vm.get('theChart._id');
            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
            ownerColumn.fireEventArgs('itemdisableclick', [{
                data: chartId
            }, e, eOpts]);
        }
    },

    /**
     * On cancel form button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            owner = vm.get('owner'),
            ownerVm = owner.getViewModel(),
            theDashboard = ownerVm.get('theDashboard'),
            theChart = vm.get('theChart'),
            charts = theDashboard.charts(),
            rows = ownerVm.get('rows');

        this.undoLocales(theDashboard, theChart, vm.get('owner').getViewModel());

        theChart.reject();
        if (vm.get('actions.add')) {
            charts.remove(theChart);
            rows.each(function (row) {
                Ext.Array.forEach(row.get('columns'), function (column) {
                    if (column.charts && Ext.Array.contains(column.charts, theChart.getId())) {
                        Ext.Array.remove(column.charts, theChart.getId());
                    }
                });
            });
            rows.fireEventArgs('datachanged', [rows]);
        }
        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
    },

    /**
     * 
     * @param {Ext.form.field.Field} combo 
     * @param {String} newValue 
     * @param {string} oldValue 
     */
    onDataSourceNameChange: function (combo, newValue, oldValue) {
        var me = this,
            view = me.getView(),
            vm = view.lookupViewModel(),
            owner = vm.get('owner'),
            ownerVm = owner.getViewModel(),
            theDashboard = ownerVm.get('theDashboard'),
            theChart = vm.get('theChart'),
            parameters = Ext.copy(theChart.dataSourceParameters().getData());

        combo.forceSelection = true;
        if (combo.getStore().findExact('name', newValue) > -1) {
            if (oldValue) {
                if (parameters.length) {
                    CMDBuildUI.util.Msg.confirm(
                        CMDBuildUI.locales.Locales.administration.common.messages.attention,
                        CMDBuildUI.locales.Locales.administration.dashboards.deletechartparametertranslations,
                        function (btnText) {
                            me.undoParametersLocalizations(theDashboard, theChart, parameters, vm, btnText === "yes");
                            theChart.dataSourceParameters().removeAll();
                            me.generateParameterBySourceType(newValue);
                        }, me);
                } else {
                    theChart.dataSourceParameters().removeAll();
                }
            } else {
                me.generateParameterBySourceType(newValue);
            }
        }

    },

    /**
     * On category axis title translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateChartDescriptionClick: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var ownerVm = vm.get('owner').getViewModel();
        var dashboardName = ownerVm.get('theDashboard.name');
        var chartId = vm.get('theChart._id');
        var translationCode = this.localizationHelper.getLocaleKeyOfDashboardChartDescription(dashboardName, chartId);
        var vmLocaleObject = Ext.String.format('theChartDescription_{0}', chartId);
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, CMDBuildUI.util.administration.helper.FormHelper.formActions.add, vmLocaleObject, ownerVm, true);
    },

    /**
     * On category axis title translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateCategoryAxisTitleClick: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var ownerVm = vm.get('owner').getViewModel();
        var dashboardName = ownerVm.get('theDashboard.name');
        var chartId = vm.get('theChart._id');
        var translationCode = this.localizationHelper.getLocaleKeyOfDashboardChartCategoryAxis(dashboardName, chartId);
        var vmLocaleObject = Ext.String.format('theChartCategoryAxisTitle_{0}', chartId);
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, CMDBuildUI.util.administration.helper.FormHelper.formActions.add, vmLocaleObject, ownerVm, true);
    },

    /**
     * On value axis label translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateValueAxisLabelClick: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var ownerVm = vm.get('owner').getViewModel();
        var dashboardName = ownerVm.get('theDashboard.name');
        var chartId = vm.get('theChart._id');
        var translationCode = this.localizationHelper.getLocaleKeyOfDashboardChartValueAxis(dashboardName, chartId);
        var vmLocaleObject = Ext.String.format('theChartValueAxisLabel_{0}', chartId);
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, CMDBuildUI.util.administration.helper.FormHelper.formActions.add, vmLocaleObject, ownerVm, true);
    },

    /**
     * On chart label field translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateChartLabelFieldClick: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var ownerVm = vm.get('owner').getViewModel();
        var dashboardName = ownerVm.get('theDashboard.name');
        var chartId = vm.get('theChart._id');
        var translationCode = this.localizationHelper.getLocaleKeyOfDashboardChartLabelField(dashboardName, chartId);
        var vmLocaleObject = Ext.String.format('theChartLabelField_{0}', chartId);
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, CMDBuildUI.util.administration.helper.FormHelper.formActions.add, vmLocaleObject, ownerVm, true);
    },
    privates: {
        formHelper: CMDBuildUI.view.administration.content.dashboards.card.chart.FormHelper,

        localizationHelper: CMDBuildUI.util.administration.helper.LocalizationHelper,

        generateFunctionParameterFieldsets: function (newValue) {
            var me = this,
                vm = me.getViewModel(),
                theChart = vm.get('theChart'),
                container = me.getView().down('#functionparameterscontiner');
            container.removeAll();

            if (newValue) {
                vm.get('functionsStore').model.load(newValue).getParameters().then(function (parameters) {
                    parameters.each(function (parameter, index) {
                        me.createDataSourceIfNotExist(theChart, parameter);
                        container.add({
                            xtype: 'administration-dashboards-parameters-parameter',
                            parameter: me.getParameter(parameter.get('name')),
                            viewModel: {
                                data: {
                                    theParameter: me.getParameter(parameter.get('name')),
                                    parameterIndex: index,
                                    chartId: theChart.get('_id'),
                                    dashboardName: vm.get('owner').getViewModel().get('theDashboard.name')
                                }
                            }
                        });
                    });
                });
            }

        },

        undoLocales: function (theDashboard, theChart, vm) {
            this.undoParametersLocalizations(theDashboard, theChart, theChart.dataSourceParameters().getData(), vm);

            var chartDescriptionVmObject = Ext.String.format('theChartDescription_{0}', theChart.get('_id'));
            if (vm.get(chartDescriptionVmObject)) {
                vm.set(chartDescriptionVmObject, undefined);
            }

            var categoryAxisTitleVmObject = Ext.String.format('theChartCategoryAxisTitle_{0}', theChart.get('_id'));
            if (vm.get(categoryAxisTitleVmObject)) {
                vm.set(categoryAxisTitleVmObject, undefined);
            }

            var valueAxisLabelVmObject = Ext.String.format('theChartValueAxisLabel_{0}', theChart.get('_id'));
            if (vm.get(valueAxisLabelVmObject)) {
                vm.set(valueAxisLabelVmObject, undefined);
            }

            var labelFieldVmObject = Ext.String.format('theChartLabelField_{0}', theChart.get('_id'));
            if (vm.get(labelFieldVmObject)) {
                vm.set(labelFieldVmObject, undefined);
            }
        },

        undoParametersLocalizations: function (theDashboard, theChart, parameters, vm, forceDeletion) {
            var me = this;
            Ext.Array.forEach(parameters.items, function (parameter, index) {
                var parameterDescriptionLocaleVmObject = Ext.String.format('theChartParameterDescription_{0}_{1}', theChart.get('_id'), index);
                if (forceDeletion) {
                    CMDBuildUI.util.Ajax.setActionId('delete-chartparameter-locales');
                    var localeId = me.localizationHelper.getLocaleKeyOfDashboardChartParameterName(theDashboard.get('name'), theChart.get('_id'), index);
                    Ext.Ajax.request({
                        url: Ext.String.format(
                            '{0}/translations/{1}',
                            CMDBuildUI.util.Config.baseUrl,
                            localeId
                        ),
                        method: 'DELETE'
                    });
                }
                if (vm.get(parameterDescriptionLocaleVmObject)) {
                    vm.set(parameterDescriptionLocaleVmObject, undefined);
                }
            });

        },

        getParameter: function (parameter) {
            var me = this,
                vm = me.getViewModel(),
                theChart = vm.get('theChart');
            return theChart.dataSourceParameters().findRecord('name', parameter);
        },

        generateParameterBySourceType: function (newValue) {
            if (!newValue) {
                CMDBuildUI.util.Logger.log("missing function arguments (generateParameterBySourceType)", CMDBuildUI.util.Logger.levels.error);
                return;
            }
            var me = this,
                view = me.getView(),
                vm = view.lookupViewModel(),
                theChart = vm.get('theChart');

            if (theChart.get('dataSourceType') === CMDBuildUI.model.dashboards.Chart.dataSourceTypes.funktion) {
                me.generateFunctionParameterFieldsets(newValue);
                if (newValue) {
                    var funcktion = vm.getStore('functionsStore').findRecord('name', newValue);
                    if (funcktion) {
                        funcktion.getAttributes().then(function (attributes) {
                            vm.bind({
                                bindTo: '{getAllFunctionAttributes}'
                            }, function () {
                                view.down('#valueAxisFields').setBind({
                                    value: '{theChart.valueAxisFields}'
                                });
                            });
                            vm.set('getAllFunctionAttributes', attributes.getRange());
                        });
                    }
                }
            }
        },
        createDataSourceIfNotExist: function (theChart, parameter) {
            var me = this;
            if (!me.getParameter(parameter.get('name'))) {
                theChart.dataSourceParameters().add({
                    name: parameter.get('name'),
                    type: parameter.get('type').toLowerCase(),
                    defaultValue: parameter.get('type').toLowerCase() === 'boolean' ? "null" : ''
                });
            }
        }
    }
});