Ext.define('CMDBuildUI.view.login.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.login-container',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        'main-header-logodark': {
            afterrenderer: 'onMainHeaderLogoDarkBeforeRenderer'
        },
        '#pwdforgottenbtn': {
            click: 'onPwdForgottenBtnClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.login.Container} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const me = this;
        const vm = view.lookupViewModel();

        vm.bind({
            bindTo: '{languages}',
            deep: true
        }, function (languages) {
            if (languages.getRange().length === 0) return; //Skip update if there isn't languages

            const selectedlanguage = vm.get('language').default;
            const record = languages.findRecord('code', selectedlanguage);
            const menuItems = [];

            const btn = me.getView().down('#languageselector');
            btn.setText(record ? record.get('description') : selectedlanguage);

            // extract languages
            languages.getRange().forEach(function (language) {
                menuItems.push({
                    text: language.get('description'),
                    icon: language.get('code'),
                    disabled: language.get('code') === selectedlanguage,
                    handler: 'onLanguageSelectorItemClick'
                });
            });

            // menu setup
            btn.setMenu({
                defaults: {
                    renderTpl:
                        '<a id="{id}-itemEl" data-ref="itemEl"' +
                        ' class="{linkCls} {childElCls}"' +
                        ' href="#" ' +
                        ' hidefocus="true"' +
                        ' unselectable="on"' +
                        '<tpl if="tabIndex != null">' +
                        ' tabindex="{tabIndex}"' +
                        '</tpl>' +
                        '<tpl foreach="ariaAttributes"> {$}="{.}"</tpl>' +
                        '>' +
                        '<span id="{id}-textEl" data-ref="textEl" class="{textCls} {textCls}-{ui} {indentCls}{childElCls}" unselectable="on" role="presentation">{text}</span>' +
                        '<div role="presentation" id="{id}-iconEl" data-ref="iconEl" class="{baseIconCls}-{ui} {baseIconCls}' +
                        '{iconCls} {childElCls} {glyphCls}">' +
                        '<img width="20px" src="resources/images/flags/{icon}.png">' +
                        '</div>' +
                        '</a>'
                },
                items: menuItems
            });
        });
    },

    onMainHeaderLogoDarkBeforeRenderer: function (view, eOpts) {
        view.center();
    },

    /**
    * @param {Ext.menu.Item} item
    * @param {Ext.event.Event} event
    * @param {Object} eOpts
    */
    onLanguageSelectorItemClick: function (item, event, eOpts) {
        CMDBuildUI.util.helper.LocalStorageHelper.set(
            CMDBuildUI.util.helper.LocalStorageHelper.keys.loginlanguage,
            item.icon
        );
        window.location.reload();
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Event} e
     */
    onPwdForgottenBtnClick: function (btn, e) {
        var popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.main.password.forgotten,
            {
                xtype: 'login-passwordforgotten-panel',
                viewModel: {
                    data: {
                        username: btn.lookupViewModel().get("theSession.username")
                    }
                },
                listeners: {
                    closepopup: function () {
                        popup.close();
                    }
                }
            },
            null,
            {
                width: 400,
                height: 250
            }
        );
    }
});
