Ext.define('CMDBuildUI.view.joinviews.configuration.items.AttributesChoiceController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-configuration-items-attributeschoice',

    control: {
        '#': {
            classaliaschange: 'onClassAliasChange',
            domainchange: 'onDomainChange',
            domaincheckchange: 'onDomainCheckChange'
        },
        '#attributegrid': {
            beforedeselect: 'onBeforeDeselect',
            deselect: 'onDeselect',
            beforeselect: 'onBeforeSelect',
            select: 'onSelect'
        },
        '#checkedonly': {
            toggle: 'onToggleButton'
        }
    },

    /**
     * @event
     * @param {Ext.form.field.Text} input 
     * @param {String} newValue 
     * @param {String} oldValue 
     */
    onClassAliasChange: function (input, newValue, oldValue) {
        var grid = this.getView().down('#attributegrid'),
            store = grid.getStore();
        // set new targetAlias and update expr
        store.each(function (item) {
            if (item.get('targetAlias') === oldValue) {
                item.set('targetAlias', newValue);
                item.set('expr', item.get('expr').replace(oldValue, newValue));
            }
        });
    },

    /**
     * @event
     * @param {Ext.data.Model} node 
     * @param {Object} context 
     */
    onDomainChange: function (record, context, eOpts) {
        var grid = this.getView().down('#attributegrid'),
            store = grid.getStore(),
            mainView = grid.up('joinviews-configuration-main');

        switch (context.field) {
            case 'targetAlias':
                // set new targetAlias and update expr
                store.each(function (item) {
                    if (item.get('targetAlias') === record.getPrevious('targetAlias')) {
                        item.set('targetAlias', record.get('targetAlias'));
                        item.set('expr', item.get('expr').replace(record.getPrevious('targetAlias'), record.get('targetAlias')));
                    }
                });
                break;

            case 'targetType':
                // get all record of targetType class
                var recordsStoreToRemove = store.queryBy(function (item) {
                    if (item.get('targetAlias') === record.getPrevious('targetAlias')) {
                        // remove stored alias of attribute
                        mainView.clearAliasIndex(mainView.aliasType.attribute, record.get('name'));
                        return true;
                    }
                    return false;
                });
                // remove all attributes of targetType class
                store.remove(recordsStoreToRemove.getRange());
                recordsStoreToRemove.destroy();
                this.addTargetAttributes(record, store);
                break;

            default:
                break;
        }

        grid.getView().refresh();
    },

    onDomainCheckChange: function (node, ctx) {
        var vm = this.getViewModel(),
            store = vm.get('allAttributesStore');

        if (node.get('checked')) {
            // add attributes of targetClass to grid                   
            this.addTargetAttributes(node, store);
        } else {
            var recordsStoreToRemove = store.queryBy(function (item) {
                return item.get('targetAlias') === node.getPrevious('targetAlias');
            });
            store.remove(recordsStoreToRemove.getRange());
            recordsStoreToRemove.destroy();
        }
    },

    onBeforeDeselect: function (grid, record) {
        if (this.getViewModel().get('actions.view')) {
            return false;
        }
        return true;
    },

    onDeselect: function (grid, record, rowIndex, eOpts) {
        var view = this.getView(),
            mainView = view.up('joinviews-configuration-main'),
            vm = mainView.lookupViewModel();

        vm.get('theView').attributes().remove(record);
        vm.get("attributesSelectedStore").remove(record);
        mainView.clearAliasIndex(mainView.aliasType.attribute, record.get('name'));
        record.set('description', '');
        record.set('name', '');
        record.set('showInGrid', false);
        record.set('showInReducedGrid', false);
        record.set('group', '');

        Ext.asap(function () {
            var gridSelection = grid.getSelection();
            vm.set('selectedAttributes', gridSelection);
            if (gridSelection.length === 0) {
                view.down("#warningattribute").show();
            }
        });
    },

    onBeforeSelect: function (grid, record) {
        var vm = this.getViewModel();

        if (vm.get('actions.view') && !vm.get('theView.attributes').findRecord('expr', record.get('expr'), 0, false, true)) {
            return false;
        }
        return true;
    },

    onSelect: function (grid, record, rowIndex, eOpts) {
        var view = this.getView(),
            mainView = view.up('joinviews-configuration-main'),
            vm = mainView.lookupViewModel(),
            attributeName = record.get('expr').split('.')[1],
            isAlredyInSelection = Ext.Array.findBy(vm.get('selectedAttributes'), function (selected) {
                return selected.get('expr') === record.get('expr');
            });

        if (isAlredyInSelection) {
            mainView.addAliasFromExisisting(mainView.aliasType.attribute, record.get('name'));
        } else {
            var nameAliasIndex = mainView.getNewAliasIndex(mainView.aliasType.attribute, attributeName);
            record.set('name', Ext.String.format('{0}{1}', attributeName, nameAliasIndex ? Ext.String.format('_{0}', nameAliasIndex) : ''));
        }

        vm.get("attributesSelectedStore").add(record);
        vm.get('theView').attributes().add(record);

        Ext.asap(function () {
            var gridSelection = grid.getSelection();
            vm.set('selectedAttributes', gridSelection);
            if (gridSelection.length !== 0) {
                view.down("#warningattribute").hide();
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Boolean} selected
     * @param {Object} eOpts
     */
    onToggleButton: function (button, selected, eOpts) {
        var grid = this.getView().down("#attributegrid"),
            store = grid.getStore();
        if (selected) {
            store.filter({
                property: 'id',
                operator: 'in',
                value: Ext.Array.pluck(grid.getSelection(), 'id')
            });
        } else {
            store.clearFilter();
        }
    },

    privates: {

        domaintree: null,

        getDeepIndex: function (node) {
            if (!this.domaintree) {
                this.domaintree = this.getView().up('#joinviews-configuration-main').down('#domainstree');
            }
            var index = '';
            if (!node) {
                return String.fromCharCode(65);
            }
            if (node.parentNode && !node.parentNode.get('root')) {
                index += this.getDeepIndex(node.parentNode);
            }
            try {
                index += String.fromCharCode(66 + Number(this.domaintree.getView().getNodeById(node.internalId).dataset.recordindex));
            } catch (e) {
                index += String.fromCharCode(66);
            }
            return index;
        },

        addTargetAttributes: function (node, store) {
            var me = this;

            function manageAttributes(attribute, targetAlias, targetType) {
                var expr = Ext.String.format('{0}.{1}', targetAlias, attribute.get('name')),
                    storeAlreadyContain = store.findRecord('expr', expr, 0, false, true),
                    attributeType = attribute.get('type'),
                    typeFormula = attributeType === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula;

                if (storeAlreadyContain) {
                    storeAlreadyContain.set('_deepIndex', me.getDeepIndex(node) + '_' + targetAlias);
                    storeAlreadyContain.set('_attributeDescription', attribute.getTranslatedDescription());
                    storeAlreadyContain.set('attributeconf', attribute.getData());
                    storeAlreadyContain.set('cmdbuildtype', attributeType);
                } else if (attribute.canAdminShow() && attribute.get('active') && !typeFormula) {
                    var joinViewAttribute = CMDBuildUI.model.views.JoinViewAttribute.create({
                        _deepIndex: me.getDeepIndex(node) + '_' + targetAlias,
                        targetAlias: targetAlias,
                        targetType: targetType,
                        expr: expr,
                        name: '',
                        description: '',
                        group: '',
                        showInGrid: false,
                        showInReducedGrid: false,
                        _attributeDescription: attribute.getTranslatedDescription(),
                        _select: false,
                        attributeconf: attribute.getData(),
                        cmdbuildtype: attributeType
                    });
                    store.addSorted(joinViewAttribute);
                }
            }

            var targetClass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(node.get('targetType'));
            targetClass.getAttributes().then(function (attributesStore) {
                attributesStore.each(function (attribute) {
                    manageAttributes(attribute, node.get('targetAlias'), node.get('targetType'));
                });
            });

            var domain = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(node.get('domain'), CMDBuildUI.util.helper.ModelHelper.objecttypes.domain);
            domain.getAttributes().then(function (attributesStore) {
                attributesStore.each(function (attribute) {
                    manageAttributes(attribute, node.get('domainAlias'), node.get('domain'));
                });
            });
        }
    }

});