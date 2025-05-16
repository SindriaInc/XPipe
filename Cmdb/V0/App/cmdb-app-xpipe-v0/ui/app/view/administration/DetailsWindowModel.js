Ext.define('CMDBuildUI.view.administration.DetailsWindowModel', {
    extend: 'CMDBuildUI.view.management.DetailsWindowModel',
    alias: 'viewmodel.administration-detailswindow',

    data: {
        titledata: {
            action: null,
            type: null,
            item: null
        },
        title: null
    }

});