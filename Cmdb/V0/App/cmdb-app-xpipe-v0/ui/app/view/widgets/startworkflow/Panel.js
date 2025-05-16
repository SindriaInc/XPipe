Ext.define('CMDBuildUI.view.widgets.startworkflow.Panel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.widgets.startworkflow.PanelController',
        'CMDBuildUI.view.widgets.startworkflow.PanelModel'
    ],

    mixins: [
        'CMDBuildUI.view.widgets.Mixin'
    ],

    alias: 'widget.widgets-startworkflow-panel',
    controller: 'widgets-startworkflow-panel',
    viewModel: {
        type: 'widgets-startworkflow-panel'
    },
    layout: 'fit'
});