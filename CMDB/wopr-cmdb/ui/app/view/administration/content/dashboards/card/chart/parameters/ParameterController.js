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
        const vm = this.getViewModel();
        const currentIndex = vm.get('parameterIndex');
        const chartId = vm.get('chartId');
        const translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardChartParameterName(vm.get('dashboardName'), chartId, currentIndex);
        const vmLocaleObject = Ext.String.format('theChartParameterDescription_{0}_{1}', chartId.replaceAll('-', '_'), currentIndex);
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), vmLocaleObject, vm.getParent().get('owner').getViewModel(), true);
    }
});