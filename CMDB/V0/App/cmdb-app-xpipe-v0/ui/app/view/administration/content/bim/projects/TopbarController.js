Ext.define('CMDBuildUI.view.administration.content.bim.projects.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bim-projects-topbar',
    requires: ['CMDBuildUI.util.administration.helper.GridHelper'],
    control: {
        '#addproject': {
            click: 'onNewProjectBtnClick'
        }
    },

    onNewProjectBtnClick: function () {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                links: {
                    theProject: {
                        type: 'CMDBuildUI.model.bim.Projects',
                        create: true
                    }
                },
                data: {
                    actions: {
                        edit: false,
                        view: false,
                        add: true
                    }
                }
            }
        });
    }    
});