Ext.define('CMDBuildUI.view.administration.content.dashboards.card.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.view-administration-content-dashboards-card',
    data: {
        hideForm: false,
        // actions: {
        //     view: true,
        //     edit: false,
        //     add: false
        // },
        rowsData: [],
        chartsData: []
    },

    formulas: {
        rowsDataManager: {
            get: function () {
                return [];
            }
        }
        // action: {
        //     bind: '{action}',
        //     get: function (action) {
        //         switch (action) {
        //             case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
        //                 this.set('formModeCls', 'formmode-edit');
        //                 return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
        //             case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
        //                 this.set('formModeCls', 'formmode-add');
        //                 return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
        //             default:
        //                 this.set('formModeCls', 'formmode-view');
        //                 return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;

        //         }
        //     },
        //     set: function (value) {
        //         this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        //         this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        //         this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
        //     }
        // }
    },

    stores: {
        // get all chart types
        chartsStore: {
            proxy: {
                type: 'memory'
            },
            data: CMDBuildUI.model.dashboards.Chart.getChartTypes()
        },
        rows: {
            proxy: {
                type: 'memory'
            },
            data: '{rowsData}',
            listeners: {
                datachanged: 'onRowsStoreDatachanged'
            }
        },

        charts: {
            proxy: {
                type: '{memory}'
            },
            data: '{chartsData}',
            listeners: {
                datachanged: 'onRowsStoreDatachanged'
            }
        }
    }
});