Ext.define('CMDBuildUI.view.administration.content.emails.templates.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-templates-topbar',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addtemplate': {
            click: 'onNewTemplateBtnClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.administration.content.emails.templates.Topbar} view 
     */
    onBeforeRender: function (view) {
        var addBtn = view.down('#addtemplate');
        var vm = view.lookupViewModel();
        if (vm.get('templateType') !== 'all') {
            addBtn.setArrowVisible(false);
        }
    },
    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewTemplateBtnClick: function (item, event, eOpts) {
        var templateType = this.getViewModel().get('templateType');
        if (templateType === 'all') {
            return this.showMenuButton(item, event, eOpts);
        }
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-templates-card-form',
            viewModel: {
                links: {
                    theTemplate: {
                        type: 'CMDBuildUI.model.emails.Template',
                        create: {
                            provider: CMDBuildUI.model.emails.Template.providers[templateType]
                        }
                    }
                },
                data: {
                    actions: {
                        view: false,
                        add: true,
                        edit: false
                    }
                }
            }
        });

    },
    privates: {
        showMenuButton: function (button) {
            var me = this;
            var templateType = this.getViewModel().get('templateType');
            if (templateType === 'all') {

                var menus = [];
                menus.push({
                    text: CMDBuildUI.locales.Locales.administration.emails.email,
                    type: CMDBuildUI.model.emails.Template.providers.email,
                    iconCls: 'x-fa fa-envelope',
                    handler: me.onAddTemplateMenuItemBtnClick
                });
                menus.push({
                    text: CMDBuildUI.locales.Locales.administration.emails.inappnotification,
                    type: CMDBuildUI.model.emails.Template.providers.inappnotification,
                    iconCls: 'x-fa fa-bell',
                    handler: me.onAddTemplateMenuItemBtnClick
                });
                menus.push({
                    text: CMDBuildUI.locales.Locales.administration.emails.mobilenotification,
                    type: CMDBuildUI.model.emails.Template.providers.mobilenotification,
                    iconCls: 'x-fa fa-bell',
                    handler: me.onAddTemplateMenuItemBtnClick
                });
                var menu = Ext.create('Ext.menu.Menu', {
                    autoShow: true,
                    items: menus
                });

                menu.alignTo(button.el.id, 'bl');
                button.setMenu(menu);
            }
        },
        onAddTemplateMenuItemBtnClick: function (menuItem) {

            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            container.removeAll();

            var cardVm = {
                links: {
                    theTemplate: {
                        type: 'CMDBuildUI.model.emails.Template',
                        create: {
                            provider: menuItem.type
                        }
                    }
                },
                data: {
                    actions: {
                        view: false,
                        add: true,
                        edit: false
                    }
                }
            };

            container.add({
                xtype: 'administration-content-emails-templates-card-form',
                viewModel: cardVm
            });
        }
    }
});