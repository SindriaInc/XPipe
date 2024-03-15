Ext.define('CMDBuildUI.view.administration.home.widgets.modelsstats.ModelsStatsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-home-widgets-modelsstats-modelsstats',
    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#addModelTool': {
            click: 'onAddModelToolClick'
        }
    },

    onAfterRender: function (view) {
        var vm = this.getViewModel();
        vm.bind({
            bindTo: '{showLoader}'
        }, function (showLoader) {
            CMDBuildUI.util.Utilities.showLoader(showLoader, view);
        });
    },

    onAddModelToolClick: function (tool, e, owner) {
        var me = this;
        var menu = Ext.create('Ext.menu.Menu', {
            autoShow: true,
            items: [{
                text: CMDBuildUI.locales.Locales.administration.classes.toolbar.addClassBtn.text,
                iconCls: 'x-fa fa-file-text-o',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/classes');
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.processes.toolbar.addProcessBtn.text,
                iconCls: 'x-fa fa-cog',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/processes');
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.domains.texts.adddomain,
                iconCls: 'x-fa fa-table',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/domains');
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.views.addview,
                iconCls: 'x-fa fa-table',
                height: 32,
                menu: [{
                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromfilter,
                    iconCls: 'x-fa fa-table',
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/views/_new/FILTER/true');
                        }
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromjoin,
                    iconCls: 'x-fa fa-table',
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/joinviews_empty/true');
                        }
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromsql,
                    iconCls: 'x-fa fa-table',
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/views/_new/SQL/true');
                        }
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromschedule,
                    iconCls: 'x-fa fa-table',
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/views/_new/CALENDAR/true');
                        }
                    }
                }]
            }, {
                text: CMDBuildUI.locales.Locales.administration.reports.texts.addreport,
                iconCls: 'x-fa fa-files-o',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/reports/_new');
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.dashboards.adddashboard,
                iconCls: 'x-fa fa-area-chart',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/dashboards/_new');
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.custompages.texts.addcustompage,
                iconCls: 'x-fa fa-code',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/custompages/_new');
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.bus.addbusdescriptor,
                iconCls: 'x-fa fa-cubes',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/bus/descriptors_empty/true');
                    }
                }
            }]
        });
        menu.alignTo(tool.el.id, 'tr-br?');
    }
});