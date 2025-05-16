Ext.define('CMDBuildUI.view.administration.home.widgets.userstats.StatsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-home-widgets-userstats-stats',
    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#addUsersTool': {
            click: 'onAddUsersToolClick'
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

    onAddUsersToolClick: function (tool, e, owner, eOpts) {
        var me = this;
        var menu = Ext.create('Ext.menu.Menu', {
            autoShow: true,
            items: [{
                text: CMDBuildUI.locales.Locales.administration.users.toolbar.addUserBtn.text,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.users.toolbar.addUserBtn.text'
                },
                iconCls: 'x-fa fa-user',
                // height: 32,
                listeners: {
                    click: function (menuitem, _eOpts) {
                        me.redirectTo('administration/users');
                        Ext.asap(function () {
                            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
                            container.removeAll();
                            container.add({
                                xtype: 'administration-content-users-card-create',
                                viewModel: {
                                    links: {
                                        theUser: {
                                            type: 'CMDBuildUI.model.users.User',
                                            create: {
                                                changePasswordRequired: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.passwordrules.defaultchangepasswordfirstlogin)
                                            }
                                        }
                                    }
                                }
                            });
                        });
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.addgroup,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.addgroup'
                },
                iconCls: 'x-fa fa-users',                
                listeners: {
                    click: function (menuitem, _eOpts) {
                        me.redirectTo('administration/groupsandpermissions_empty/true');
                    }
                }
            }]
        });
        menu.alignTo(tool.el.id, 'tr-br?');
    }


});