Ext.define('CMDBuildUI.view.administration.content.bim.projects.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-bim-projects-view',
    data: {
        storeAutoLoad: false,
        storeProxyUrl: '',
        toolAction: {
            _canAdd: false,
            _canClone: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_bim_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canClone', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },

        panelTitle: {
            bind: {
                theProject: '{theProject}',
                theProjectDescription: '{theProject.description}'
            },
            get: function (data) {
                if (data.theProject.phantom) {
                    return CMDBuildUI.locales.Locales.administration.bim.newproject;
                } else {
                    return Ext.String.format('{0}: {1}', CMDBuildUI.locales.Locales.administration.bim.projectlabel, data.theProjectDescription);
                }

            }
        },
        updateStoreVariables: {
            get: function (data) {
                // set auto load
                this.set("storeAutoLoad", true);
            }
        }
    },

    stores: {
        projects: {
            // source: 'bim.Projects',
            type: 'bim-projects',
            autoLoad: '{storeAutoLoad}',
            autoDestroy: true,
            pageSize: 0
        }
    }

});