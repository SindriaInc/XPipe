Ext.define('CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bim-projects-card-viewinrow',
    control: {
        '#': {
            beforeRender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
        },
        // '#downloadBtn': {
        //     click: 'onDownloadBtnClick'
        // },
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
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var record = view.getInitialConfig()._rowContext.record;
        vm.linkTo("theProject", {
            type: 'CMDBuildUI.model.bim.Projects',
            id: record.getId()
        });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onEditBtnClick: function (button, eOpts) {
        var vm = this.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var theProject = vm.get('theProject');
        container.removeAll();
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                links: {
                    theProject: {
                        type: 'CMDBuildUI.model.bim.Projects',
                        id: theProject.getId()
                    }
                },
                data: {
                    actions: {
                        edit: true,
                        view: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onOpenBtnClick: function (button, eOpts) {
        var vm = this.getViewModel();
        var grid = this.getView().up('grid');
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var theProject = vm.get('theProject').copy();
        var cardDescription = vm.get('cardDescription');
        theProject.set('cardDescription', cardDescription);
        container.removeAll();
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                links: {
                    theProject: {
                        type: 'CMDBuildUI.model.bim.Projects',
                        id: theProject.getId()
                    }
                },
                data: {
                    grid: grid,
                    actions: {
                        edit: false,
                        view: true,
                        add: false
                    }
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
        var grid = this.getView().up('grid');
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
        var view = this.getView();
        var vm = view.getViewModel();
        var theProject = vm.get('theProject');
        theProject.set('active', !theProject.get('active'));
        theProject.save({
            success: function (record, operation) {

            },
            failure: function (record, reason) {
                record.reject();
            }
        });
    },

    onConvertBtnClick: function (button, eOpts) {
        var grid = button.up('grid');
        CMDBuildUI.util.Utilities.showLoader(true, grid);
        button.setDisabled(true);
        var view = this.getView();
        var vm = view.getViewModel();
        var theProject = vm.get('theProject');
        var url = Ext.String.format('{0}/{1}/convert/xkt', theProject.getProxy().getUrl(), theProject.getId());
        Ext.Ajax.request({
            url: url,
            method: 'POST',
            failure: function () {
                if (button && !button.destroyed) {
                    button.setDisabled(false);
                }
            },
            callback: function (response) {
                if (grid && !grid.destroyed) {
                    CMDBuildUI.util.Utilities.showLoader(false, grid);
                }
            }
        });
    }
});