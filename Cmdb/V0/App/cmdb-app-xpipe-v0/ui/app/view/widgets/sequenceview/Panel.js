Ext.define('CMDBuildUI.view.widgets.sequenceview.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.widgets.sequenceview.PanelController'
    ],

    alias: 'widget.widgets-sequenceview-panel',
    controller: 'widgets-sequenceview-panel',
    viewModel: {},

    mixins: [
        'CMDBuildUI.view.widgets.Mixin'
    ],

    layout: "fit",
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    fbar: [{
        xtype: 'button',
        ui: 'secondary-action',
        itemId: 'closebtn',
        text: CMDBuildUI.locales.Locales.common.actions.close,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.close'
        },
        autoEl: {
            'data-testid': 'widgets-sequenceview-close'
        }
    }]
});