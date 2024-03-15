Ext.define('CMDBuildUI.view.administration.content.bim.projects.card.ViewEditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bim-projects-card-viewedit',
    control: {
        '#': {
            beforeRender: 'onBeforeRender'
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
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#convertBtn': {
            click: 'onConvertBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewEditController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, e, eOpts) {
        Ext.getStore('bim.Projects').load();
        var me = this;
        var vm = this.getViewModel();
        vm.bind({
            bindTo: {
                panelTitle: '{panelTitle}'
            }
        }, function (data) {
            if (data.panelTitle) {
                me.getView().up().setTitle(data.panelTitle);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        var form = me.getView();
        var inputFileIFC = me.lookupReference("fileIFC").extractFileInput().files[0];

        var viewports = Ext.ComponentQuery.query('viewport');
        var grid = viewports[0].down('administration-content-bim-projects-grid');

        if (form.isValid()) {
            CMDBuildUI.util.Utilities.showLoader(true);
            var theProject = vm.get('theProject');
            var eventtocall = theProject.phantom ? 'itemcreated' : 'itemupdated';
            var method = theProject.phantom ? 'POST' : 'PUT';
            var url = Ext.String.format('{0}/bim/projects{1}', CMDBuildUI.util.Config.baseUrl, !theProject.phantom ? '/' + theProject.get('_id') : '');

            var getData = function () {
                var data = theProject.getData();
                if (theProject.phantom) {

                    delete data._id;
                }
                for (var key in data) {
                    if (Ext.String.startsWith(key, "_")) {
                        delete data[key];
                    }
                }

                return data;
            };

            CMDBuildUI.util.File.uploadFileWithMetadata(
                method,
                url,
                inputFileIFC,
                getData(), {
                filePartName: 'file',
                metadataPartName: 'data'
            }
            ).then(function (response) {
                // execute after action triggers
                var plugin = grid.getPlugin('administration-forminrowwidget');
                if (plugin) {
                    plugin.view.fireEventArgs(eventtocall, [grid, Ext.create('CMDBuildUI.model.bim.Projects', response), this]);
                }
                CMDBuildUI.util.Utilities.showLoader(false);
                form.up("panel").close();
            }, function (err) {
                CMDBuildUI.util.Utilities.showLoader(false);
            }, Ext.emptyFn, this);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getView().up('panel').close();
    },

    onEditBtnClick: function () {
        this.getViewModel().set('actions.edit', true);
        this.getViewModel().set('actions.view', false);
    },

    sendFileIfc: function (inputFileIFC) {
        var me = this;
        var reader = new FileReader();

        reader.addEventListener("load", function () {
            var res = reader.result;
            var ifctype;
            if (res.search("(('IFC2X3'))")) {
                ifctype = 'ifc2x3tc1';
            } else {
                ifctype = 'ifc4';
            }
            me.prepareCall(inputFileIFC, ifctype);
        }, false);

        if (inputFileIFC) {
            reader.readAsText(inputFileIFC);
        }
    },

    prepareCall: function (inputFileIFC, ifctype) {
        var me = this;
        var form = me.getView();
        var vm = me.getViewModel();
        // init formData
        var formData = new FormData();
        // append attachment json data
        var jsonData = Ext.encode(vm.get("theProject").getData());
        var fieldName = 'fileIfc';
        try {
            formData.append(fieldName, new Blob([jsonData], {
                type: "application/json"
            }));
        } catch (err) {
            CMDBuildUI.util.Logger.log(
                "Unable to create attachment Blob FormData entry with type 'application/json', fallback to 'text/plain': " + err,
                CMDBuildUI.util.Logger.levels.error
            );
            // metadata as 'text/plain' (format compatible with older webviews)
            formData.append(fieldName, jsonData);
        }
        // get url
        var url = Ext.String.format('{0}/bim/projects/{1}/file?ifcFormat={2}', CMDBuildUI.util.Config.baseUrl, vm.get('theProject').get('_id'), ifctype);
        // define method
        var method = "POST";
        CMDBuildUI.util.File.upload(method, formData, inputFileIFC, url, {
            success: function () {
                form.up("panel").close();
                if (form && form.loadMask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(form);
                }
            },
            failure: function (error) {
                form.up("panel").close();
                var response = {
                    responseText: error
                };
                CMDBuildUI.util.Ajax.showMessages(response, {
                    hideErrorNotification: false
                });
                if (form && form.loadMask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(form);
                }
            }
        });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onDownloadBtnClick: function (button, eOpts) {
        var vm = this.getView().getViewModel();
        var url = Ext.String.format('{0}/bim/projects/{1}/file?bimFormat={2}', CMDBuildUI.util.Config.baseUrl, vm.get('theProject').getId(), button.fileType);
        CMDBuildUI.util.File.download(url, 'ifc');
    },
    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onCloneBtnClick: function (button, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var clonedProject = Ext.copy(vm.get('theProject').clone());
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                data: {
                    theProject: clonedProject,
                    actions: {
                        edit: false,
                        view: false,
                        add: true
                    }
                }
            }
        });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onDeleteBtnClick: function (button, eOpts) {
        var me = this;
        var grid = this.getViewModel().get('grid');
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-project');
                    var vm = me.getViewModel();
                    var theProject = vm.get('theProject');
                    theProject.erase({
                        success: function (record, operation) {
                            grid.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemremoved', [grid, record, me]);
                            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                        },
                        failure: function (opration) {
                            theProject.reject();
                        }
                    });
                }
            }, this);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onToggleActiveBtnClick: function (button, eOpts) {
        var me = this;
        var view = this.getView();
        var vm = view.getViewModel();
        var grid = vm.get('grid');
        var theProject = vm.get('theProject');
        theProject.set('active', !theProject.get('active'));
        theProject.save({
            success: function (record, operation) {
                grid.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [grid, record, me]);
            },
            failure: function (record, reason) {
                record.reject();
            }
        });

    },
    onConvertBtnClick: function (button, eOpts) {
        var view = button.up('administration-content-bim-projects-card-viewedit');
        var vm = view.getViewModel();
        var theProject = vm.get('theProject');
        var url = Ext.String.format('{0}/{1}/convert/xkt', theProject.getProxy().getUrl(), theProject.getId());
        CMDBuildUI.util.Utilities.showLoader(true, view);

        Ext.Ajax.request({
            url: url,
            method: 'POST',
            failure: function () {
                if (button && !button.destroyed) {
                    button.setDisabled(false);
                }
            },
            callback: function (response) {
                if (view && !view.destroyed) {
                    CMDBuildUI.util.Utilities.showLoader(false, view);
                }
            }
        });
    }
});