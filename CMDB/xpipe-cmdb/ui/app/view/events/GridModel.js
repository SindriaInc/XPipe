Ext.define('CMDBuildUI.view.events.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.events-grid',
    data: {
        'events-grid': {
            selectedId: null,
            eventsStore: null,
            selection: null
        }
    }
});
