Ext.define('CMDBuildUI.view.administration.home.widgets.modelsstats.ModelsStatsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-home-widgets-modelsstats-modelsstats',
    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#addModelTool': {
            click: 'onAddModelToolClick',
            destroy: 'onDestroyTool'
        }
    },

    onAfterRender: function (view) {
        const vm = this.getViewModel();
        vm.bind({
            bindTo: '{showLoader}'
        }, function (showLoader) {
            CMDBuildUI.util.Utilities.showLoader(showLoader, view);
        });
    },

    onAddModelToolClick: function (tool, e, owner) {
        const me = this;
        tool.menu = Ext.create('Ext.menu.Menu', {
            autoShow: true,
            items: [{
                text: CMDBuildUI.locales.Locales.administration.classes.toolbar.addClassBtn.text,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'regular'),
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/classes');
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.processes.toolbar.addProcessBtn.text,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid'),
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/processes');
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.domains.texts.adddomain,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/domains');
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.views.addview,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                height: 32,
                menu: [{
                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromfilter,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/classes');
                        }
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromjoin,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/processes');
                        }
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromsql,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/domains');
                        }
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromschedule,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                    height: 32,
                    menu: [{
                        text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromfilter,
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                        height: 32,
                        listeners: {
                            click: function (menuitem, eOpts) {
                                me.redirectTo('administration/views/_new/FILTER/true');
                            }
                        }
                    }, {
                        text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromjoin,
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                        height: 32,
                        listeners: {
                            click: function (menuitem, eOpts) {
                                me.redirectTo('administration/joinviews_empty/true');
                            }
                        }
                    }, {
                        text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromsql,
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                        height: 32,
                        listeners: {
                            click: function (menuitem, eOpts) {
                                me.redirectTo('administration/views/_new/SQL/true');
                            }
                        }
                    }, {
                        text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromschedule,
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                        height: 32,
                        listeners: {
                            click: function (menuitem, eOpts) {
                                me.redirectTo('administration/views/_new/CALENDAR/true');
                            }
                        }
                    }]
                }, {
                    text: CMDBuildUI.locales.Locales.administration.reports.texts.addreport,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'solid'),
                    height: 32,
                    listeners: {
                        click: function (menuitem, eOpts) {
                            me.redirectTo('administration/reports/_new');
                        }
                    }
                }]
            }, {
                text: CMDBuildUI.locales.Locales.administration.reports.texts.addreport,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('copy', 'regular'),
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/reports/_new');
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.dashboards.adddashboard,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('chart-area', 'solid'),
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/dashboards/_new');
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.custompages.texts.addcustompage,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('code', 'solid'),
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/custompages/_new');
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.bus.addbusdescriptor,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('cubes', 'solid'),
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        me.redirectTo('administration/bus/descriptors_empty/true');
                    }
                }
            }]
        });
        tool.menu.alignTo(tool.el.id, 'tr-br?');
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Object} eOpts
     */
    onDestroyTool: function (tool, eOpts) {
        if (tool.menu) {
            tool.menu.destroy();
        }
    }

});