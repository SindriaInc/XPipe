Ext.define('CMDBuildUI.view.fields.schedulerdate.SchedulerDateFieldModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.fields-schedulerdate-schedulerdatefield',
    formulas: {

        //This bind handles the visibility of the scheduler trigger
        triggerVisibility: {
            bind: '{schedulerdatefield.triggerStore}', //Contains all the triggers for that class and not for the single attribute
            get: function (triggerStore) {
                if (triggerStore) {
                    var triggersData = triggerStore.getRange();
                    var view = this.getView();

                    for (var i = 0; i < triggersData.length; i++) {
                        triggerData = triggersData[i];
                        if (triggerData.get('active') && triggerData.get('ownerAttr') == view.getName() && triggerData.get('sequenceParamsEditMode') !== 'hidden') {
                            var trigger = view.getTrigger('scheduler');
                            trigger.setHidden(false);
                            return;
                        }
                    }
                    return true;
                }
            }
        },

        triggerCondition: {
            bind: '{schedulerdatefield.triggerStore}',
            get: function (triggerStore) {
                if (triggerStore) {
                    triggersData = triggerStore.getRange();
                    var view = this.getView();

                    triggersData.forEach(function (trigger) {
                        var conditionScript = null;
                        if (trigger.get('active') && trigger.get('ownerAttr') == view.getName())
                            conditionScript = trigger.get('conditionScript');
                        if (!Ext.isEmpty(conditionScript)) {
                            this.getView().addConditionalTriggerVisibilityRules(trigger);
                        }
                    }, this);

                    if (view._enumConditions == 0) {
                        //if the component doesn't have conditions on fields is considered initialized otherwise is initialized after the first event fired SchedulerDateField.js#140
                        view.setInitialized(true); 
                    }
                }
            }
        },

        updateObjectTypeName: {
            bind: '{schedulerdatefield.theObject}',
            get: function (theObject) {
                if (theObject) {
                    var model = CMDBuildUI.util.helper.ModelHelper.getModelFromName(theObject.$className);
                    this.set('objectTypeName', model.objectTypeName);
                }
            }
        },

        sequenceStoreFilters: {
            bind: {
                theObject: '{schedulerdatefield.theObject}'
            },
            get: function (data) {
                if (data.theObject) {
                    var sequence = data.theObject.sequences();
                    sequence.setFilters[{
                        property: 'trigger',
                        operator: '!=',
                        value: 0
                    }];
                }
            }
        }
    }

});
