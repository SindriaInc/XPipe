Ext.define('CMDBuildUI.view.management.ContentModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.management-content',

    data: {
        activeMapTabPanel: 0,
        activeView: 'grid-list',
        actualZoom: undefined,
        mapCenter: [0, 0]
    }
});
