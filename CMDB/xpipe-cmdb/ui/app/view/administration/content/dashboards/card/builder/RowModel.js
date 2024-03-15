Ext.define('CMDBuildUI.view.administration.content.dashboards.card.builder.RowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dashboards-card-builder-row',
    data: {
        name: 'CMDBuildUI',
        row: null
    },

    formulas: {},
    stores: {
        rows: {
            data: [{},{}]
        },
        classes: {
            type: 'chained',
            source: 'classes.Classes',
            autoDestroy: true
        }
    }

});
