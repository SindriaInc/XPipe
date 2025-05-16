Ext.define('CMDBuildUI.view.joinviews.configuration.items.DomainsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.joinviews-configuration-items-domains',
    data: {
        treeRoot: null
    },
    formulas: {        
        sourceClassManager: {
            bind: '{theView.masterClass}',
            get: function (masterClassName) {                
                var masterClass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(masterClassName);
                if (masterClass) {
                    this.set('fieldsetTitle', Ext.String.format(CMDBuildUI.locales.Locales.joinviews.domainsof, masterClass.getTranslatedDescription()));
                    this.set('treepanelHidden', false);
                } else {
                    this.set('fieldsetTitle', CMDBuildUI.locales.Locales.joinviews.pleaseseleceavalidmasterclass);
                    this.set('treepanelHidden', true);
                }
            }
        },
        treeRoot: {
            bind: {
                currentStepWas: '{currentStepWas}',
                currentStep: '{currentStep}',
                masterClass: '{theView.masterClass}'
            },
            get: function (data) {                   
                var treeStore = this.getStore('treeStore');
                if(data.currentStep === 1 && data.currentStepWas === 0 && (!treeStore || data.masterClass !== treeStore.getRootNode().get('targetType'))){
                    this.getParent().set('stepNavigationLocked', true);
                    var root = this.get('theView').joinWith().count() ? this.get('theView').joinWith().first().getData() : {};
                    var target = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.masterClass);
                    var targetDescription = target && target.getTranslatedDescription() ? target.getTranslatedDescription() : data.masterClass;
    
                    var node = {};
                    node._id = root._id || CMDBuildUI.util.Utilities.generateUUID();
                    node.text = targetDescription;
                    node.targetAlias = this.get('theView').get('masterClassAlias');
                    node.targetType = data.masterClass;
                    node.children = [];
                    node.expanded = false;
                    node.checked = true;
                    node.filter = '';
                    node.domain = root.domain;                
                    return node;
                }                          
            }
        },
        joinTypes: {
            get: function (get) {
                return [{
                    value: CMDBuildUI.model.views.JoinViewJoin.jointypes.inner_join,
                    label: CMDBuildUI.locales.Locales.joinviews.innerjoin
                }, {
                    value: CMDBuildUI.model.views.JoinViewJoin.jointypes.outer_join,
                    label: CMDBuildUI.locales.Locales.joinviews.outerjoin
                }];
            }
        }
    },

    stores: {

        childrensClassesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',            
            proxy: 'memory',
            sorter: ['label'],
            autoDestroy: true
        },

        joinTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{joinTypes}',
            proxy: 'memory',
            sorters: ['label'],
            autoDestroy: true
        },

        treeStore: {
            type: 'tree',
            folderSort: true,
            proxy: {
                type: 'memory'
            },
            storeId: 'joinTreeStore',
            sorters: ['text'],
            root: '{treeRoot}',
            listeners: {                
                rootchange: 'onTreeStoreRootChange'
            }
        }
    }

});