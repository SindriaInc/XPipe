Ext.define('CMDBuildUI.view.administration.content.bim.projects.card.ViewEditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-bim-projects-card-viewedit',
    data: {
        toolAction: {
            _canAdd: false,
            _canClone: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },
    formulas: {
        panelTitle: {
            bind: {
                theProject: '{theProject}',
                description: '{theProject.description}'
            },
            get: function (data) {
                var title = Ext.String.format(
                    '{0} {1} {2}',
                    data.theProject.phantom ? CMDBuildUI.locales.Locales.administration.bim.newproject : CMDBuildUI.locales.Locales.administration.bim.projectlabel,
                    data.description ? ' - ' : '',
                    data.description
                );
               return title;
            }
        },       
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
        canConvert: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_bim_modify}'               ,
                _can_convert: '{theProject._can_convert}' 
            },
            get: function (data) {                
                this.set('toolAction._canConvert', data.canModify === true && data._can_convert);
            }
        },
        updateCardDescription: {
            bind: '{theProject.ownerCard}',
            get: function (card) {
                if (card) {
                    var vm = this;
                    var theProject = vm.get('theProject');
                    var id = card;
                    var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                    CMDBuildUI.util.helper.ModelHelper.getModel(type, theProject.get('ownerClass')).then(
                        function (model) {
                            model.load(id, {
                                success: function (record) {
                                    try {
                                        vm.set('cardDescription', record.get('Description'));
                                    } catch (e) {

                                    }
                                }
                            });
                        }
                    );
                }
            }
        }
    },
    stores: {
        projects: {
            source: 'bim.Projects'
        }
    }
});