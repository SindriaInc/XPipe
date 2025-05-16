/**
 * @file CMDBuildUI.util.helper.FormHelper
 * @module CMDBuildUI.util.helper.FormHelper
 * @author Tecnoteca srl
 * @access public
 */

/**
 * @typedef DefaultValue
 * @type {Object}
 * @property {String|Number|Boolean} value Field default value.
 * @property {String} valuedescription Default description. Used for Reference and Lookup fields.
 * @property {Boolean} editable Override for editable property.
 */
Ext.define('CMDBuildUI.util.helper.FormHelper', {
    singleton: true,

    /**
     * @constant {Object} formmodes Available form modes.
     * @property {String} create Create mode.
     * @property {String} update Update/edit mode.
     * @property {String} read Read mode.
     *
     */
    formmodes: {
        create: 'create',
        update: 'update',
        read: 'read'
    },

    /**
     * @constant {Object} fieldmodes Available field modes.
     * @property {String} hidden Hidden mode.
     * @property {String} immutable Immutable mode.
     * @property {String} read Read mode.
     * @property {String} write Writable mode.
     *
     */
    fieldmodes: {
        hidden: 'hidden',
        immutable: 'immutable',
        read: 'read',
        write: 'write'
    },

    /**
     * @constant {Object} formtriggeractions Available form trigger actions.
     * @property {String} afterInsert After create action.
     * @property {String} afterInsertExecute After create and execute process action.
     * @property {String} beforeInsert Before create action.
     * @property {String} afterEdit After edit action.
     * @property {String} afterEditExecute After edit and execute process action.
     * @property {String} beforeEdit Before edit action.
     * @property {String} afterClone After clone action.
     * @property {String} beforeClone Before clone action.
     * @property {String} afterDelete After delete action.
     *
     */
    formtriggeractions: {
        afterInsert: 'afterInsert',
        afterInsertExecute: 'afterInsertExecute',
        beforeInsert: 'beforeInsert',
        afterEdit: 'afterEdit',
        afterEditExecute: 'afterEditExecute',
        beforeEdit: 'beforeEdit',
        afterClone: 'afterClone',
        beforeClone: 'beforeClone',
        afterDelete: 'afterDelete'
    },

    /**
     * @constant {Object} properties Properties to use in views.
     * @property {String} padding Value for standard padding.
     *
     */
    properties: {
        padding: '0 15 0 15'
    },

    /**
     * @constant {Object} editortypes Type of editors to use.
     * @property {String} plain Editor plain text.
     * @property {String} html Editor html.
     * @property {String} markdown Editor markdown.
     */
    editortypes: {
        plain: 'PLAIN',
        html: 'HTML',
        markdown: 'MARKDOWN'
    },

    /**
     * @constant {Object} fieldDefaults Default values for fieldDefaults property.
     * @property {String} labelAlign Label align. Value: "top"
     * @property {Numeric} labelPad Label pad. Value: 2
     * @property {String} labelSeparator Label separator. Value ""
     * @property {String} anchor Anchor. Value: "100%"
     *
     */
    fieldDefaults: {
        labelAlign: 'top',
        labelPad: 2,
        labelSeparator: '',
        anchor: '100%'
    },

    /**
     * @private
     *
     * @param {String} fieldname
     *
     * @returns {String} The store id
     */
    getStoreId: function (fieldname) {
        return fieldname + "Store";
    },

    /**
     * Get form fields definition from the Class/Process/View/... model.
     *
     * @param {Ext.data.Model} model
     * @param {Object} config
     * @param {String} config.mode Form mode. Default: CMDBuildUI.util.helper.FormHelper.fieldmodes.read
     * @param {DefaultValue[]} config.defaultValues An array of objects containing default values.
     * @param {String} config.linkName The name of the object linked within the ViewModel.
     * @param {String} config.activityLinkName The name of the activity linked within the ViewModel. Used for process instances.
     * @param {Boolean} config.ignoreSchedules Ignore schedules generation for date field with 'schedules rule definition' associated.
     * @param {Object} config.attributesOverrides An object containing properties to override for attributes.
     * @param {Boolean} config.attributesOverrides.attributename.writable Override writable property.
     * @param {Boolean} config.attributesOverrides.attributename.mandatory Override mandatory property.
     * @param {Boolean} config.attributesOverrides.attributename.hidden Override writable property.
     * @param {Numeric} config.attributesOverrides.attributename.index Override index property.
     *
     * @returns {Object[]} the list of form fields
     *
     */
    getFormFields: function (model, config) {
        var items = [];
        config = config || {};
        var me = this;

        // set default configuration
        Ext.applyIf(config, {
            readonly: false,
            attributesOverrides: {},
            mode: config.readonly ? this.formmodes.read : this.formmodes.update
        });

        Ext.Array.each(model.getFields(), function (modelField, index) {
            var field = Ext.merge({}, modelField);
            if (!Ext.String.startsWith(field.name, "_")) {
                var cmdbuildtype = field.cmdbuildtype.toLowerCase(),
                    attributeconf = field.attributeconf,
                    defaultValue;
                // check and use default values
                if (!Ext.isEmpty(config.defaultValues)) {
                    defaultValue = Ext.Array.findBy(config.defaultValues, function (item, index) {
                        if (item.value) {
                            if (cmdbuildtype === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase()) {
                                return (item.attribute && item.attribute === field.attributename) ||
                                    (item.domain && item.domain === attributeconf.domain);
                            } else if (cmdbuildtype === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase()) {
                                return attributeconf.targetClass === item.destination || (item.attribute && item.attribute === field.attributename);
                            } else {
                                return item.attribute && item.attribute === field.attributename;
                            }
                        }
                    });
                }

                var overrides = config.attributesOverrides[field.name];
                if (overrides) {
                    if (!Ext.isEmpty(overrides.index)) {
                        attributeconf.index = overrides.index;
                    }
                    if (!Ext.isEmpty(overrides.mandatory)) {
                        field.mandatory = overrides.mandatory;
                    }
                    if (!Ext.isEmpty(overrides.writable)) {
                        field.writable = overrides.writable;
                    }
                    if (!Ext.isEmpty(overrides.hidden)) {
                        field.hidden = overrides.hidden;
                    }
                }

                var formfield = me.getFormField(field, {
                    mode: config.mode,
                    defaultValue: defaultValue,
                    linkName: config.linkName,
                    activityLinkName: config.activityLinkName,
                    filterLinkName: config.filterLinkName,
                    ignoreUpdateVisibilityToField: config.ignoreUpdateVisibilityToField,
                    ignoreCustomValidator: config.ignoreCustomValidator,
                    ignoreAutovalue: config.ignoreAutovalue,
                    ignoreSchedules: config.ignoreSchedules
                });

                items.push(formfield);
            }
        });

        // sort attributes on index property
        return items.sort(function (a, b) {
            return a.metadata.index - b.metadata.index;
        });
    },

    /**
     * Get form field definition from the Class/Process/View/... model field.
     *
     * @param {Ext.data.field.Field} field
     * @param {Object} config
     * @param {String} config.mode Form mode. One of the properties of CMDBuildUI.util.helper.FormHelper.fieldmodes. Default: CMDBuildUI.util.helper.FormHelper.fieldmodes.read
     * @param {DefaultValue} config.defaultValue An object containing default value.
     * @param {String} config.linkName The name of the object linked within the ViewModel.
     * @param {String} config.activityLinkName The name of the activity linked within the ViewModel. Used for process instances.
     * @param {String} config.filterLinkName The name of the filter targed linked within the ViewModel, used for ecql filters.
     * @param {Boolean} config.ignoreUpdateVisibilityToField Ignore custom visibility rules.
     * @param {Boolean} config.ignoreCustomValidator Ignore custom validator rules.
     * @param {Boolean} config.ignoreAutovalue Ignore auto value rules.
     * @param {Boolead} config.ignoreSchedules Ignore schedules generation for date field with 'schedules rule definition' associated.
     *
     * @returns {Object} An `Ext.form.field.Field` definition.
     *
     */
    getFormField: function (field, config) {
        var fieldsettings;

        config = Ext.applyIf(config, {
            linkName: this._default_link_name,
            ignoreUpdateVisibilityToField: this._default_ignoreUpdateVisibilityToField,
            ignoreCustomValidator: this._default_ignoreCustomValidator,
            ignoreAutovalue: this._default_ignoreAutovalue,
            ignoreSchedules: this._default_ignoreSchedules,
            mode: this.formmodes.read
        });

        // append asterisk to label for mandatory fields
        var label = field.isInstance ? field.getDescription() : field.attributeconf._description_translation || field.description;;

        var bind = {};
        if (config.linkName) {
            bind = {
                value: Ext.String.format('{{0}.{1}}', config.linkName, field.name)
            };
        }

        // base field information
        var formfield = {
            fieldLabel: label,
            labelPad: CMDBuildUI.util.helper.FormHelper.properties.labelPad,
            labelSeparator: CMDBuildUI.util.helper.FormHelper.properties.labelSeparator,
            name: field.name,
            hidden: field.hidden,
            anchor: '100%',
            metadata: field.attributeconf,
            formmode: config.mode,
            bind: bind,
            autoEl: {
                "data-testid": 'field_' + field.name
            }
        };

        if (config.defaultValue) {
            // add listener to set value when field is added to form
            // to apdate theObject within viewmodel.
            formfield.listeners = {
                beforerender: function (f) {
                    var vm = f.lookupViewModel(true); // get form view model
                    if (config.linkName) {
                        vm.set(config.linkName + "." + field.name, config.defaultValue.value);
                    } else {
                        f.setValue(config.defaultValue.value);
                    }
                    if (config.defaultValue.valuedescription) {
                        vm.get(config.linkName).set(
                            Ext.String.format("_{0}_description", field.name),
                            config.defaultValue.valuedescription
                        );
                    }
                }
            };
            if (!Ext.isEmpty(config.defaultValue.editable)) {
                field.writable = config.defaultValue.editable;
            }
        }

        // Add help tooltip
        var attrconf = field.attributeconf;
        if (attrconf && attrconf.help && (config.mode !== this.formmodes.read || attrconf.helpAlwaysVisible)) {
            var converter = new showdown.Converter();
            var help = converter.makeHtml(attrconf.help);
            formfield.labelToolIconQtip = help;
            formfield.labelToolIconCls = 'fa-question-circle';
        }

        if (
            config.mode !== this.formmodes.read &&
            (field.writable) &&
            (field.mode != this.fieldmodes.immutable || config.mode === this.formmodes.create)
        ) {
            fieldsettings = this.getEditorForField(
                field,
                {
                    linkName: config.linkName,
                    activityLinkName: config.activityLinkName,
                    filterLinkName: config.filterLinkName,
                    formmode: config.mode,
                    ignoreSchedules: config.ignoreSchedules,
                    ignoreUpdateVisibilityToField: config.ignoreUpdateVisibilityToField,
                    ignoreCustomValidator: config.ignoreCustomValidator,
                    ignoreAutovalue: config.ignoreAutovalue
                }
            );
        }

        if (!fieldsettings) {
            fieldsettings = this.getReadOnlyField(field, config.linkName, config.mode, config.activityLinkName);
        }

        // override mandatory behaviour
        if (field.mandatory && !field.hidden) {
            fieldsettings.allowBlank = false;
        }
        Ext.merge(formfield, fieldsettings);

        return formfield;
    },

    /**
     * Returns the editor definition for given model field.
     *
     * @param {Ext.data.field.Field} field
     * @param {Object} config
     * @param {String} config.linkName The name of the object linked within the ViewModel.
     * @param {String} config.activityLinkName The name of the activity linked within the ViewModel. Used for process instances.
     * @param {String} config.ignoreUpdateVisibilityToField Ignore custom visibility rules.
     * @param {Boolean} config.ignoreCustomValidator Ignore custom validator rules.
     * @param {Boolean} config.ignoreAutovalue Ignore auto value rules.
     * @param {String} config.formmode Form mode. One of the properties of CMDBuildUI.util.helper.FormHelper.fieldmodes.
     * @param {Boolean} config.ignoreSchedules
     *
     * @returns {Object}
     *
     */
    getEditorForField: function (field, config) {
        var editor;
        config = config || {};

        config = Ext.applyIf(config, {
            linkName: this._default_link_name,
            ignoreUpdateVisibilityToField: this._default_ignoreUpdateVisibilityToField,
            ignoreCustomValidator: this._default_ignoreCustomValidator,
            ignoreAutovalue: this._default_ignoreAutovalue,
            ignoreSchedules: this._default_ignoreSchedules
        });

        // field configuration
        switch (field.cmdbuildtype.toLowerCase()) {
            /**
             * Boolean field
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                editor = {
                    xtype: 'threestatecheckboxfield'
                };
                break;
            /**
             * Date fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase():
                var datextype = 'datefield';
                if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.scheduler.enabled) && field.attributeconf.calendarTriggers && field.attributeconf.calendarTriggers.length && config.ignoreSchedules == false) {
                    datextype = 'schedulerdatefield';
                }
                editor = {
                    xtype: datextype,
                    format: CMDBuildUI.util.helper.UserPreferences.getDateFormat(),
                    formatText: '',
                    altFormats: 'Y-m-d',
                    recordLinkName: config.linkName,
                    listeners: {
                        drop: {
                            element: 'el', //bind to the underlying el property on the panel
                            fn: function () {
                                var view = Ext.getCmp(this.id);
                                view.inputEl.focus();
                            }
                        },
                        change: function (field, newvalue, oldvalue) {
                            if (Ext.isDate(newvalue) && field.getBind() && field.getBind().value) {
                                field.getBind().value.setValue(newvalue);
                            }
                        }
                    }
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase():
                var format = field.attributeconf.showSeconds ?
                    CMDBuildUI.util.helper.UserPreferences.getTimestampWithSecondsFormat() :
                    CMDBuildUI.util.helper.UserPreferences.getTimestampWithoutSecondsFormat();
                editor = {
                    xtype: 'datefield',
                    format: format,
                    formatText: '',
                    altFormats: '',
                    listeners: {
                        expand: function (datefield, eOpts) {
                            var todayBtn = datefield.getPicker().todayBtn;
                            todayBtn.on('click', function () {
                                var picker = datefield.getPicker(),
                                    today = new Date(),
                                    selectToday = function () {
                                        datefield.setValue(today);
                                        datefield.focus();
                                        this.hide();
                                    };
                                this.setHandler(selectToday, picker);
                            });
                        },
                        change: function (field, newvalue, oldvalue) {
                            if (Ext.isDate(newvalue) && field.getBind() && field.getBind().value) {
                                field.getBind().value.setValue(newvalue);
                            }
                        }
                    }
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time.toLowerCase():
                editor = {
                    xtype: 'textfield',
                    vtype: 'time',
                    listeners: {
                        blur: function (field, event, eOpts) {
                            // add left pad to numbers
                            var v = field.getValue();
                            if (v) {
                                var nv = [];
                                v.split(":").forEach(function (n) {
                                    nv.push(n.length === 1 ? "0" + n : n);
                                });
                                field.setValue(nv.join(":"));
                            }
                        }
                    }
                };
                break;
            /**
             * IP field
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress.toLowerCase():
                var vtype;
                if (field.attributeconf.ipType === "ipv4") {
                    vtype = "IPv4Address";
                } else if (field.attributeconf.ipType === "ipv6") {
                    vtype = "IPv6Address";
                } else {
                    vtype = "IPAddress";
                }
                editor = {
                    xtype: 'textfield',
                    vtype: vtype
                };
                break;
            /**
             * Numeric fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal.toLowerCase():
                var maxvalue = Math.pow(10, (field.attributeconf.precision - field.attributeconf.scale));
                editor = {
                    xtype: 'numberfield',
                    mouseWheelEnabled: false,
                    hideTrigger: true,
                    keyNavEnabled: false,
                    mouseWhellEnabled: false,
                    decimalPrecision: field.attributeconf.scale,
                    decimalSeparator: CMDBuildUI.util.helper.UserPreferences.getDecimalsSeparator(),
                    allowExponential: false,
                    validator: function (v) {
                        if (Ext.isEmpty(v)) {
                            return true;
                        }
                        v = parseFloat(v);
                        if (!(v < maxvalue && v > -maxvalue)) {
                            return false;
                        }
                        return true;
                    }
                };

                //if the unitOfMesure is set
                if (field.attributeconf.unitOfMeasure) {
                    Ext.apply(editor, this.getNumberOfMesureConfigs(field.attributeconf.unitOfMeasure))
                }
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double.toLowerCase():
                editor = {
                    xtype: 'numberfield',
                    mouseWheelEnabled: false,
                    hideTrigger: true,
                    keyNavEnabled: false,
                    mouseWhellEnabled: false,
                    decimalPrecision: 20,
                    decimalSeparator: CMDBuildUI.util.helper.UserPreferences.getDecimalsSeparator(),
                    allowExponential: false
                };

                //if the unitOfMesure is set
                if (field.attributeconf.unitOfMeasure) {
                    Ext.apply(editor, this.getNumberOfMesureConfigs(field.attributeconf.unitOfMeasure))
                }
                break;

            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint.toLowerCase():
                editor = {
                    xtype: 'biginteger'
                };

                // if the unitOfMesure is set
                if (field.attributeconf.unitOfMeasure) {
                    Ext.apply(editor, this.getNumberOfMesureConfigs(field.attributeconf.unitOfMeasure))
                }
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer.toLowerCase():
                editor = {
                    xtype: 'numberfield',
                    mouseWheelEnabled: false,
                    hideTrigger: true,
                    allowDecimals: false,
                    maxValue: 2147483647, // Integer max value
                    minValue: -2147483648, // Integer min value
                    allowExponential: false
                };

                //if the unitOfMesure is set
                if (field.attributeconf.unitOfMeasure) {
                    Ext.apply(editor, this.getNumberOfMesureConfigs(field.attributeconf.unitOfMeasure))
                }
                break;
            /**
             * Relation fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                editor = {
                    xtype: 'lookupfield',
                    recordLinkName: config.linkName,
                    lookupIdField: field.attributeconf.lookupIdField
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookupArray.toLowerCase():
                editor = {
                    xtype: 'lookuparrayfield',
                    recordLinkName: config.linkName
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                if (CMDBuildUI.util.helper.ModelHelper.getObjectFromName(field.attributeconf.targetClass, field.attributeconf.targetType)) {
                    editor = {
                        xtype: 'referencefield',
                        recordLinkName: config.linkName,
                        filterRecordLinkName: config.filterLinkName
                    };
                }
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase():
                editor = {
                    xtype: 'referencefield',
                    recordLinkName: config.linkName
                };
                break;
            /**
             * File field
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file.toLowerCase():
                editor = {
                    xtype: 'cmdbuildfilefield',
                    recordLinkName: config.linkName
                };
                break;
            /**
             * Text fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char.toLowerCase():
                editor = {
                    xtype: 'textfield',
                    enforceMaxLength: true,
                    maxLength: 1
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string.toLowerCase():
                if (field.attributeconf.password) {
                    editor = {
                        xtype: 'passwordfield',
                        recordLinkName: config.linkName
                    };
                } else {
                    editor = {
                        xtype: 'textfield',
                        enforceMaxLength: true,
                        maxLength: field.attributeconf.maxLength
                    };
                    if (field.attributeconf.multiline) {
                        editor.xtype = 'textareafield';
                        editor.resizable = {
                            handles: 's'
                        };
                    }
                }
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
                if (field.attributeconf.editorType === this.editortypes.html) {
                    editor = CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor();
                } else {
                    editor = {
                        xtype: 'textareafield',
                        resizable: {
                            handles: 's'
                        }
                    };
                }
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link.toLowerCase():
                editor = {
                    xtype: 'linkfield',
                    recordLinkName: config.linkName
                }
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula.toLowerCase():
                editor = {
                    xtype: 'displayfield'
                }
                break;
            default:
                CMDBuildUI.util.Logger.log("Missing field for " + field.name, CMDBuildUI.util.Logger.levels.warn);
                break;
        }

        // append metadata to editor configuration
        if (Ext.isObject(editor) && !Ext.Object.isEmpty(editor)) {
            editor.metadata = field.attributeconf;

            // add updateVisibility function
            if (!config.ignoreUpdateVisibilityToField) {
                this.addUpdateVisibilityToField(editor, field.attributeconf, config.linkName, config.formmode, config.activityLinkName);
            }

            // add custom validator
            if (!config.ignoreCustomValidator) {
                this.addCustomValidator(editor, field.attributeconf, config.linkName, config.formmode, config.activityLinkName);
            }

            // add auto value
            if (!config.ignoreAutoValue) {
                this.addAutoValue(editor, field.attributeconf, config.linkName, config.formmode, config.activityLinkName)
            }
        }

        return editor;
    },

    /**
     * Returns the display definition for given model field.
     *
     * @param {Ext.data.field.Field} field
     * @param {String} linkName The name of the object linked within the ViewModel.
     * @param {String} formmode Form mode. One of the properties of CMDBuildUI.util.helper.FormHelper.fieldmodes.
     * @param {String} activityLinkName The name of the activity linked within the ViewModel. Used for process instances.
     *
     * @returns {Object}
     *
     */
    getReadOnlyField: function (field, linkName, formmode, activityLinkName) {
        // setup readonly fields
        var fieldsettings = {
            xtype: 'displayfield'
        };

        switch (field.cmdbuildtype.toLowerCase()) {
            /**
             * Boolean field
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderThreeStateBooleanField(value);
                };
                break;
            /**
             * Date fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderDateField(value);
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value, {
                        hideSeconds: !f.metadata.showSeconds
                    });
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderTimeField(value, {
                        hideSeconds: !f.metadata.showSeconds
                    });
                };
                break;
            /**
             * Numeric fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderDecimalField(value, {
                        scale: f.metadata.scale,
                        showThousandsSeparator: f.metadata.showThousandsSeparator,
                        unitOfMeasure: f.metadata.unitOfMeasure,
                        unitOfMeasureLocation: f.metadata.unitOfMeasureLocation
                    });
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderDoubleField(value, {
                        visibleDecimals: f.metadata.visibleDecimals,
                        showThousandsSeparator: f.metadata.showThousandsSeparator,
                        unitOfMeasure: f.metadata.unitOfMeasure,
                        unitOfMeasureLocation: f.metadata.unitOfMeasureLocation
                    });
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderBigIntegerField(value, {
                        showThousandsSeparator: f.metadata.showThousandsSeparator,
                        unitOfMeasure: f.metadata.unitOfMeasure,
                        unitOfMeasureLocation: f.metadata.unitOfMeasureLocation
                    });
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderIntegerField(value, {
                        showThousandsSeparator: f.metadata.showThousandsSeparator,
                        unitOfMeasure: f.metadata.unitOfMeasure,
                        unitOfMeasureLocation: f.metadata.unitOfMeasureLocation
                    });
                };
                break;
            /**
             * Relation fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookupArray.toLowerCase():
                CMDBuildUI.model.lookups.LookupType.loadLookupValues(field.attributeconf.lookupType);
                fieldsettings.renderer = function (value, f) {
                    var record;
                    if (linkName) {
                        var vm = f.lookupViewModel();
                        record = vm.get(linkName);
                    }
                    if (value) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderLookupArrayField(value, {
                            lookupType: field.attributeconf.lookupType,
                            fieldName: field.name,
                            record: record
                        });
                    }
                    return value;
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                CMDBuildUI.model.lookups.LookupType.loadLookupValues(field.attributeconf.lookupType);
                fieldsettings.renderer = function (value, f) {
                    var record;
                    if (linkName) {
                        var vm = f.lookupViewModel();
                        record = vm.get(linkName);
                    }
                    return CMDBuildUI.util.helper.FieldsHelper.renderLookupField(value, {
                        lookupIdField: field.attributeconf.lookupIdField,
                        lookupType: field.attributeconf.lookupType,
                        fieldName: field.name,
                        record: record
                    });
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase():
                if (linkName) {
                    fieldsettings.bind = {
                        value: Ext.String.format('{{0}._{1}_description}', linkName, field.name)
                    };
                }
                fieldsettings.xtype = "displayfieldwithtriggers";
                fieldsettings.triggers = {
                    open: {
                        cls: 'x-fa fa-external-link',
                        handler: function (f, trigger, eOpts) {
                            var url,
                                id,
                                targetClass = field.attributeconf.targetClass;
                            if (linkName) {
                                id = f.lookupViewModel().get(linkName + "." + field.name);
                            } else {
                                id = f.getValue();
                            }
                            switch (field.attributeconf.targetType) {
                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                    url = CMDBuildUI.util.Navigation.getClassBaseUrl(targetClass, id, null, true);
                                    break;
                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                    url = CMDBuildUI.util.Navigation.getProcessBaseUrl(targetClass, id, null, null, true);
                                    break;
                                default:
                                    return;
                            }
                            CMDBuildUI.util.Utilities.closeAllPopups();
                            CMDBuildUI.util.Utilities.redirectTo(url);
                        }
                    }
                };
                fieldsettings.renderer = function (value, f) {
                    var record;
                    if (linkName) {
                        record = f.lookupViewModel().get(linkName);
                    }
                    if (value && CMDBuildUI.util.helper.ModelHelper.getObjectFromName(field.attributeconf.targetClass, field.attributeconf.targetType)) {
                        f.getTrigger('open').show();
                    } else {
                        f.getTrigger('open').hide();
                    }
                    return CMDBuildUI.util.helper.FieldsHelper.renderReferenceField(record ? record.get(field.name) : value, {
                        fieldName: field.name,
                        isHtml: field.attributeconf._html,
                        targetType: field.attributeconf.targetType,
                        targetTypeName: field.attributeconf.targetClass,
                        record: record
                    });
                };
                break;
            /**
             * File field
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    var record;
                    if (linkName) {
                        record = f.lookupViewModel().get(linkName);
                    }
                    return CMDBuildUI.util.helper.FieldsHelper.renderFileField(value, {
                        record: record,
                        fieldName: field.name,
                        showPreview: field.attributeconf.showPreview,
                        objectType: field.owner.objectType,
                        objectTypeName: field.owner.objectTypeName,
                        dmsCategory: field.attributeconf.dmsCategory,
                        dmsModel: field.attributeconf.dmsModel,
                        owner: f,
                        showInfo: true
                    });
                };
                break;
            /**
             * Text fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula.toLowerCase():
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string.toLowerCase():
                if (field.attributeconf.password) {
                    if (linkName) {
                        fieldsettings.bind = {
                            value: Ext.String.format('{{0}._{1}_has_value}', linkName, field.name)
                        };
                    }
                    fieldsettings.xtype = "displayfieldwithtriggers";
                    fieldsettings.triggers = this.getDisplayPasswordTriggers();
                    fieldsettings.renderer = function (value, f) {
                        var record;
                        if (linkName) {
                            record = f.lookupViewModel().get(linkName);
                        }
                        if (value) {
                            var response = '<span class="pwd-hide">' + "•••••" + '</span>';
                            if (!Ext.isEmpty(record.get(field.name))) {
                                f.getTrigger('show').show();
                                response += '<span class="pwd-show" style="display:none;">' + record.get(field.name) + '</span>';
                            }
                            return response;
                        } else {
                            return "";
                        }
                    };
                } else {
                    fieldsettings.renderer = function (value, f) {
                        var record;
                        if (linkName) {
                            record = f.lookupViewModel().get(linkName);
                        }
                        return CMDBuildUI.util.helper.FieldsHelper.renderTextField(value, {
                            html: field.attributeconf._html,
                            markdown: field.attributeconf.editorType === CMDBuildUI.util.helper.FormHelper.editortypes.markdown,
                            record: record,
                            fieldname: field.name
                        });
                    };
                }
                break;
        }

        switch (field.cmdbuildtype.toLowerCase()) {
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase():
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string.toLowerCase():
                // add new rules for HTML
                if (field.attributeconf._html) {
                    // add listener to open links in new tab
                    fieldsettings.listeners = {
                        afterRender: function (field) {
                            field.getEl().on('click', function (me) {
                                if (me.getTarget()) {
                                    var linkTarget = me.getTarget().tagName.toLowerCase();
                                    var linkOrigin = me.getTarget().origin;
                                    if (linkTarget == 'a' && linkOrigin !== window.location.origin) {
                                        me.getTarget().setAttribute("target", '_blank');
                                    }
                                    return false;
                                }
                            });
                        }
                    };
                }
                break;
        }

        // add updateVisibility function
        this.addUpdateVisibilityToField(fieldsettings, field.attributeconf, linkName, formmode, activityLinkName);

        // add auto value
        this.addAutoValue(fieldsettings, field.attributeconf, linkName, formmode, activityLinkName);

        return fieldsettings;
    },

    /**
     * Returns store definition for LookUp store.
     *
     * @private
     *
     * @param {String} type
     *
     * @returns {Object} Ext.data.Store definition
     *
     */
    getLookupStore: function (type) {
        return {
            model: 'CMDBuildUI.model.lookups.Lookup',
            proxy: {
                url: CMDBuildUI.util.api.Lookups.getLookupValues(type),
                type: 'baseproxy'
            },
            autoLoad: true,
            pageSize: 0
        };
    },

    /**
     * Returns store definition for Reference store.
     *
     * @private
     *
     * @param {String} type Target type.
     * @param {String} name Target name.
     *
     * @returns {Object} Ext.data.Store definition
     *
     */
    getReferenceStore: function (type, name) {
        if (type === 'class') {
            return {
                model: 'CMDBuildUI.model.domains.Reference',
                proxy: {
                    url: '/classes/' + name + '/cards/',
                    type: 'baseproxy'
                },
                autoLoad: true
            };
        }
    },

    /**
     * Return the base form for given model
     * @param {Ext.Model} model
     * @param {Object} config
     * @param {String} config.mode Form mode. Default: CMDBuildUI.util.helper.FormHelper.fieldmodes.read
     * @param {DefaultValue[]} config.defaultValues An array of objects containing default values.
     * @param {String} config.linkName The name of the object linked within the ViewModel.
     * @param {String} config.activityLinkName The name of the activity linked within the ViewModel. Used for process instances.
     * @param {Boolean} config.ignoreUpdateVisibilityToField Ignore custom visibility rules.
     * @param {Boolean} config.ignoreCustomValidator Ignore custom validator rules.
     * @param {Boolean} config.ignoreAutovalue Ignore auto value rules.
     * @param {Boolead} config.ignoreSchedules Ignore schedules generation for date field with 'schedules rule definition' associated.
     * @param {Boolean} config.showNotes Show notes as new tab.
     * @param {Boolean} config.showAsFieldsets Set to true for display fieldsets instead of tabs.
     * @param {Boolean} config.isCalendar Set to true if the model is calendar.
     * @param {Object} config.attributesOverrides An object containing properties to override for attributes.
     * @param {Boolean} config.attributesOverrides.attributename.writable Override writable property.
     * @param {Boolean} config.attributesOverrides.attributename.mandatory Override mandatory property.
     * @param {Numeric} config.attributesOverrides.attributename.index Override index property.
     * @param {String[]} config.visibleAttributes An array containing the names of visible attributes.
     * @param {String[]} config.excludeAttributeTypes An array containing the type of the attributes to exclude.
     * @param {Object} config.layout Layout definition for form.
     * @param {CMDBuildUI.model.AttributeGrouping[]} config.grouping An array which define the attributes grouping.
     * @param {Boolean} config.showOnlyAttributesInLayout Set to true to not display the fields present in the base data.
     * @param {String} config.formValidation A custom script to execute to validate the form.
     *
     * @returns {CMDBuildUI.components.tab.FormPanel|CMDBuildUI.components.tab.FieldSet[]}
     *
     */
    renderForm: function (model, config) {
        // set default configuration
        Ext.applyIf(config || {}, {
            readonly: true,
            defaultValues: [],
            linkName: this._default_link_name,
            ignoreUpdateVisibilityToField: this._default_ignoreUpdateVisibilityToField,
            ignoreCustomValidator: this._default_ignoreCustomValidator,
            ignoreAutovalue: this._default_ignoreAutovalue,
            ignoreSchedules: this._default_ignoreSchedules,
            showNotes: false,
            isCalendar: false,
            showAsFieldsets: false,
            attributesOverrides: {},
            visibleAttributes: undefined,
            excludeAttributeTypes: [],
            mode: config.readonly == undefined || config.readonly ? this.formmodes.read : this.formmodes.update,
            grouping: [],
            layout: {},
            showOnlyAttributesInLayout: false
        });

        // empty group
        var emptyGroup = Ext.create("CMDBuildUI.model.AttributeGrouping", {
            _id: "",
            description: CMDBuildUI.locales.Locales.common.attributes.nogroup,
            name: CMDBuildUI.model.AttributeGrouping.nogroup,
            index: config.grouping.length + 1
        });
        config.grouping.push(emptyGroup);

        // sort groups
        config.grouping.sort(function (a, b) {
            return a.index - b.index;
        });

        // get form fields
        var fields = CMDBuildUI.util.helper.FormHelper.getFormFields(model, {
            readonly: config.readonly,
            defaultValues: config.defaultValues,
            linkName: config.linkName,
            activityLinkName: config.activityLinkName,
            filterLinkName: config.filterLinkName,
            ignoreUpdateVisibilityToField: config.ignoreUpdateVisibilityToField,
            ignoreCustomValidator: config.ignoreCustomValidator,
            ignoreAutovalue: config.ignoreAutovalue,
            attributesOverrides: config.attributesOverrides,
            ignoreSchedules: config.ignoreSchedules,
            mode: config.mode
        });

        // collapse fieldset config
        var collapsefieldsets = false;
        if (model.objectType && model.objectTypeName) {
            var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(model.objectTypeName, model.objectType);
            collapsefieldsets = obj ? obj.get("_closefieldsets_" + config.mode) : collapsefieldsets;
        }

        // create json view
        var items = [];
        var hiddengroups = [];
        var rowdefconf = {
            xtype: 'container',
            layout: 'column',
            // layout: 'vbox',
            defaults: {
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                flex: '0.5',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                layout: 'anchor',
                minHeight: 1
            },
            items: []
        };
        var defaultcols = 2;
        config.grouping.forEach(function (g, gindex) {
            var hidden = true;
            // get group fields
            var groupfields = Ext.Array.filter(fields, function (f) {
                return g.get("_id") === f.metadata.group;
            });

            var group = {
                title: g.get("_description_translation") || g.get("description"),
                groupId: g.get("description"),
                items: [],
                collapsed: config.showAsFieldsets && g.get('defaultDisplayMode') != CMDBuildUI.model.AttributeGrouping.displayMode.open
            };

            // get layout configuration for group
            var grouplayout = config.layout[g.get("name")];

            // add field specified in layout
            if (!Ext.isEmpty(grouplayout)) {
                grouplayout.rows.forEach(function (rdef) {
                    var row = Ext.Object.merge({}, rowdefconf, {
                        defaults: {
                            columnWidth: 1 / rdef.columns.length,
                            flex: 1 / rdef.columns.length
                        },
                        items: []
                    });
                    group.items.push(row);

                    rdef.columns.forEach(function (cdef) {
                        var col = {
                            layout: 'fit',
                            items: []
                        };
                        if (!Ext.isEmpty(cdef.width)) {
                            col.columnWidth = cdef.width;
                            col.flex = cdef.width;
                        }
                        row.items.push(col);
                        var fields = cdef.fields || [];
                        fields.forEach(function (fdef) {
                            // search for specified field
                            var field = Ext.Array.findBy(groupfields, function (f) {
                                return fdef.attribute === f.name;
                            });
                            if (field && (config.visibleAttributes === undefined || Ext.Array.indexOf(config.visibleAttributes, field.name) !== -1)) {
                                // add field in column and remove from groupfields array
                                col.items.push(field);
                                Ext.Array.remove(groupfields, field);
                                hidden = !hidden ? hidden : field.hidden;
                            }
                        });
                    });
                });
            }

            // add fields without layout
            if (!Ext.isEmpty(groupfields) && !config.showOnlyAttributesInLayout) {
                // create columns layout
                var row;
                var index = 0;
                var hiddenfields = Ext.Object.merge({}, rowdefconf, {
                    items: []
                });
                groupfields.forEach(function (f) {
                    if ((config.visibleAttributes === undefined || Ext.Array.indexOf(config.visibleAttributes, f.name) !== -1) && !Ext.Array.contains(config.excludeAttributeTypes, f.metadata.type)) {
                        if (!f.hidden) {
                            if (index % defaultcols === 0) {
                                // create row
                                row = Ext.Object.merge({}, rowdefconf, {
                                    defaults: {
                                        columnWidth: 1 / defaultcols,
                                        flex: 1 / defaultcols
                                    },
                                    items: []
                                });
                                group.items.push(row);
                            }
                            // push column
                            row.items.push({
                                layout: 'fit',
                                items: f
                            });
                            index++;
                        } else {
                            // add field in fake row
                            hiddenfields.items.push(f);
                        }
                        hidden = !hidden ? hidden : f.hidden;
                    }
                });
                if (hiddenfields.items.length) {
                    group.items.push(hiddenfields);
                }
            }
            group.hidden = hidden || group.items.length === 0;

            // add group in items
            if (group.hidden) {
                hiddengroups.push(group);
            } else {
                items.push(group);
            }
        });

        // get tenant field config
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled)) {
            var objectdefinition = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(model.objectTypeName, model.objectType);
            var multitenantMode = objectdefinition ? objectdefinition.get("multitenantMode") : null;
            if (
                multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.always ||
                multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.mixed
            ) {
                var tenantfield = this.getTenantField(config.mode, multitenantMode, config.linkName);
                var group = items.length ? items[0] : {
                    title: CMDBuildUI.locales.Locales.common.attributes.nogroup,
                    items: []
                };

                var trow = Ext.Object.merge({}, rowdefconf, {
                    defaults: {
                        columnWidth: 0.5,
                        flex: 0.5
                    },
                    items: [{
                        items: tenantfield
                    }]
                });
                Ext.Array.insert(group.items, 0, [trow]);
            }
        }

        // add notes in a new page
        if (config && config.showNotes) {
            var value = config.isCalendar ? "notes" : "Notes";
            items.push({
                title: CMDBuildUI.locales.Locales.common.tabs.notes,
                reference: "_notes",
                items: [{
                    xtype: 'displayfield',
                    name: value,
                    anchor: '100%',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    bind: {
                        value: Ext.String.format("{{0}.{1}}", config.linkName, value)
                    }
                }]
            });
        }

        // add hidden groups
        hiddengroups.forEach(function (g) {
            items.push(g);
        });

        // return as fieldsets
        if (config.showAsFieldsets) {
            // set fieldset configurations
            items.forEach(function (item, index) {
                Ext.apply(item, {
                    xtype: 'formpaginationfieldset',
                    collapsible: items.length > 1,
                    items: item.items,
                    _cmdbuildFields: item.items
                });
            });

            // validator
            if (
                config.formValidation &&
                (config.mode === this.formmodes.update || config.mode === this.formmodes.create) &&
                !config.ignoreCustomValidator
            ) {
                Ext.Array.insert(items, 0, [{
                    xtype: 'formvalidatorfield',
                    validationCode: config.formValidation,
                    formMode: config.mode
                }]);
            }
            return items;
        }

        // hide tab in tabbar if there is only one tab
        if (items.length === 1) {
            items[0].tabConfig = {
                cls: 'hidden-tab'
            };
        }
        // return tab panel
        return {
            xtype: 'formtabpanel',
            items: items
        };
    },

    /**
     *
     * @param {String} objectType Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} objectTypeName Class/Process/View/... name.
     * @param {Object} config The same config object used in CMDBuildUI.util.helper.FormHelper.renderForm
     *
     * @returns {Ext.promise.Promise} Resolve method has as argument
     * the items to add to the form. Reject method has as argument
     * a {String} containing error message.
     *
     */
    renderFormForType: function (objectType, objectTypeName, config) {
        var deferred = new Ext.Deferred();

        // define model
        CMDBuildUI.util.helper.ModelHelper.getModel(
            objectType,
            objectTypeName
        ).then(function (model) {
            var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);
            config = Ext.applyIf(config || {}, {
                grouping: obj.attributeGroups ? obj.attributeGroups().getRange() : undefined,
                layout: obj.get("formStructure") && obj.get("formStructure").active ? obj.get("formStructure").form : undefined,
                formValidation: obj.get('validationRule')
            });
            deferred.resolve(CMDBuildUI.util.helper.FormHelper.renderForm(model, config))
        }).otherwise(function () {
            deferred.reject("nomodel");
        });

        return deferred.promise;
    },

    /**
     * @constant {String} waitFormHTML The HTML to display while the form is rendering.
     *
     */
    waitFormHTML: CMDBuildUI.util.Navigation.defaultManagementContentTitle,

    /**
     * Returns `true` if there is a form in saving mode.
     *
     * @returns {Boolean}
     */
    isFormSaving: function () {
        return this._isFormSaving;
    },

    /**
     * Set saving form status to true.
     *
     * @returns {Boolena}
     */
    startSavingForm: function () {
        this._isFormSaving = true;
        return this._isFormSaving;
    },

    /**
     * Set saving form status to false.
     *
     * @returns {Boolena}
     */
    endSavingForm: function () {
        this._isFormSaving = false;
        return this._isFormSaving;
    },

    /**
     * 
     * @param {String[]} triggers 
     * @param {Object} api 
     */
    executeFormTriggers: function (triggers, api) {
        // execute form triggers
        triggers.forEach(function (triggerfunction) {
            var executeFormTrigger;
            var jsfn = Ext.String.format(
                'executeFormTrigger = function(api) {\n{0}\n}',
                triggerfunction
            );
            try {
                eval(jsfn);
            } catch (e) {
                CMDBuildUI.util.Logger.log(
                    "Error on trigger function.",
                    CMDBuildUI.util.Logger.levels.error,
                    null,
                    e
                );
                executeFormTrigger = Ext.emptyFn;
            }
            // use try / catch to manage errors
            try {
                executeFormTrigger(api);
            } catch (e) {
                CMDBuildUI.util.Logger.log(
                    "Error on execution of form trigger.",
                    CMDBuildUI.util.Logger.levels.error,
                    null,
                    {
                        fn: triggerfunction
                    }
                );
            }
        });
    },
    privates: {
        _default_link_name: 'theObject',
        _default_ignoreUpdateVisibilityToField: false,
        _default_ignoreCustomValidator: false,
        _default_ignoreAutovalue: false,
        _default_ignoreSchedules: false,

        /**
         * @private
         *
         * @property {Boolead} isFormSaving Used to prevent browser closure when a form is saving data.
         */
        _isFormSaving: false,

        /**
         * @param {String} formmode
         * @param {String} multitenantmode
         *
         * @returns {Object}
         */
        getTenantField: function (formmode, multitenantmode, linkName) {
            linkName = linkName || this._default_link_name;
            var tenants = CMDBuildUI.util.helper.SessionHelper.getActiveTenants();
            var writable = formmode === this.formmodes.update || formmode === this.formmodes.create;
            if (writable) {
                // add combobox
                return {
                    xtype: 'combobox',
                    viewModel: {
                        formulas: {
                            hidetenantcombo: {
                                bind: {
                                    theobject: '{' + linkName + '}'
                                },
                                get: function (data) {
                                    if (multitenantmode === CMDBuildUI.model.users.Tenant.tenantmodes.always && tenants.length === 1) {
                                        if (data.theobject) {
                                            data.theobject.set("_tenant", tenants[0].code);
                                        }
                                        return true;
                                    }
                                    return false;
                                }
                            }
                        }
                    },
                    labelSeparator: CMDBuildUI.util.helper.FormHelper.properties.labelSeparator,
                    fieldLabel: CMDBuildUI.util.Utilities.getTenantLabel(),
                    displayField: 'description',
                    valueField: 'code',
                    queryMode: 'local',
                    anchor: '100%',
                    forceSelection: true,
                    allowBlank: multitenantmode !== CMDBuildUI.model.users.Tenant.tenantmodes.always,
                    bind: {
                        value: '{' + linkName + '._tenant}',
                        hidden: '{hidetenantcombo}'
                    },
                    store: {
                        data: tenants
                    },
                    triggers: {
                        clear: {
                            cls: 'x-form-clear-trigger',
                            handler: function (combo, trigger, eOpts) {
                                combo.clearValue();
                                combo.lastSelectedRecords = [];
                                if (combo.hasBindingValue) {
                                    combo.getBind().value.setValue(null);
                                }
                            }
                        }
                    }
                };
            } else {
                if (tenants.length > 1) {
                    return {
                        xtype: 'displayfield',
                        fieldLabel: CMDBuildUI.util.Utilities.getTenantLabel(),
                        labelSeparator: CMDBuildUI.util.helper.FormHelper.properties.labelSeparator,
                        bind: {
                            value: '{theObject._tenant}'
                        },
                        renderer: function (value, field) {
                            var t = Ext.Array.findBy(tenants, function (i) {
                                return i.code == value;
                            });
                            if (t) {
                                return t.description;
                            }
                            return "";
                        }
                    };
                }
            }
            return;
        },

        /**
         * @param {String} formmode
         * @param {String} linkName
         *
         * @returns {Object}
         */
        getActivityField: function (formmode, linkName) {
            linkName = linkName || this._default_link_name;
            var writable = formmode === this.formmodes.update || formmode === this.formmodes.create;
            if (writable) {
                // add combobox
                return {
                    xtype: 'combobox',
                    viewModel: {
                        formulas: {
                            activitiesDataManager: {
                                get: function () {
                                    var me = this;
                                    if (me.get('objectType') === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                                        var process = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(me.get('objectTypeName'), me.get('objectType'));
                                        if (process) {
                                            process.getActivities().then(function (activities) {
                                                var activitiesData = [];
                                                activities.each(function (activity) {
                                                    activitiesData.push({
                                                        value: activity.get('_definition'),
                                                        label: activity.get('description')
                                                    });
                                                });
                                                me.set('activitiesData', activitiesData);
                                            });
                                        }
                                    }
                                }
                            }
                        },
                        stores: {
                            activities: {
                                proxy: 'memory',
                                data: '{activitiesData}',
                                autoDestroy: true,
                                autoLoad: true
                            }
                        }
                    },
                    labelSeparator: CMDBuildUI.util.helper.FormHelper.properties.labelSeparator,
                    fieldLabel: CMDBuildUI.locales.Locales.common.tabs.activity,
                    valueField: 'value',
                    displayField: 'label',
                    queryMode: 'local',
                    anchor: '100%',
                    forceSelection: true,
                    bind: {
                        hidden: '{hideactivitycombo}',
                        store: '{activities}'
                    },
                    triggers: {
                        clear: {
                            cls: 'x-form-clear-trigger',
                            handler: function (combo, trigger, eOpts) {
                                combo.clearValue();
                                combo.lastSelectedRecords = [];
                                if (combo.hasBindingValue) {
                                    combo.getBind().value.setValue(null);
                                }
                            }
                        }
                    }
                };
            }
            return;
        },

        /**
         * Add update visibility function to field
         * @param {Object} config Ext.form.Field configuration
         * @param {Object} fieldMeta Field metadata
         * @param {Object} fieldMeta.showIf Show if code
         * @param {String} linkname
         * @param {String} formmode One of `read`, `create` or `update`.
         * @param {String} activityLinkName
         */
        addUpdateVisibilityToField: function (config, fieldMeta, linkname, formmode, activityLinkName) {
            linkname = linkname || this._default_link_name;
            formmode = formmode || this.formmodes.read;
            // Add show if control
            if (config && fieldMeta && !Ext.isEmpty(fieldMeta.showIf) && !fieldMeta.hidden) {
                fieldMeta.showIf.trim();
                var api = {},
                    // extract bind property
                    expr = /^api\.bind(\s?)=(\s?)\[.*\](\s?);/,
                    bind = expr.exec(fieldMeta.showIf);
                try {
                    if (bind && Ext.isArray(bind) && bind.length) {
                        eval(bind[0]);
                    }
                } catch (err) {
                    CMDBuildUI.util.Logger.log("Error evaluating showIf binds", CMDBuildUI.util.Logger.levels.error, "", err);
                }

                // evaluate script
                var script = fieldMeta.showIf.replace(expr, "");
                /* jshint ignore:start */
                try {
                    var jsfn = Ext.String.format(
                        'function executeShowIf(api) {\n{0}\n}',
                        script
                    );
                    eval(jsfn);
                } catch (err) {
                    CMDBuildUI.util.Logger.log("Error evaluating showIf script", CMDBuildUI.util.Logger.levels.error, "", err);
                    var executeShowIf = function () {
                        return true;
                    }
                }
                /* jshint ignore:end */

                // set auto value binds
                var bindConfig;
                if (api.bind) {
                    var b = {};
                    api.bind.forEach(function (k) {
                        b[k] = Ext.String.format("{{0}.{1}}", linkname, k);
                    });
                    bindConfig = {
                        bindTo: b
                    };
                } else {
                    // bind every object change
                    bindConfig = {
                        bindTo: '{' + linkname + '}',
                        deep: true
                    };
                }

                // add formula on view model
                config.viewModel = Ext.applyIf(config.viewModel || {});
                config.viewModel.formulas = Ext.applyIf(config.viewModel.formulas || {}, {
                    updateFieldVisibility: {
                        bind: bindConfig,
                        get: function (data) {
                            var activity;
                            if (activityLinkName) {
                                activity = this.get(activityLinkName);
                            }
                            var api = Ext.apply({
                                record: this.get(linkname),
                                activity: activity,
                                mode: formmode
                            },
                                CMDBuildUI.util.api.Client.getApiForFieldVisibility()),
                                view = this.getView();

                            // use try / catch to manage errors
                            try {
                                var visibility = executeShowIf(api);
                                if (visibility === true || visibility === "true" || visibility === "enabled") {
                                    view.setHidden(false);
                                    view.setDisabled(false);
                                } else if (visibility === "disabled") {
                                    view.setHidden(false);
                                    view.setDisabled(true);
                                } else {
                                    view.setHidden(true);
                                    view.setDisabled(true);
                                }
                            } catch (e) {
                                CMDBuildUI.util.Logger.log(
                                    "Error on showIf configuration for field " + view.getFieldLabel(),
                                    CMDBuildUI.util.Logger.levels.error,
                                    null,
                                    e
                                );
                            }
                        }
                    }
                });
            }
        },

        /**
         * Add update visibility function to field
         * @param {Object} config Ext.form.Field configuration
         * @param {Object} fieldMeta Field metadata
         * @param {Object} fieldMeta.validationRules Validation rules code
         * @param {String} linkname
         * @param {String} formmode One of `read`, `create` or `update`.
         */
        addCustomValidator: function (config, fieldMeta, linkname, formmode, activityLinkName) {
            var me = this;
            linkname = linkname || this._default_link_name;
            formmode = formmode || this.formmodes.read;
            if (config && fieldMeta && !Ext.isEmpty(fieldMeta.validationRules)) {
                var bind = me.extractBindFromExpression(fieldMeta.validationRules, linkname);

                /* jshint ignore:start */
                var jsfn = Ext.String.format(
                    'function executeValidationRules(value, api) {\n{0}\n}',
                    fieldMeta.validationRules
                );
                eval(jsfn);
                /* jshint ignore:end */

                // update binds
                if (!bind.deep) {
                    bind = bind.bindTo;
                    bind[fieldMeta.name] = Ext.String.format("{{0}.{1}}", linkname, fieldMeta.name);
                }

                // bind validator
                config.bind = Ext.applyIf(config.bind || {}, {
                    validation: '{customFieldValidation}'
                });
                // add formula on view model
                config.viewModel = Ext.applyIf(config.viewModel || {});
                config.viewModel.formulas = Ext.applyIf(config.viewModel.formulas || {}, {
                    customFieldValidation: {
                        bind: bind,
                        get: function (data) {
                            var api = Ext.apply({
                                record: this.get(linkname),
                                activity: this.get(activityLinkName),
                                mode: formmode
                            }, CMDBuildUI.util.api.Client.getApiForFieldCustomValidator());

                            try {
                                var isvalid = executeValidationRules(api.record.get(fieldMeta.name), api);
                                if (isvalid === false) {
                                    isvalid = CMDBuildUI.locales.Locales.notifier.error;
                                }
                                return isvalid;
                            } catch (e) {
                                CMDBuildUI.util.Logger.log(
                                    "Error on validationRules configuration for field " + fieldMeta.name,
                                    CMDBuildUI.util.Logger.levels.error,
                                    null,
                                    e
                                );
                                return false;
                            }
                        }
                    }
                });
            }
        },

        /**
         * Add auto value script to field
         * @param {Object} config Ext.form.Field configuration
         * @param {Object} fieldMeta Field metadata
         * @param {Object} fieldMeta.validationRules Validation rules code
         * @param {String} linkname
         * @param {String} formmode One of `read`, `create` or `update`.
         */
        addAutoValue: function (config, fieldMeta, linkname, formmode, activityLinkName) {
            linkname = linkname || this._default_link_name;
            formmode = formmode || this.formmodes.read;
            if (config && fieldMeta && !Ext.isEmpty(fieldMeta.autoValue) && formmode != this.formmodes.read) {
                fieldMeta.autoValue.trim();
                var api = {},
                    // extract bind property
                    expr = /^api\.bind(\s?)=(\s?)\[.*\](\s?);/,
                    bind = expr.exec(fieldMeta.autoValue);
                try {
                    if (bind && Ext.isArray(bind) && bind.length) {
                        eval(bind[0]);
                    }
                } catch (err) {
                    CMDBuildUI.util.Logger.log("Error evaluating autoValue binds", CMDBuildUI.util.Logger.levels.error, "", err);
                }

                // evaluate script
                var script = fieldMeta.autoValue.replace(expr, "");
                /* jshint ignore:start */
                try {
                    var jsfn = Ext.String.format(
                        'function executeAutoValue(api) {\n{0}\n}',
                        script
                    );
                    eval(jsfn);
                } catch (err) {
                    CMDBuildUI.util.Logger.log("Error evaluating autoValue script", CMDBuildUI.util.Logger.levels.error, "", err);
                    var executeAutoValue = Ext.emptyFn;
                }
                /* jshint ignore:end */

                // set auto value binds
                var bindConfig;
                if (api.bind) {
                    var b = {};
                    api.bind.forEach(function (k) {
                        b[k] = Ext.String.format("{{0}.{1}}", linkname, k);
                    });
                    bindConfig = {
                        bindTo: b
                    };
                } else {
                    // bind every object change
                    bindConfig = {
                        bindTo: '{' + linkname + '}',
                        deep: true
                    };
                }

                // add formula on view model
                config.viewModel = Ext.applyIf(config.viewModel || {});
                config.viewModel.formulas = Ext.applyIf(config.viewModel.formulas || {}, {
                    setValueFromAutoValue: {
                        bind: bindConfig,
                        get: function (data) {
                            var record = this.get(linkname),
                                activity;
                            if (activityLinkName) {
                                activity = this.get(activityLinkName);
                            }

                            var api = Ext.apply({
                                record: record,
                                activity: activity,
                                mode: formmode,
                                setValue: function (value) {
                                    this._setValue(fieldMeta.name, value);
                                },
                                setHTMLValue: function (value) {
                                    this._setHTMLValue(fieldMeta.name, value);
                                },
                                setReferenceValue: function (value, description, code) {
                                    this._setReferenceValue(fieldMeta.name, value, description, code)
                                },
                                setReferenceValueFromRecord: function (fromRecord) {
                                    this._setReferenceValueFromRecord(fieldMeta.name, fromRecord);
                                },
                                setLookupValue: function (value, code, description) {
                                    this._setLookupValue(fieldMeta.name, value, code, description);
                                },
                                setLookupValueFromRecord: function (fromRecord) {
                                    this._setLookupValueFromRecord(fieldMeta.name, fromRecord);
                                },
                                setLinkValue: function (url, label) {
                                    this._setLinkValue(fieldMeta.name, url, label);
                                }
                            }, CMDBuildUI.util.api.Client.getApiForFieldAutoValue());

                            // execute script
                            try {
                                executeAutoValue(api);
                            } catch (err) {
                                CMDBuildUI.util.Logger.log("Error executing autoValue script", CMDBuildUI.util.Logger.levels.error, "", err);
                            }
                        }
                    }
                });
            }
        },

        /**
         *
         * @param {String} expression Javascript expression as string
         * @param {String} linkname
         */
        extractBindFromExpression: function (expression, linkname) {
            var api = {};
            var expr = /^api\.bind(\s?)=(\s?)\[.*\](\s?);/;
            var bind = expr.exec(expression);
            try {
                if (bind && Ext.isArray(bind) && bind.length) {
                    eval(bind[0]);
                }
            } catch (err) {
                CMDBuildUI.util.Logger.log("Error evaluating binds", CMDBuildUI.util.Logger.levels.error, "", err);
            }

            if (api.bind) {
                var b = {};
                api.bind.forEach(function (k) {
                    b[k] = Ext.String.format("{{0}.{1}}", linkname, k);
                });
                return {
                    bindTo: b
                };
            }
            // bind every object change
            return {
                bindTo: '{' + linkname + '}',
                deep: true
            };
        },

        /**
         * This function returns some configuration to render the unitOfMesure in the numberfields
         * @param {String} unitOfMeasure
         */
        getNumberOfMesureConfigs: function (unitOfMeasure) {
            return {
                hideTrigger: false,
                triggers: {
                    unitOfMesureTrigger: {
                        cls: 'unitOfMesure',
                        hidden: false,
                        hideOnReadOnly: false,
                        readOnly: false,
                        defaultPass: true,
                        handler: function () {
                            return
                        }
                    },
                    spinner: {
                        hidden: true,
                        hideOnReadOnly: true,
                        readOnly: true
                    }
                },
                listeners: {
                    afterRender: function (view, eOpts) {
                        var trigger = view.getTrigger('unitOfMesureTrigger');
                        if (trigger) {
                            var triggerEl = trigger.getEl().dom;
                            triggerEl.setAttribute('unitOfMesure', unitOfMeasure);
                        }

                        var trigger = view.getTrigger('spinner');
                        if (trigger) {
                            trigger.hide()
                        }
                    }
                }
            }
        },

        /**
         * This function return the triggers configuration for password field
         */
        getDisplayPasswordTriggers: function () {
            return {
                show: {
                    cls: 'x-fa fa-eye',
                    handler: function (f, trigger, eOpts) {
                        f.getEl().dom.querySelector(".pwd-hide").style = "display:none";
                        f.getEl().dom.querySelector(".pwd-show").style = "display";
                        f.getTrigger('hide').show();
                        trigger.hide();
                    },
                    hidden: true
                },
                hide: {
                    cls: 'x-fa fa-eye-slash',
                    handler: function (f, trigger, eOpts) {
                        f.getEl().dom.querySelector(".pwd-show").style = "display:none";
                        f.getEl().dom.querySelector(".pwd-hide").style = "display:";
                        f.getTrigger('show').show();
                        trigger.hide();

                    },
                    hidden: true
                }
            }
        }
    }

});