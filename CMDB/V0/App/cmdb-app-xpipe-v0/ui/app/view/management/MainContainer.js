
Ext.define('CMDBuildUI.view.management.MainContainer', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.management.MainContainerController',
        'CMDBuildUI.view.management.MainContainerModel',

        'CMDBuildUI.view.management.Content'
    ],

    xtype: 'management-maincontainer',
    controller: 'management-maincontainer',
    viewModel: {
        type: 'management-maincontainer'
    },
    layout: 'border',

    items: [{
        xtype: 'management-navigation-container',
        split: true, // enable resizing
        region: 'west',
        collapsible: true // make collapsible
    }, {
        region: 'center', // center region is required, no width/height specified
        xtype: 'management-content'
    }]

});
