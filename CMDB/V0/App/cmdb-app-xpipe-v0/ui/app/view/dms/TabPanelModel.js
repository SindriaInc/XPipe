Ext.define('CMDBuildUI.view.dms.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dms-tabpanel',
    data: {
        'dms-tabpanel': {
            DMSCategoryValue: null,
            DMSModelClassName: null,
            DMSModelClass: null,
            DMSClass: null,
            attachmentId: null
        },
        schedulesHidden: true
    },
    formulas: {
        DMSModelClassNameCalculation: {
            bind: {
                DMSCategoryType: '{dms-container.DMSCategoryType}',
                DMSCategoryTypeValue: '{dms-tabpanel.DMSCategoryValue}'
            },
            get: function (data) {
                if (data.DMSCategoryType && data.DMSCategoryTypeValue) {
                    var categoryValues = data.DMSCategoryType.values();
                    var r = categoryValues.findRecord('_id', data.DMSCategoryTypeValue);

                    if (r) {
                        var DMSModelClassName = r.get('modelClass') || 'BaseDocument';
                        this.getView().setDMSModelClassName(DMSModelClassName);
                    }
                }
            }
        },

        DMSmodelClassCalculation: {
            bind: '{dms-tabpanel.DMSModelClassName}',
            get: function (modelClassName) {
                if (modelClassName) {
                    CMDBuildUI.util.helper.ModelHelper.getModel(
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                        modelClassName
                    ).then(function (model) {
                        if (this.getView()) {
                            this.getView().setDMSModelClass(model);
                        }
                    }, Ext.emptyFn, Ext.emptyFn, this);
                }
            }
        },

        DMSClassCalculation: {
            bind: '{dms-tabpanel.DMSModelClassName}',
            get: function (DMSModelClassName) {
                //only after the model is calculated

                if (DMSModelClassName) {
                    var DMSClass = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(DMSModelClassName);
                    this.getView().setDMSClass(DMSClass);
                }
            }
        },

        isRecordPhantom: {
            bind: {
                record: '{record}'
            },
            get: function(data) {
                return data.record && data.record.phantom;
            }
        },

        schedulesHidden: {
            bind: {
                ignoreSchedules: '{dms-container.ignoreSchedules}',
                hasTriggers: '{dms-tabpanel.DMSClass._hasTriggers}'
            },
            get: function (data) {
                return (!CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.scheduler.enabled) || (!data.hasTriggers || data.ignoreSchedules));
            }
        },
        schedulesHeight: {
            bind: {
                hidden: '{schedulesHidden}'
            },
            get: function (data) {
                if (!data.hidden) {
                    var view = this.getView();
                    return view.up('dms-container').getHeight();
                }
            }
        },

        eventsStore: {
            bind: {
                dmsModelAttachmentId: '{dms-tabpanel.dmsModelAttachmentId}',
                schedulesHidden: '{schedulesHidden}'
            },
            get: function (data) {
                var store;
                if (data.dmsModelAttachmentId && data.schedulesHidden == false) {

                    store = Ext.create('Ext.data.BufferedStore', {
                        model: 'CMDBuildUI.model.calendar.Event',
                        pageSize: 50,
                        leadingBufferZone: 100,
                        sorters:[{
                            property: 'date',
                            direction:"ASC"
                        }],
                        proxy: {
                            type: 'baseproxy',
                            url: '/calendar/events',
                            extraParams: {
                                // detailed: true
                            }
                        }
                    });
                    var filter = store.getAdvancedFilter();
                    filter.addAttributeFilter(
                        'card',
                        CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                        data.dmsModelAttachmentId
                    );
                    store.load();
                }
                return store;
            }
        }
    }
});
