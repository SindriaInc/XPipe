Ext.define('CMDBuildUI.view.main.header.HeaderController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.main-header-header',

    control: {
        '#cmdbuildLogo': {
            afterrender: 'onImageAfterRender'
        },
        '#globalsearch': {
            click: 'onGlobalSearchClick'
        },
        '#administration': {
            click: 'onHrefBtnClick'
        },
        '#administrationbtn': {
            afterrender: 'onLinkBtnClickAfterRender',
            click: 'onHrefBtnClick'
        },
        '#management': {
            click: 'onHrefBtnClick'
        },
        '#managementbtn': {
            afterrender: 'onLinkBtnClickAfterRender',
            click: 'onHrefBtnClick'
        },
        '#logout': {
            click: 'onLogoutClick'
        },
        '#logoutbtn': {
            click: 'onLogoutClick'
        },
        '#infobtn': {
            click: 'onInfoBtnClick'
        },
        '#schedulerbtn': {
            click: 'onSchedulerBtnClick'
        },
        '#companylogocontainer': {
            afterrender: 'onCompanyLogoContainerAfterRender'
        },
        '#notificationsBtn': {
            click: 'onNotificationBtnClick'
        },
        '#alertBtn': {
            click: 'onAlertBtnClick'
        }
    },

    listen: {
        global: {
            notificationdeleted: 'onGlobalNotificationDeleted',
            setTooltips: 'onSetTooltips'
        }
    },

    onImageAfterRender: function (image, e) {
        var img_tag = document.getElementById(image.id);
        // modify cursor style
        img_tag.style.cursor = 'pointer';
        // add click event listener
        img_tag.addEventListener("click", function () {
            window.open(image.getSiteUrl(), '_blank');
        });
        // update container height
        image.fireEvent('resize');
        this.getView().updateLayout();
    },

    /**
     * @param {Event} event
     * @param {Ext.dom.Element} el
     * @param {Object} eOpts
     */
    onCmdbuildLogoImageLoaded: function (event, el, eOpts) {
        // fix image width
        this.getView().updateLayout();
    },

    /**
     * @param {Event} event
     * @param {Ext.dom.Element} el
     * @param {Object} eOpts
     */
    onCompanyLogoImageLoaded: function (event, el, eOpts) {
        // fix image width
        this.getView().updateLayout();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onGlobalSearchClick: function (button, e, eOpts) {
        CMDBuildUI.util.Msg.alert('Warning', 'Action "Global search" not implemented!');
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onLinkBtnClickAfterRender: function (button, e, eOpts) {
        if (button.href) {
            // wrap button with "a" tag
            button.el.dom.innerHTML = Ext.String.format(
                '<a class="header-button-link" href="{0}">{1}</a>',
                button.href,
                button.el.dom.innerHTML
            );
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onLogoutClick: function (button, e, eOpts) {
        this.redirectTo("logout", true);
    },

    /**
     *
     * @param {Ext.button.Button} btn
     */
    onHrefBtnClick: function (btn) {
        try {
            location.href = btn.getHref();
        } catch (error) {
            location.href = btn.href;
        }
        window.location.reload();
        return false;
    },

    /**
     * @param {Ext.button.Button} btn
     * @param {Event} e
     * @param {Object} eOpts
     */
    onInfoBtnClick: function (btn, e, eOpts) {
        CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.main.info,
            {
                xtype: 'main-header-infopanel',
                viewModel: {
                    data: {
                        applicationVersion: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.fullversion)
                    }
                }
            },
            null,
            {
                width: 400,
                height: CMDBuildUI.view.main.header.InfoPanel.popupHeight,
                ui: 'managementlighttabpanel'
            }
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSchedulerBtnClick: function (button, e, eOpts) {
        this.redirectTo('events', true);
    },

    /**
     * Set tooltips on items
     */
    onSetTooltips: function () {
        Ext.Array.forEach(this.getView().items.items, function (item, index, allitems) {
            if (item.tooltipLabel) {
                item.setTooltip(eval(item.tooltipLabel));
            }
        });
    },

    /**
     *
     * @param {Ext.Container} container
     * @param {Object} eOpts
     */
    onCompanyLogoContainerAfterRender: function (container, eOpts) {
        var vm = container.lookupViewModel();
        vm.bind({
            bindTo: {
                logo: '{companylogoid}'
            }
        }, function (binds) {
            if (binds.logo) {
                container.removeAll();
                Ext.asap(function () {
                    vm.set("companylogoinfo.hidden", false);
                    var img = Ext.create('Ext.Img', {
                        src: Ext.String.format("{0}/resources/company_logo/download?_dc={1}", CMDBuildUI.util.Config.baseUrl, new Date().getTime()),
                        alt: CMDBuildUI.locales.Locales.main.logo.companylogo,
                        height: 30,
                        cls: 'logo',
                        autoEl: {
                            'data-testid': 'header-companylogo'
                        },
                        listeners: {
                            afterrender: function () {
                                var imgDom = this.imgEl.dom;
                                imgDom.onload = function () {
                                    container.update();
                                };
                            }
                        }
                    });
                    container.add(img);
                });
            } else {
                vm.set("companylogoinfo.hidden", true);
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onNotificationBtnClick: function (button, e, eOpts) {
        if (!button._menu) {
            button._menu = Ext.create('Ext.menu.Menu', {
                autoShow: true,
                alignTarget: button.el,
                defaultAlign: 't80-b',
                items: [{
                    xtype: 'main-notifications-menulist'
                }],
                fbar: [{
                    text: CMDBuildUI.locales.Locales.notifications.viewall,
                    ui: 'management-primary-outline',
                    disabled: true,
                    bind: {
                        disabled: '{!notifications.count}'
                    },
                    listeners: {
                        click: this.onNotificationViewAll
                    }
                }],
                listeners: {
                    'show': this.onNotificationMenuShow
                }
            });
        } else {
            button._menu.show();
        }
    },

    /**
     * @param {Number|String} notificationId
     */
    onGlobalNotificationDeleted: function (notificationId) {
        var store = this.getViewModel().get('notificationStore'),
            record = store.getById(notificationId);
        if (record) {
            store.remove(record);
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAlertBtnClick: function (button, e, eOpts) {
        if (!button._menu) {
            button._menu = Ext.create('Ext.menu.Menu', {
                autoShow: true,
                alignTarget: button.el,
                defaultAlign: 't80-b',
                width: 300,
                items: [{
                    xtype: 'container',
                    padding: 10,
                    html: CMDBuildUI.locales.Locales.administration.common.messages.reloadsystem
                }],
                fbar: [{
                    text: CMDBuildUI.locales.Locales.administration.common.messages.reload,
                    ui: 'administration-action-small',
                    disabled: true,
                    bind: {
                        disabled: '{disableAlertBtn}'
                    },
                    listeners: {
                        click: this.onReloadBtnClick
                    }
                }]
            });
        } else {
            button._menu.show();
        }
    },

    privates: {
        /**
         * Open notifications popup.
         */
        onNotificationViewAll: function () {
            CMDBuildUI.util.Utilities.openPopup(
                null,
                CMDBuildUI.locales.Locales.notifications.label,
                {
                    xtype: 'main-notifications-grid'
                }
            )
        },

        /**
         * On notification menu show.
         *
         * @param {Ext.menu.Menu} menu
         * @param {Object} eOpts
         */
        onNotificationMenuShow: function (menu, eOpts) {
            setTimeout(function () {
                if (menu.isVisible()) {
                    var store = menu.lookupViewModel().get('notificationStore');
                    store.getRange().forEach(function (n) { //query('_isNew', true).
                        n.set({
                            status: 'archived',
                            _isNew: false
                        });
                    });
                    store.sync();
                }
            }, 1500);

            var menulist = menu.down('main-notifications-menulist');
            menulist.refresh();
        },

        /**
         *
         * @param {Ext.button.Button} button
         * @param {Event} event
         * @param {Object} eOpts
         */
        onReloadBtnClick: function (button, event, eOpts) {
            const reloadApp = function () {
                Ext.Ajax.request({
                    url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getBootStatusUrl(),
                    method: 'GET',
                    callback: function (records, operation, success) {
                        if (success.status === 200 && success.responseText) {
                            const jsonresponse = Ext.JSON.decode(success.responseText);
                            const status = jsonresponse.status;
                            if (status === 'READY') {
                                window.location.reload();
                            }
                        }

                        setTimeout(function () {
                            reloadApp();
                        }, 2000);
                    }
                });
            }

            CMDBuildUI.util.Msg.confirm(
                CMDBuildUI.locales.Locales.notifier.attention,
                CMDBuildUI.locales.Locales.administration.common.messages.areyousurerebootapplication,
                function (action) {
                    if (action === "yes") {
                        const mainContainer = CMDBuildUI.util.Navigation.getMainContainer()
                        mainContainer.getViewModel().set("disableAlertBtn", true);
                        CMDBuildUI.util.Utilities.addLoadMask(mainContainer);
                        Ext.Ajax.request({
                            method: 'POST',
                            url: CMDBuildUI.util.Config.baseUrl + "/system/restart",
                            callback: function () {
                                reloadApp();
                            }
                        });
                    }
                }
            );
        }
    }
});