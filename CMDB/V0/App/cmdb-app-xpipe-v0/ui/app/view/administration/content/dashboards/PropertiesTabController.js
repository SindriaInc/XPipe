Ext.define('CMDBuildUI.view.administration.content.dashboards.PropertiesTabController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-propertiestab',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            closed: 'onClose'
        }
    },
    onClose: function () {
        var vm = this.getViewModel();
        if (vm.get('dashboardId') !== '_new') {
            this.redirectTo(Ext.History.getToken(), true);
        } else {
            this.redirectTo('administration/dashboards', true);
        }
    },
    onBeforeRender: function (view) {
        var me = this,
            vm = me.getViewModel();
        if (view.getShowCard()) {
            var config = {
                xtype: 'view-administration-content-dashboards-card',
                viewModel: {
                    data: {
                        hideForm: vm.get('hideForm')
                    }
                }
            };
            view.add(config);
        } else {
            view.add({
                xtype: 'administration-content-dashboards-grid',
                region: 'center',
                bind: {
                    hidden: '{isGridHidden}'
                }
            });
        }

    }

});
