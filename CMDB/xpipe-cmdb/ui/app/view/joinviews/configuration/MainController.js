Ext.define('CMDBuildUI.view.joinviews.configuration.MainController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-configuration-main',

    control: {
        '#': {
            validitychange: 'onValidityChange'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggle'
        },
        '#disableBtn': {
            click: 'onActiveToggle'
        },
        '#prevBtn': {
            click: 'onPrevBtnClick'
        },
        '#nextBtn': {
            click: 'onNextBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @event
     * @param {Ext.form.Panel} form 
     * @param {Boolean} valid 
     * @param {Object} eOpts 
     */
    onValidityChange: function (form, valid, eOpts) {
        var vm = this.getViewModel();
        if (vm.get('currentStep') !== 5) {
            Ext.asap(function () {
                if (form && !form.destroyed) {
                    vm.set('stepNavigationLocked', !valid);
                }
            });
        }
    },

    /**
     * @event
     * @param {Ext.button.Button} button 
     */
    onEditBtnClick: function (button) {
        var vm = button.lookupViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },
    /**
     * @event
     * @param {Ext.button.Button} button 
     */
    onDeleteBtnClick: function (button) {
        this.getView().fireEventArgs('deletejoinview', [button]);
    },

    /**
     * @event
     * @param {Ext.button.Button} button 
     */
    onPrevBtnClick: function (button) {
        var view = this.getView();
        var vm = button.lookupViewModel();
        var activeView = view.items.getAt(vm.get('currentStep'));
        if (!activeView.goingPreviousStep || (activeView.goingPreviousStep && activeView.goingPreviousStep())) {
            vm.set('currentStepWas', vm.get('currentStep'));
            vm.set('currentStep', vm.get('currentStep') - 1);
        } else {
            // TODO show message or something
        }
    },

    /**
     * @event
     * @param {Ext.button.Button} button 
     */
    onNextBtnClick: function (button) {
        var view = this.getView();
        var vm = button.lookupViewModel();
        var activeView = view.items.getAt(vm.get('currentStep'));
        if (!activeView.goingNextStep || (activeView.goingNextStep && activeView.goingNextStep())) {
            vm.set('currentStepWas', vm.get('currentStep'));
            vm.set('currentStep', vm.get('currentStep') + 1);
        } else {
            // TODO show message or something
        }
    },

    onActiveToggle: function (button) {
        var vm = this.getViewModel();
        vm.set('theView.active', !vm.get('theView.active'));
        this.onSaveBtnClick(button);
    },
    /**
     * @event
     * @param {Ext.button.Button} button 
     */
    onSaveBtnClick: function (button) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        button.setDisabled(true);
        CMDBuildUI.util.Utilities.showLoader(true);

        var me = this,
            view = me.getView(),
            vm = view.lookupViewModel(),
            theView = vm.get('theView'),
            joinData = view.getJoinData(),
            attributes = view.getAttributesData(),
            attributeGroups = view.getAttributesGroups(),
            sorter = view.getSorter(),
            filter = view.getFilterData(),
            contextMenuItems = [];

        theView.contextMenuItems().each(function (record, index) {
            var data = record.getData();
            delete data.id;
            delete data._id;
            contextMenuItems.push(data);
        });
        theView.set('join', joinData);
        theView.set('attributes', attributes);
        theView.set('attributeGroups', attributeGroups);
        theView.set('sorter', sorter);
        theView.set('filter', filter);
        theView.set('contextMenuItems', contextMenuItems);
        CMDBuildUI.util.Ajax.setActionId(Ext.String.format('save-{0}-joinview', vm.get('action')));
        theView.save({
            success: function (record, operation) {
                me.saveLocales(Ext.copy(vm), record);
                view.fireEventArgs('saved', [vm.get('action'), record, operation]);
            },
            failure: function (record, operation) {
                if (button && !button.destroyed) {
                    button.setDisabled(false);
                }
            },
            callback: function (record, operation, success) {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.showLoader(false);
            }
        });


    },

    /**
     * @event
     * @param {Ext.button.Button} button 
     */
    onCancelBtnClick: function (button) {
        var me = this,
            view = me.getView(),
            vm = view.lookupViewModel(),
            theView = vm.get('theView');
        view.fireEventArgs('cancel', [vm.get('action'), theView]);
    },

    /**
     * @event
     * @param {CMDBuildUI.view.fields.allelementscombo.AllelementsCombo} input 
     * @param {String} newClassName 
     * @param {String} oldClassName 
     */
    onClassChange: function (input, newClassName, oldClassName) {
        var vm = input.lookupViewModel();
        var allAttributesStore = vm.get('allAttributesStore');
        var klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(newClassName);

        if (oldClassName) {
            // clean all old attributes
            vm.get('theView').attributes().removeAll();
            allAttributesStore.removeAll();
        }
        if (input.lookupViewModel().get('actions.add')) {
            if (!oldClassName || !vm.get('theView.masterClassAlias')) {
                // // set masterClassAlias
                vm.set('theView.masterClassAlias', newClassName);
            }
        }
        if (klass) {
            // all class attributes
            klass.getAttributes().then(function (classAttributesStore) {
                allAttributesStore.beginUpdate();
                classAttributesStore.each(function (attribute) {
                    var expr = Ext.String.format('{0}.{1}', vm.get('theView.masterClassAlias'), attribute.get('name'));
                    var storeAlreadyContain = allAttributesStore.findRecord('expr', expr, 0, false, true);
                    if (attribute.canAdminShow() && !storeAlreadyContain && attribute.get('active') && attribute.get('type') !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula) {
                        allAttributesStore.addSorted(CMDBuildUI.model.views.JoinViewAttribute.create({
                            _deepIndex: String.fromCharCode(36) + '_' + vm.get('theView.masterClassAlias'), // first when sorted in sorter
                            targetAlias: vm.get('theView.masterClassAlias'),
                            targetType: vm.get('theView.masterClass'),
                            expr: expr,
                            name: '',
                            description: '',
                            group: '',
                            showInGrid: false,
                            showInReducedGrid: false,
                            _attributeDescription: attribute.getTranslatedDescription(),
                            attributeconf: attribute.getData(),
                            cmdbuildtype: attribute.get('type')
                        }));
                    } else if (storeAlreadyContain) {
                        storeAlreadyContain.set('_deepIndex', String.fromCharCode(36) + '_' + storeAlreadyContain.get('targetAlias'));
                        storeAlreadyContain.set('_attributeDescription', attribute.getTranslatedDescription());
                        storeAlreadyContain.set('attributeconf', attribute.getData());
                        storeAlreadyContain.set('cmdbuildtype', attribute.get('type'));
                    }
                });
                allAttributesStore.endUpdate();
            });
        }
    },

    /**
     * @event
     * @param {Ext.form.field.Text} input 
     * @param {String} newValue 
     * @param {String} oldValue 
     */
    onClassAliasChange: function (input, newValue, oldValue) {
        this.fireEventToAllItems('classaliaschange', [input, newValue, oldValue]);
    },

    /**
     * @event
     * @param {Ext.data.Model} node 
     * @param {Object} context 
     */
    onDomainChange: function (node, context) {
        this.fireEventToAllItems('domainchange', [node, context]);
    },

    /**
     * @event
     * @param {Ext.data.Model} node 
     * @param {Object} context 
     */
    onDomainCheckChange: function (node, context) {
        this.fireEventToAllItems('domaincheckchange', [node, context]);
    },

    /**
     * @event
     * @param {Ext.data.Model} attributeGroup 
     */
    onAttributeGroupsChanged: function (attributeGroup) {
        this.fireEventToAllItems('attributegruopchanged', [attributeGroup]);
    },

    /**
     * @event
     * @param {Ext.data.Model} attributeGroup 
     */
    onAttributeGroupsRemoved: function (attributeGroup) {
        this.fireEventToAllItems('attributegruopremoved', [attributeGroup]);
    },


    allAttributesStoreOnEndUpdate: function () {
        var me = this.getView();
        var store = me.getViewModel().get('allAttributesStore');
        if (me.lastAppendedAttributeTimeout) {
            me.lastAppendedAttributeTimeout.cancel();
        }
        me.lastAppendedAttributeTimeout = new Ext.util.DelayedTask(function () {
            if (store.getData() && store.getGroupField()) {
                store.getData().setGrouper(store.getGroupField());
            }
        });
        me.lastAppendedAttributeTimeout.delay(250);

    },
    privates: {
        lastAppendedAttributeTimeout: null,
        /**
         * 
         * @param {String} event 
         * @param {Array} parameters 
         */
        fireEventToAllItems: function (event, parameters) {
            var me = this;
            me.getView().items.each(function (item) {
                if (item.down("#attributesfilterpanel")) {
                    item = item.down("#attributesfilterpanel");
                }
                item.fireEventArgs(event, parameters);
            });
        },

        saveLocales: function (vm, record) {

            var descriptionTranslation = vm.get('theDescriptionTranslation');
            if (descriptionTranslation) {
                descriptionTranslation.phantom = false;
                descriptionTranslation.crudState = 'U';
                descriptionTranslation.crudStateWas = 'U';
                delete descriptionTranslation.data._id;
                descriptionTranslation.save();
            }

            record.attributeGroups().each(function (attributeGroup) {
                var translation = vm.get(Ext.String.format('theGroupingDescriptionTranslation_{0}', CMDBuildUI.util.Utilities.stringToHex(attributeGroup.get('name'))));
                if (translation) {
                    translation.phantom = false;
                    translation.crudState = 'U';
                    translation.crudStateWas = 'U';
                    delete translation.data._id;
                    translation.save();
                }
            });
            record.attributes().each(function (attribute) {
                var translation = vm.get(Ext.String.format('theAttributeDescriptionTranslation_{0}', attribute.get('name')));
                if (translation) {
                    translation.phantom = false;
                    translation.crudState = 'U';
                    translation.crudStateWas = 'U';
                    delete translation.data._id;
                    translation.save();
                }
            });

            record.contextMenuItems().each(function (contextMenu) {
                var deferred = new Ext.Deferred();
                var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassContextMenuItem(vm.get('theView').get('name'), contextMenu.get('label'));
                // save the translation
                var vmObject = vm.get('theContextMenuTranslation_' + CMDBuildUI.util.Utilities.stringToHex(contextMenu.get('label')));
                if (vmObject) {
                    CMDBuildUI.util.Ajax.setActionId('joinview.contextmenu_translation');
                    vmObject.crudState = 'U';
                    vmObject.crudStateWas = 'U';
                    vmObject.phantom = false;
                    vmObject.set('_id', key);
                    vmObject.save({
                        success: function (translations, operation) {
                            deferred.resolve();
                        }
                    });
                }
                return deferred;
            });
        }

    }
});