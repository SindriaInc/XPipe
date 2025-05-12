Ext.define('CMDBuildUI.view.widgets.gotocard.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.widgets.gotocard.PanelController'
    ],

    alias: 'widget.widgets-gotocard-panel',
    controller: 'widgets-gotocard-panel',

    mixins: [
        'CMDBuildUI.view.widgets.Mixin'
    ]
});