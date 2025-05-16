
Ext.define('CMDBuildUI.view.widgets.createreport.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.widgets.createreport.PanelController',
        'CMDBuildUI.view.widgets.createreport.PanelModel',

        'CMDBuildUI.view.reports.ContainerController'
    ],
    mixins: [
        'CMDBuildUI.view.widgets.Mixin'
    ],

    alias: 'widget.widgets-createreport-panel',
    controller: 'widgets-createreport-panel',
    viewModel: {
        type: 'widgets-createreport-panel'
    },

    layout: 'fit'

    /**
     * @cfg {String} theWidget.ReportCode
     * Is the code of the report to open.
     */

    /**
     * @cfg {Boolean} theWidget.ForcePDF
     * Force PDF format for this report.
     */

    /**
     * @cfg {Boolean} theWidget.ForceCSV
     * Force CSV format for this report.
     */

    /**
     * @cfg {String} theWidget.ReadOnlyAttributes
     * The list of read only attributes separted by comma. E.g. `"Attribute1,Attribute2"`.
     */
});
