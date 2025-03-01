Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-importexport-datatemplates-topbar',

    control: {
        '#addtemplate': {
            click: 'onNewBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.button.Button} button
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (button, event, eOpts) {
        var vm = button.lookupViewModel();
        if (vm.get("showInMainPanel")) {
            this.redirectTo('administration/importexport/datatemplates_empty/true');
        } else {
            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            var targetName = button.lookupViewModel().get('targetName');
            var targetType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(targetName);
            container.removeAll();
            var disabledTargetType, disabledTargetTypeName;
            if (this.getView().up('tabpanel')) {
                disabledTargetType = true;
                disabledTargetTypeName = true;
            }
            var theGateTemplate = {
                type: 'CMDBuildUI.model.importexports.Template',
                create: {
                    targetType: targetType,
                    targetName: targetName
                }
            };            
            if(targetType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process){
                theGateTemplate.create.type = CMDBuildUI.model.importexports.Template.types['export'];
            } else if (vm.get('theGate.config.tag')) {              
                theGateTemplate.create.fileFormat = vm.get('theGate.config.tag');
                theGateTemplate.create.type = CMDBuildUI.model.importexports.Template.types['import'];
            }

            container.add({
                xtype: 'administration-content-importexport-datatemplates-card',
                viewModel: {
                    links: {
                        theGateTemplate: theGateTemplate
                    },
                    data: {
                        disabledTargetType: disabledTargetType,
                        disabledTargetTypeName: disabledTargetTypeName,
                        grid: button.up('grid'),
                        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                        targetType: targetType,
                        targetName: targetName,
                        actions: {
                            view: false,
                            edit: false,
                            add: true
                        }
                    }
                }
            });
        }

    }
});