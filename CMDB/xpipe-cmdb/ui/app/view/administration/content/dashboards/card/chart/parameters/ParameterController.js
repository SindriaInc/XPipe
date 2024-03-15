Ext.define('CMDBuildUI.view.administration.content.dashboards.card.chart.parameters.ParameterController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-card-chart-parameters-parameter',

    /**
     * On parameter label description translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onParameterNameTranslateClick: function (event, button, eOpts) {

        var vm = this.getViewModel();
        var dashboardName = vm.get('dashboardName');
        var currentIndex = vm.get('parameterIndex');
        var localizationHelper = CMDBuildUI.util.administration.helper.LocalizationHelper;
        var chartId = vm.get('chartId');        
        var translationCode = localizationHelper.getLocaleKeyOfDashboardChartParameterName(dashboardName, chartId, currentIndex);
        var vmLocaleObject = Ext.String.format('theChartParameterDescription_{0}_{1}', chartId, currentIndex);
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, CMDBuildUI.util.administration.helper.FormHelper.formActions.add, vmLocaleObject, vm.getParent().get('owner').getViewModel(), true);
    }


});