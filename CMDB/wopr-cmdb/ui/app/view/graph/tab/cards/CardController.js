Ext.define('CMDBuildUI.view.graph.tab.cards.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-tab-cards-card',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.graph.tab.cards.Card} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel();
        vm.bind("{selectedNode}", function (selectedNode) {
            if (!selectedNode || selectedNode.length === 0) return;

            // Set the value for the label
            const type = selectedNode[0].type;
            var objectname = type,
                dataField = {};
            if (type.includes('compound_')) {
                objectname = type.replace('compound_', '');
            }

            const defaultField = {
                xtype: 'displayfield',
                labelAlign: "left",
                labelWidth: 'auto',
                cls: Ext.baseCSSPrefix + 'process-action-field',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                fieldLabel: "",
                value: CMDBuildUI.util.helper.ModelHelper.getObjectDescription(objectname)
            };

            if (CMDBuildUI.util.helper.ModelHelper.getClassFromName(type)) {
                defaultField.fieldLabel = CMDBuildUI.locales.Locales.relationGraph.class;
                dataField = {
                    xtype: 'classes-cards-card-view',
                    shownInPopup: true,
                    hideTools: true,
                    hideWidgets: true,
                    viewModel: {
                        data: {
                            objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                            objectTypeName: type,
                            objectId: selectedNode[0].id
                        }
                    }
                };
            } else if (CMDBuildUI.util.helper.ModelHelper.getProcessFromName(type)) {
                defaultField.fieldLabel = CMDBuildUI.locales.Locales.relationGraph.activity;
            } else if (type.includes('compound')) {
                defaultField.fieldLabel = CMDBuildUI.locales.Locales.relationGraph.compoundnode;
            } else {
                console.error('Should never be here');
            }

            view.removeAll();
            view.add(defaultField, dataField);
        });
    }
});