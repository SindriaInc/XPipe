Ext.define('CMDBuildUI.view.dms.attachment.Mixin', {
    mixinId: 'dms-mixin',

    mixins: ['CMDBuildUI.mixins.forms.FormTriggers'],

    config: {

        objectType: {
            $value: null,
            evented: true
        },
        objectTypeName: {
            $value: null,
            evented: true
        },
        objectId: {
            $value: null,
            evented: true
        },
        attachmentId: {
            $value: null,
            evented: true
        },
        theObject: {
            $value: null,
            evented: true
        },
        DMSCategoryTypeName: {
            $value: null,
            evented: true
        },
        /**
         * @cfg {String} the DMS Category description
         */
        DMSCategoryDescription: null,
        DMSCategoryValue: {
            $value: null,
            evented: true
        },

        //calculated starting from DMSCategoryTypeName && DMSCategoryValue
        DMSModelClassName: {
            $value: null,
            evented: true
        },

        //calculated starting from DMSModelClassName
        DMSModelClass: {
            $value: null,
            evented: true
        },

        //calculated starting from DMSModelClassName
        DMSClass: {
            $value: null,
            evented: true
        },
        ignoreSchedules: false,

        /**
         * @cfg {Ext.data.Store}
         */
        asyncStore: null,

        /**
         * @cfg {String []} invalidFileNames
         */
        invalidFileNames: [],

        /**
         * @cfg {String} currentFileName
         */
        currentFileName: null
    },

    /**
     * @property {String} formmode
     * Override this property in each form. 
     * Used in common functions to know form mode.
     */
    formmode: null,

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    /**
     * Add rules for fields visibility
     *
     * @deprecated
     */
    addConditionalVisibilityRules: Ext.emptyFn,

    /**
     * Add rules for fields visibility
     *
     * @deprecated
     */
    addAutoValueRules: Ext.emptyFn,

    /**
     * Initialize before action form triggers.
     * 
     * @param {String} action 
     * @param {Object} base_api 
     */
    initBeforeActionFormTriggers: function (action, base_api) {
        var me = this;
        var vm = this.getViewModel();
        var model = me.getDMSModelClass();
        var item = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(model.objectTypeName);
        if (item) {
            // get form triggers
            var triggers = item.getFormTriggersForAction(action);
            if (triggers && triggers.length) {
                // bind object creation
                vm.bind({
                    bindTo: {
                        theObject: '{' + me.getReference() + '.theObject}'
                    }
                }, function (data) {
                    var api = Ext.apply({
                        record: data.theObject
                    }, base_api);
                    me.executeFormTriggers(triggers, api);
                });
            }
        }
    },

    /**
     * Execute after action form triggers.
     * 
     * @param {String} action 
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {Object} base_api 
     */
    executeAfterActionFormTriggers: function (action, record, base_api) {
        var me = this;
        var model = me.getDMSModelClass();
        var item = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(model.objectTypeName);
        if (item) {
            // get form triggers
            var triggers = item.getFormTriggersForAction(action);
            if (triggers && triggers.length) {
                var api = Ext.apply({
                    record: record
                }, base_api);
                me.executeFormTriggers(triggers, api);
            }
        }
    },

    /**
     * 
     * @param {Object} base_api 
     * @return {Object}
     */
    getApiForTrigger: function (base_api) {
        base_api._attachmentOwner = {
            type: this.getObjectType(),
            typeName: this.getObjectTypeName(),
            id: this.getObjectId()
        }
        return base_api;
    },

    /**
     * @return {Boolean}
     */
    isAsyncSave: function () {
        return !Ext.isEmpty(this.getAsyncStore());
    },

    privates: {

        /**
         * 
         * @param {Object[]} items
         */
        getFormItems: function (items) {
            var me = this;

            // add notes fieldset
            items.push(me.getInlineNotesConfig());

            return {
                flex: 1,
                layout: {
                    type: 'hbox',
                    align: 'stretch' //stretch vertically to parent
                },
                height: "100%",
                items: [{
                    flex: 1,
                    scrollable: 'y',
                    items: [{
                        items: items
                    }]
                }, {
                    xtype: 'widgets-launchers',
                    formMode: me.formmode,
                    padding: 10,
                    targetLinkName: me.getReference() + '.theObject',
                    bind: {
                        widgets: '{DMSWidgets}'
                    }
                }]
            };
        },

        /**
         * 
         * @param {Boolean} collapsed
         * @return {Object}
         */
        getInlineNotesConfig: function (collapsed) {
            var field;
            var bindvalue = '{' + this.getReference() + '.theObject.Notes}';
            var containerbind = {};
            var viewModel = null;
            var containerhidden = false;
            var collapsed = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(this.getDMSModelClass().objectTypeName).get("noteInlineClosed");
            if (this.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.read) {
                field = {
                    xtype: 'displayfield',
                    bind: {
                        value: bindvalue
                    }
                };
                containerbind = {
                    hidden: '{notesHidden}'
                };
                viewModel = {
                    data: {
                        notesHidden: true
                    },
                    formulas: {
                        notesHidden: {
                            bind: bindvalue,
                            get: function (value) {
                                return Ext.isEmpty(value);
                            }
                        }
                    }
                };
                containerhidden = true;
            } else {
                field = CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                    bind: {
                        value: bindvalue
                    }
                });
            }
            return {
                xtype: 'formpaginationfieldset',
                title: CMDBuildUI.locales.Locales.common.tabs.notes,
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                collapsed: collapsed,
                collapsible: true,
                hidden: containerhidden,
                items: [field],
                bind: containerbind,
                viewModel: viewModel ? viewModel : null
            };
        },

        /**
         * 
         * @param {String} DMSCategoryTypeName 
         * @param {Number} DMSCategoryValue 
         * @param {Ext.data.model} DMSModelClass 
         * @returns {Number} The size allowed max size.          
         */
        getMaxFileSize: function (DMSCategoryTypeName, DMSCategoryValue, DMSModelClass) {
            var maxFileSize = null;
            if (DMSCategoryTypeName && DMSCategoryValue && DMSModelClass) {
                var lk = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(DMSCategoryTypeName);

                var lookupValuesStore = lk.values();
                //we suppose that the lookupValues for the lookupType are already loaded.
                //If not make this function asincronous or load lookupValues febore calling this function

                var lookupValue = lookupValuesStore.getById(DMSCategoryValue);
                if (lookupValue) {

                    maxFileSize = lookupValue.get('maxFileSize');

                    if (Ext.isEmpty(maxFileSize)) {
                        var modelName = DMSModelClass.objectTypeName;
                        var DMSClass = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(modelName);

                        maxFileSize = DMSClass.get('maxFileSize');
                        if (Ext.isEmpty(maxFileSize)) {
                            if (Ext.isEmpty(maxFileSize)) {
                                maxFileSize = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.maxfilesize);
                            }
                        }
                    }
                }
            }
            CMDBuildUI.util.Logger.log("maxFileSize = " + Number(maxFileSize), CMDBuildUI.util.Logger.levels.debug);
            return Number(maxFileSize);
        }
    }
});