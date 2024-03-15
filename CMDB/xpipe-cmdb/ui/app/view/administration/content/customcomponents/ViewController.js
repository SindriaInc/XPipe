Ext.define('CMDBuildUI.view.administration.content.customcomponents.ViewController', {
    extend: 'Ext.app.ViewController',
    requires: ['CMDBuildUI.util.administration.File'],
    alias: 'controller.administration-content-customcomponents-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#searchtext': {
            beforerender: 'onSearchTextBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#addBtn': {
            beforerender: 'onAddBtnBeforeRender',
            click: 'onAddBtnClick'
        },
        '#downloadBtn': {
            click: 'onDownloadBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }
    },

    /**
     * Before render
     * @param {CMDBuildUI.view.administration.content.customcomponents.View} view
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        var subtitle;
        switch (vm.get('componentType')) {
            case 'contextmenu':
                subtitle = CMDBuildUI.locales.Locales.administration.common.labels.contextmenu;
                vm.set('componentTypeName', CMDBuildUI.locales.Locales.administration.customcomponents.strings.contextmenu);
                break;
            case 'widget':
                subtitle = CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formWidgets;
                vm.set('componentTypeName', CMDBuildUI.locales.Locales.administration.customcomponents.strings.widget);
                break;
            default:
                break;
        }
        vm.bind({
            bindTo: '{theCustomcomponent.devices}'
        }, function (device) {
            var title = Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.administration.customcomponents.plural, subtitle);
            view.up('administration-content').getViewModel().set('title', title);

        });
        this.setFilefieldProperties();
    },
    /**
     * Before render search textfield
     * @param {Ext.form.field.Text} input
     */
    onSearchTextBeforeRender: function (input) {
        var vm = input.lookupViewModel();
        switch (vm.get('componentType')) {
            case 'contextmenu':
                vm.set('componentTypeName', CMDBuildUI.locales.Locales.administration.customcomponents.strings.contextmenu);
                input.setEmptyText(CMDBuildUI.locales.Locales.administration.customcomponents.strings.searchcontextmenus);
                break;
            case 'widget':
                vm.set('componentTypeName', CMDBuildUI.locales.Locales.administration.customcomponents.strings.widget);
                input.setEmptyText(CMDBuildUI.locales.Locales.administration.customcomponents.strings.searchwidgets);
                break;

            default:
                break;
        }
    },
    /**
     * Before render add button
     * @param {Ext.button.Button} button
     */
    onAddBtnBeforeRender: function (button) {
        var vm = button.lookupViewModel();
        switch (vm.get('componentType')) {
            case 'contextmenu':
                button.setText(CMDBuildUI.locales.Locales.administration.customcomponents.strings.addcontextmenu);
                break;
            case 'widget':
                button.setText(CMDBuildUI.locales.Locales.administration.customcomponents.strings.addwidget);
                break;
            default:
                break;
        }
    },
    /**
     * On add customcomponent button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        this.redirectTo(Ext.String.format('administration/customcomponents_empty/{0}/true', vm.get('componentType')), true);
    },

    /**
     * On delete customcomponent button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);

                    CMDBuildUI.util.Ajax.setActionId('delete-customcomponent');
                    me.getViewModel().get('theCustomcomponent').erase({
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomComponentUrl(vm.get('componentType'));
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', Ext.util.History.getToken(), nextUrl, me);
                        },
                        callback: function (record, reason) {
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                        }
                    });
                }
            }, this);
    },
    removeVersionBtnClick: function (button) {
        var vm = this.getViewModel();
        vm.set(Ext.String.format('{0}Removed', button.device), true);
    },
    downloadVersionBtnClick: function (button) {
        var vm = this.getViewModel();
        var url = Ext.String.format(
            '{0}/components/{1}/{2}/{3}',
            CMDBuildUI.util.Config.baseUrl,
            vm.get('componentType'),
            vm.get('theCustomcomponent._id'),
            button.device
        );
        CMDBuildUI.util.File.download(url, 'zip');
    },
    /**
     * On download customcomponent button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDownloadBtnClick: function (button, e, eOpts) {
        var onDownloadForDevice = function () {
            var vm = button.lookupViewModel();
            var url = Ext.String.format(
                '{0}/components/{1}/{2}/{3}',
                CMDBuildUI.util.Config.baseUrl,
                vm.get('componentType'),
                vm.get('theCustomcomponent._id'),
                this.device
            );
            CMDBuildUI.util.File.download(url, 'zip');

        };
        var devices = this.getViewModel().get('theCustomcomponent.devices');
        var hasDefault = devices.indexOf(CMDBuildUI.model.menu.Menu.device['default']) > -1;
        var hasMobile = devices.indexOf(CMDBuildUI.model.menu.Menu.device.mobile) > -1;

        var items = [];
        if (hasDefault) {
            items.push({
                iconCls: 'x-fa fa-desktop',
                text: CMDBuildUI.locales.Locales.administration.common.labels.desktop,
                device: CMDBuildUI.model.menu.Menu.device['default'],
                height: 32,
                autoEl: {
                    'data-testid': 'customcomponents-downloadDefaultBtn'
                },
                handler: onDownloadForDevice
            });
        }
        if (hasMobile) {
            items.push({
                iconCls: 'x-fa fa-mobile',
                text: CMDBuildUI.locales.Locales.administration.common.labels.mobile,
                device: CMDBuildUI.model.menu.Menu.device.mobile,
                height: 32,
                autoEl: {
                    'data-testid': 'customcomponents-downloadDefaultBtn'
                },
                handler: onDownloadForDevice
            });
        }
        var menu = Ext.create('Ext.menu.Menu', {
            autoShow: true,
            ui: 'default',
            items: items
        });
        menu.setMinWidth(35);
        menu.alignTo(button.el.id, 't-b?');

    },
    /**
     * On edit customcomponent button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.setFilefieldProperties();
    },

    /**
     * On cancel button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        if (vm.get('actions.add')) {
            var nextUrl = Ext.String.format('administration/customcomponents_empty/{0}/false', vm.get('componentType'));
            this.redirectTo(nextUrl, true);
            var store = Ext.getStore('administration.MenuAdministration');
            var vmNavigation = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode("objecttype", CMDBuildUI.model.administration.MenuItem.types.customcomponent);
            vmNavigation.set('selected', currentNode);
        } else {
            vm.get('theCustomcomponent').reject();
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        }
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theCustomcomponent = vm.get('theCustomcomponent');
        theCustomcomponent.set('active', !theCustomcomponent.get('active'));
        this.save(vm);
    },

    /**
     * On save button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var me = this;
        var form = this.getView().getForm();
        var vm = this.getView().getViewModel();

        if (form.isValid()) {
            var removeVersion = function (record, device) {
                Ext.Ajax.request({
                    url: Ext.String.format("{0}/{1}/{2}", record.getProxy().getUrl(), record.get('_id'), device),
                    method: 'DELETE'
                });
            };
            var afterSave = function (record) {
                if (vm.get('defaultRemoved')) {
                    removeVersion(record, CMDBuildUI.model.menu.Menu.device['default']);
                }
                if (vm.get('mobileRemoved')) {
                    removeVersion(record, CMDBuildUI.model.menu.Menu.device.mobile);
                }
                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomComponentUrl(vm.get('componentType'), record.get('_id'));
                if (vm.get('actions.edit')) {
                    var newDescription = record.get('description');
                    CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, newDescription, me);
                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                } else {
                    CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                        function () {
                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                            if (button.el && button.el.dom) {
                                button.setDisabled(false);
                            }
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                        });
                }
            };

            me.save(vm,
                function (res) {
                    var record = res;
                    if (!record.isModel) {
                        record = CMDBuildUI.model.customcomponents.ContextMenu.create(record.data);
                    }
                    afterSave(record);
                },
                function (reason) {
                    button.setDisabled(false);
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                }
            );
        } else {
            button.setDisabled(false);
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },


    /**
     * @privates
     */
    privates: {
        /**
         * @private
         */
        setFilefieldProperties: function () {
            var view = this.getView();
            var vm = this.getViewModel();
            if (vm.get('actions.edit')) {
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(view.down('[name="fileCustomcomponentDefault"]'), true, this.getView());
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(view.down('[name="fileCustomcomponentMobile"]'), true, this.getView());
            } else if (vm.get('actions.add')) {
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(view.down('[name="fileCustomcomponentDefault"]'), false, this.getView());
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(view.down('[name="fileCustomcomponentMobile"]'), false, this.getView());
            }
        },
        /**
         * @private
         * @param {CMDBuildUI.view.administration.content.customcomponents.ViewModel} vm 
         * @param {Function} successCb 
         * @param {Function} errorCb 
         */
        save: function (vm, successCb, errorCb) {
            CMDBuildUI.util.Ajax.setActionId('customcomponent.upload');
            // define method
            var method = vm.get("actions.add") ? "POST" : "PUT";

            var fileDefault = this.getView().down('[name="fileCustomcomponentDefault"]').extractFileInput();
            var fileMobile = this.getView().down('[name="fileCustomcomponentMobile"]').extractFileInput();

            // init formData
            var formData = new FormData();

            // append attachment json data
            var data = vm.get("theCustomcomponent").getData();
            if (vm.get('actions.add')) {
                delete data._id;
            }
            var jsonData = Ext.encode(data);
            var fieldName = 'data';
            try {
                formData.append(fieldName, new Blob([jsonData], {
                    type: "application/json"
                }));
            } catch (err) {
                CMDBuildUI.util.Logger.log("Unable to create attachment Blob FormData entry with type 'application/json', fallback to 'text/plain': " + err, CMDBuildUI.util.Logger.levels.error);
                // metadata as 'text/plain' (format compatible with older webviews)
                formData.append(fieldName, jsonData);
            }

            // get url
            var custompageUrl = Ext.String.format('{0}/components/{1}', CMDBuildUI.util.Config.baseUrl, vm.get('componentType'));
            var url = vm.get('actions.add') ? custompageUrl : Ext.String.format('{0}/{1}', custompageUrl, vm.get('theCustomcomponent._id'));
            // upload 
            if (fileDefault.files.length || fileMobile.files.length) {
                CMDBuildUI.util.Ajax.initRequestException();
                var files = [];
                if (fileDefault && fileDefault.value) {
                    vm.set('defaultRemoved', false);
                    files.push(fileDefault);
                }
                if (fileMobile && fileMobile.value) {
                    vm.set('mobileRemoved', false);
                    files.push(fileMobile);
                }
                CMDBuildUI.util.administration.File.upload(method, formData, files, url, {
                    success: function (response) {
                        if (typeof response === 'string') {
                            response = Ext.JSON.decode(response);
                        }
                        if (Ext.isFunction(successCb)) {
                            Ext.callback(successCb, null, [response]);
                        }
                    },
                    failure: function (error) {
                        if (typeof error === 'string') {
                            error = Ext.JSON.decode(error);
                        }
                        if (Ext.isFunction(errorCb)) {
                            Ext.callback(errorCb, null, [error]);
                        }
                    }
                });
            } else {
                // file is empty, use default put 
                vm.get('theCustomcomponent').save({
                    success: function (record, operation) {
                        if (Ext.isFunction(successCb)) {
                            Ext.callback(successCb, null, [record]);
                        }
                    },
                    failure: function (record, reason) {

                        if (Ext.isFunction(errorCb)) {
                            Ext.callback(errorCb, null, [reason]);
                        }

                    }
                });
            }

        }
    }
});