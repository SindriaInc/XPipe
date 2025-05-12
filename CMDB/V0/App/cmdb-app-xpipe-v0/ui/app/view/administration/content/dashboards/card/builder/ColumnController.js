Ext.define('CMDBuildUI.view.administration.content.dashboards.card.builder.ColumnController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-card-builder-column',

    control: {
        '#': {
            itemcreated: 'onItemCreated',
            itemeditclick: 'onItemEditClick',
            itemopenclick: 'onItemOpenClick',
            itemdeleteclick: 'onItemDeleteClick',
            itemdisableclick: 'onItemEnableToggleClick',
            itemenableclick: 'onItemEnableToggleClick',
            itemcloneclick: 'onItemCloneClick'
        }
    },
    onItemCreated: function (record) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theChart = vm.get('theDashboard.charts').findRecord('_id', record.get('_id'));

        var config = {
            xtype: 'administration-content-dashboards-card-chart-form',
            viewModel: {
                data: {
                    theChart: theChart,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    owner: view.up('view-administration-content-dashboards-card'),
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                }
            }
        };
        this.openDetailWindow(config);
    },
    onItemEditClick: function (record, e, eOpts) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theChart = vm.get('theDashboard.charts').findRecord('_id', record.data);
        var config = {
            xtype: 'administration-content-dashboards-card-chart-form',
            viewModel: {
                data: {
                    owner: view.up('view-administration-content-dashboards-card'),
                    theChart: theChart,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
                }
            }
        };
        this.openDetailWindow(config);
        return false;

    },
    onItemOpenClick: function (record, e, eOpts, hideToolbar) {
        e.stopPropagation();
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theChart = vm.get('theDashboard.charts').findRecord('_id', record.data);
        var config = {
            xtype: 'administration-content-dashboards-card-chart-form',
            viewModel: {
                data: {
                    ownerColumn: view,
                    owner: view.up('view-administration-content-dashboards-card'),
                    theChart: theChart,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    hideToolbar: hideToolbar
                }
            }
        };

        this.openDetailWindow(config);
        return false;
    },
    onItemDeleteClick: function (record, e, eOpts) {
        e.stopPropagation();
        var me = this,
            view = me.getView(),
            vm = view.lookupViewModel(),
            theChart = vm.get('theDashboard.charts').findRecord('_id', record.data),
            theDashboard = vm.get('theDashboard'),
            charts = theDashboard.charts(),
            rows = vm.get('rows');

        charts.remove(theChart);
        rows.each(function (row) {
            Ext.Array.forEach(row.get('columns'), function (column) {
                if (column.charts && Ext.Array.contains(column.charts, theChart.getId())) {
                    Ext.Array.remove(column.charts, theChart.getId());
                }
            });
        });
        rows.fireEventArgs('datachanged', [rows]);
        return false;

    },

    onItemEnableToggleClick: function (record, e, eOpts) {
        e.stopPropagation();
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theChart = vm.get('theDashboard.charts').findRecord('_id', record.data),
            theDashboard = vm.get('theDashboard'),
            charts = theDashboard.charts(),
            chart = charts.getAt(charts.findBy(function (item) {
                return item.get('_id') === theChart.get('_id');
            })),
            rows = vm.get('rows');
        chart.set('active', !chart.get('active'));
        rows.fireEventArgs('datachanged', [rows]);
        return false;
    },

    onItemCloneClick: function (record, e, eOpts) {
        e.stopPropagation();
        var me = this,
            view = me.getView(),
            vm = view.lookupViewModel(),
            newChart = vm.get('theDashboard.charts').findRecord('_id', record.data).copyForClone();

        var gridStore = vm.get('rows').getRange();
        var columns = gridStore[view.getRowIndex()].columns || gridStore[view.getRowIndex()].get('columns');
        columns[view.getColIndex()].charts.push(newChart.get('_id'));
        view.up('view-administration-content-dashboards-card')
            .getViewModel()
            .get('theDashboard')
            .charts()
            .add(newChart);

        var config = {
            xtype: 'administration-content-dashboards-card-chart-form',
            viewModel: {
                data: {
                    owner: view.up('view-administration-content-dashboards-card'),
                    theChart: newChart,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    actions: {
                        edit: false,
                        view: false,
                        add: true
                    }
                }
            }
        };

        this.openDetailWindow(config);
        return false;
    },

    privates: {
        openDetailWindow: function (config, title) {
            if (!config) {
                return false;
            }            
            var vm = this.getViewModel(),
                container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            container.removeAll();
            container.addListener('beforeclose', function (panel) {
                var cancelBtn = panel.down('#cancelBtn');
                if(cancelBtn){
                    cancelBtn.fireEventArgs('click', [cancelBtn]);
                }
            });
            container.add(config);
        }
    }
});
