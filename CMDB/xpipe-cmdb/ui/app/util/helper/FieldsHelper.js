/**
 * @file CMDBuildUI.util.helper.FieldsHelper
 * @module CMDBuildUI.util.helper.FieldsHelper
 * @author Tecnoteca srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.FieldsHelper', {
    singleton: true,

    /**
     * Generate fielcontainer with slider and numbierfield.
     *
     * @param {Object} config
     * @param {Number} config.multiplier the maxValue of slider, the input
     * minValue will be set equal to minValue * multiplier
     * defaultValue = 1
     *
     * @param {Number} config.minValue the maxValue of slider, the input
     * minValue will be set  equal to minValue * multiplier
     * defaultValue = 0
     *
     * @param {Number} config.maxValue the maxValue of slider, the input
     * maxValue will be set equal to maxValue * multiplier
     * defaultValue = 100
     *
     * @param {Boolean} config.showPercentage if true show percentage "%" symbol
     * in slider qtip
     * defaultValue = false
     *
     * @param {Number} config.sliderDecimalPrecision use 0 if showPercentage
     * otherwise use any number, value is used to format the slider qtip text
     * defaultValue = 0
     *
     * @param {Number} config.inputDecimalPrecision use 0 if showPercentage
     * otherwise use any number, value is used to format the input field value
     * defaultValue = 0
     *
     * @param {String} config.name name used on input field
     * defaultValue = undefined
     *
     * @param {String} config.columnWidth use this is the onwner of
     * fieldcontainer is layout type "column"
     * defaultValue = undefined
     *
     * @param {String|Number} config.padding the padding of field conainer
     * defaultValue = 0
     *
     * @param {String} config.fieldLabel the label of fieldcontainer
     * defaultValue = undefined
     *
     * @param {Object} config.bind the bind of slider
     * defaultValue = undefined
     *
     * @param {Object} config.localized the locale object key
     * @param {String} config.loacalized.fieldLabel the localized key as string
     * of fieldLabel
     * defaultValue = {}
     *
     * @returns {Ext.field.FieldContainer}
     *
     */
    getSliderWithInputField: function (config) {
        config = Ext.merge({
            multiplier: 1,
            minValue: 0,
            maxValue: 100,
            showPercentage: false,
            sliderDecimalPrecision: 0,
            inputDecimalPrecision: 0,
            name: undefined,
            columnWidth: undefined,
            padding: 0,
            fieldLabel: undefined,
            bind: {},
            localized: {}
        }, config);

        var fieldcontainer = {
            columnWidth: config.columnWidth,
            xtype: 'fieldcontainer',
            padding: config.padding,
            fieldLabel: config.fieldLabel,
            localized: config.localized,
            layout: 'column',
            items: [{
                xtype: 'fieldcontainer',
                layout: 'hbox',
                columnWidth: 1,
                bind: {
                    hidden: '{actions.view}'
                },
                items: [{
                    flex: 1,
                    xtype: 'slider',
                    increment: config.increment,
                    minValue: config.minValue,
                    maxValue: config.maxValue,
                    decimalPrecision: config.sliderDecimalPrecision,
                    padding: '0 15 0 0',
                    name: config.name,
                    bind: Ext.merge({
                        hidden: '{actions.view}'
                    }, config.bind),
                    tipText: function (thumb) {
                        if (config.showPercentage) {
                            return Ext.util.Format.percent(thumb.value);
                        } else {
                            return parseFloat(thumb.value * config.multiplier).toFixed(config.sliderDecimalPrecision);
                        }
                    },
                    listeners: {
                        change: function (slider, newValue) {
                            slider.up('fieldcontainer').down('numberfield').setValue(String(Ext.util.Format.number(newValue * config.multiplier, config.inputDecimalPrecision)));
                        }
                    }
                }, {
                    xtype: 'numberfield',
                    width: 50,
                    step: config.increment * config.multiplier,
                    minValue: config.minValue * config.multiplier,
                    maxValue: config.maxValue * config.multiplier,
                    decimalPrecision: config.inputDecimalPrecision,
                    // Remove spinner buttons, and arrow key and mouse wheel listeners
                    hideTrigger: true,
                    value: 0,
                    keyNavEnabled: false,
                    mouseWheelEnabled: false,
                    selectOnFocus: true,
                    fieldStyle: 'text-align: center;padding: 5px 5px 4px',
                    listeners: {
                        blur: function (numberfield, event, eOpts) {
                            var slider = numberfield.up('fieldcontainer').down('slider');
                            numberfield.validate();
                            numberfield.lookupViewModel().set(slider.getConfig().bind.value.stub.path, parseFloat(Number(numberfield.getValue()) / config.multiplier).toFixed(config.sliderDecimalPrecision));
                        }
                    }
                }]
            }, {
                xtype: 'displayfield',
                columnWidth: 1,
                bind: Ext.merge(config.bind, {
                    hidden: '{!actions.view}'
                }),
                hidden: true,
                renderer: function (value) {
                    if (config.showPercentage) {
                        return Ext.util.Format.percent(value);
                    } else {
                        return parseFloat(value * config.multiplier).toFixed(config.sliderDecimalPrecision);
                    }
                }
            }]
        };
        return fieldcontainer;

    },

    /**
     * Generate fielcontainer with slider and numbierfield.
     *
     * @param {Object} config
     *
     * @param {String} config.name name used on input field
     * defaultValue = undefined
     *
     * @param {String} config.columnWidth use this is the onwner of
     * fieldcontainer is layout type "column"
     * defaultValue = undefined
     *
     * @param {String|Number} config.padding the padding of field conainer
     * defaultValue = 0
     *
     * @param {String} config.fieldLabel the label of fieldcontainer
     * defaultValue = undefined
     *
     * @param {Object} config.bind the bind of slider
     * defaultValue = undefined
     *
     * @param {Object} config.alt img alt tag attribute
     * [W] For WAI-ARIA compliance, IMG elements SHOULD have an alt attribute.
     * defaultValue = '-'
     *
     * @param {Object} config.localized the localized object used in
     * fieldconatiner and image
     * @param {String} config.localized.fieldLabel the localized key as string
     * of fieldLabel
     * @param {String} config.localized.alt the localized key as string of alt
     * defaultValue = {}
     *
     * @returns {Ext.field.FieldContainer}
     *
     */
    getColorpickerField: function (config) {
        config = Ext.applyIf(config, {
            itemId: undefined,
            name: undefined,
            columnWidth: undefined,
            padding: 0,
            fieldLabel: undefined,
            bind: {},
            localized: {},
            alt: '-'
        });

        var fieldcontainer = {

            columnWidth: config.columnWidth,
            xtype: 'fieldcontainer',
            fieldLabel: config.fieldLabel,
            localized: config.localized,
            layout: 'column',
            padding: config.padding,
            items: [{
                itemId: config.itemId,
                name: config.name,
                columnWidth: 1,
                xtype: 'cmdbuild-colorpicker',
                bind: config.bind,
                triggers: {
                    clear: {
                        cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                        handler: function (input) {
                            input.lookupViewModel().set(input.getConfig().bind.value.stub.path, null);
                            input.up('fieldcontainer').down('image').setStyle('color', 'initial');
                            input.reset();
                        }
                    }
                },
                listeners: {
                    change: function (input, newValue) {
                        input.up('fieldcontainer').down('image').setStyle('color', newValue);
                    }
                },
                autoEl: {
                    'data-testid': 'utilhelper-colorpicker_input'
                }
            }, {
                xtype: 'image',
                autoEl: 'div',
                alt: config.alt || config.fieldLabel,
                localized: config.localized,
                width: 32,
                cls: 'fa-2x x-fa fa-square',
                style: {
                    lineHeight: '32px'
                },
                autoEl: {
                    'data-testid': 'utilhelper-colorpicker_display'
                }
            }]
        };
        return fieldcontainer;
    },

    /**
     * Render Two States Boolean field.
     *
     * @param {Boolean} value
     *
     * @returns {String}
     *
     */
    renderBooleanField: function (value) {
        if (Ext.isEmpty(value)) {
            return Ext.String.format("<span class=\"{0}\"><span class=\"x-form-checkbox-default\"></span></span>", '');
        }
        var klass = '';
        if (value) {
            klass = 'x-form-cb-checked';
        }
        return Ext.String.format("<span class=\"{0}\"><span class=\"x-form-checkbox-default\"></span></span>", klass);
    },

    /**
     * Render Three States Boolean field.
     *
     * @param {Boolean} value
     *
     * @returns {String}
     *
     */
    renderThreeStateBooleanField: function (value) {
        var css = '';
        if (value === false || value === "false") {
            css = Ext.baseCSSPrefix + 'form-tscb-unchecked';
        } else if (value === true || value === "true") {
            css = Ext.baseCSSPrefix + 'form-tscb-checked';
        }
        return Ext.String.format("<span class=\"x-form-tscb-readonly {0} x-item-disabled x-form-item-default\"><span class=\"x-form-checkbox-default x-form-tscb-default\"></span></span>", css);
    },

    /**
     * Render Integer field.
     *
     * @param {Numeric} value
     * @param {Object} config
     * @param {Boolean} config.showThousandsSeparator If true the number will be displayed with thousands separator. Default: false
     * @param {String} config.unitOfMeasure The Unit of measure to display (if not blank).
     * @param {String} config.unitOfMeasureLocation The position of the unit of measure. Can be 'AFTER' or 'BEFORE'. Default: 'AFTER'
     *
     * @returns {String} The string representing the integer.
     *
     */
    renderIntegerField: function (value, config) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        config = config || {};

        // show thousands separator
        if (config.showThousandsSeparator) {
            value = this.formatNumber(value, null, config.showThousandsSeparator);
        }

        // show unit of measure
        if (!Ext.isEmpty(config.unitOfMeasure)) {
            var format;
            if (config.unitOfMeasureLocation === CMDBuildUI.model.Attribute.unitOfMeasureLocations.before) {
                format = "{1} {0}";
            } else {
                format = "{0} {1}";
            }
            value = Ext.String.format(format, value, config.unitOfMeasure);
        }

        return value;
    },

    /**
     * Render BigInteger field.
     *
     * @param {Numeric} value
     * @param {Object} config
     * @param {Boolean} config.showThousandsSeparator If true the number will be displayed with thousands separator. Default: false
     * @param {String} config.unitOfMeasure The Unit of measure to display (if not blank).
     * @param {String} config.unitOfMeasureLocation The position of the unit of measure. Can be 'AFTER' or 'BEFORE'. Default: 'AFTER'
     *
     * @returns {String} The string representing the biginteger.
     *
     */
    renderBigIntegerField: function (value, config) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        // currently is the same of integer field
        return this.renderIntegerField(value, config);
    },


    /**
     * Render Decimal field.
     *
     * @param {Numeric} value
     * @param {Object} config
     * @param {Integer} config.scale The scale to use to represent the decimal number.
     * @param {Boolean} config.showThousandsSeparator If true the number will be displayed with thousands separator. Default: false
     * @param {String} config.unitOfMeasure The Unit of measure to display (if not blank).
     * @param {String} config.unitOfMeasureLocation The position of the unit of measure. Can be 'AFTER' or 'BEFORE'. Default: 'AFTER'
     *
     * @returns {String} The string representing the decimal.
     *
     */
    renderDecimalField: function (value, config) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        config = config || {};

        // show thousands separator
        if (config.scale || config.showThousandsSeparator) {
            value = this.formatNumber(value, config.scale, config.showThousandsSeparator);
        }

        // show unit of measure
        if (!Ext.isEmpty(config.unitOfMeasure)) {
            var format;
            if (config.unitOfMeasureLocation === CMDBuildUI.model.Attribute.unitOfMeasureLocations.before) {
                format = "{1} {0}";
            } else {
                format = "{0} {1}";
            }
            value = Ext.String.format(format, value, config.unitOfMeasure);
        }

        return value;
    },


    /**
     * Render Double field.
     *
     * @param {Numeric} value
     * @param {Object} config
     * @param {Object} config.visibleDecimals The number of decimals to display.
     * @param {Boolean} config.showThousandsSeparator If true the number will be displayed with thousands separator. Default: false
     * @param {String} config.unitOfMeasure The Unit of measure to display (if not blank).
     * @param {String} config.unitOfMeasureLocation The position of the unit of measure. Can be 'AFTER' or 'BEFORE'. Default: 'AFTER'
     *
     * @returns {String}
     *
     */
    renderDoubleField: function (value, config) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        config = config || {};

        // show thousands separator
        value = this.formatNumber(value, config.visibleDecimals, config.showThousandsSeparator);

        // show unit of measure
        if (!Ext.isEmpty(config.unitOfMeasure)) {
            var format;
            if (config.unitOfMeasureLocation === CMDBuildUI.model.Attribute.unitOfMeasureLocations.before) {
                format = "{1} {0}";
            } else {
                format = "{0} {1}";
            }
            value = Ext.String.format(format, value, config.unitOfMeasure);
        }

        return value;
    },

    /**
     * Render Date field.
     * @param {Date} value
     *
     * @returns {String}
     *
     */
    renderDateField: function (value) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        if (typeof value === "string") {
            value = Ext.Date.parse(value, "Y-m-d");
        }
        return Ext.util.Format.date(value, CMDBuildUI.util.helper.UserPreferences.getDateFormat());
    },

    /**
     * Render Time field.
     * @param {Date} value
     * @param {Object} config
     * @param {Boolean} config.hideSeconds Hide seconds when display time. Default: false
     *
     * @returns {String}
     *
     */
    renderTimeField: function (value, config) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        config = config || {};
        var format;
        // convert to date
        if (typeof value === "string") {
            value = Ext.Date.parse(value, "H:i:s") || Ext.Date.parse(value, "H:i");
        }
        // get format
        if (config.hideSeconds) {
            format = CMDBuildUI.util.helper.UserPreferences.getTimeWithoutSecondsFormat();
        } else {
            format = CMDBuildUI.util.helper.UserPreferences.getTimeWithSecondsFormat();
        }
        return Ext.util.Format.date(value, format);
    },

    /**
     * Render TimeStamp field.
     *
     * @param {Date} value
     * @param {Object} config
     * @param {Boolean} config.hideSeconds Hide seconds when display time. Default: false
     *
     * @returns {String}
     *
     */
    renderTimestampField: function (value, config) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        config = config || {};
        var format;
        if (config.hideSeconds) {
            format = CMDBuildUI.util.helper.UserPreferences.getTimestampWithoutSecondsFormat();
        } else {
            format = CMDBuildUI.util.helper.UserPreferences.getTimestampWithSecondsFormat();
        }
        return Ext.util.Format.date(value, format);
    },

    /**
     * Render Lookup field.
     *
     * @param {Number} value
     * @param {Object} config
     * @param {String} config.lookupType Lookup type name.
     * @param {String} config.lookupIdField The property used as id. One of "id" and "code". Default: "id"
     * @param {Ext.data.Model} config.record The card/process instance/view/.. record.
     * @param {String} config.fieldName The field name.
     *
     * @returns {String}
     *
     */
    renderLookupField: function (value, config) {
        var output = "";
        if (value) {
            var lookupvalue;
            if (config.lookupIdField === 'code') {
                lookupvalue = CMDBuildUI.model.lookups.Lookup.getLookupValueByCode(config.lookupType, value);
            } else {
                lookupvalue = CMDBuildUI.model.lookups.Lookup.getLookupValueById(config.lookupType, value);
            }
            if (lookupvalue) {
                output = lookupvalue.getFormattedDescription();
            }
        }

        // if output is empty get description from record data
        if (value && !output && config.record) {
            var translation = config.record.get("_" + config.fieldName + "_description_translation");
            var description = config.record.get("_" + config.fieldName + "_description");
            output = translation ? translation : description;
            output = output || '';
        }
        return output;
    },

    /**
     * Render Lookup Array field
     *
     * @param {Number[]} values The IDs of the records presents in the attribute.
     * @param {Object} config
     * @param {String} config.lookupType Lookup type name.
     * @param {Ext.data.Model} config.record The card/process instance/view/.. record.
     * @param {String} config.fieldName The field name.
     * 
     * @returns {String}
     */
    renderLookupArrayField: function (values, config) {
        var description = [];
        if (typeof values === 'string') {
            values = values.split(',');
        }
        values.forEach(function (item) {
            description.push(CMDBuildUI.util.helper.FieldsHelper.renderLookupField(item, {
                lookupType: config.lookupType,
                fieldName: config.fieldName,
                record: config.record
            }))
        });
        return description.join(", ");
    },

    /**
     * Render Reference field.
     *
     * @param {Number|String} value
     * @param {Object} config
     * @param {String} config.fieldName The field name.
     * @param {Boolean} config.isHtml True if the field must be displayed as HTML field. Default: false
     * @param {Boolean} config.stripTags True to remove tags. Default: false
     * @param {String} config.targetType The target type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes
     * @param {String} config.targetTypeName The class/process/view/.. name.
     * @param {Ext.data.Model} config.record The card/process instance/view/.. record.
     *
     * @returns {String}
     *
     */
    renderReferenceField: function (value, config) {
        var description;
        if (config.record) {
            description = config.record.get("_" + config.fieldName + "_description");
        }

        function renderText(value) {
            return CMDBuildUI.util.helper.FieldsHelper.renderTextField(value, {
                html: config.isHtml,
                record: config.record,
                fieldname: config.fieldName
            });
        }

        function renderStrippedText(value) {
            if (!config.isHtml) {
                value = CMDBuildUI.util.helper.FieldsHelper.renderTextField(value);
            }
            return Ext.util.Format.stripTags(value);
        }

        if (value && description !== undefined) {
            return config.stripTags ? renderStrippedText(description) : renderText(description);
        } else if (value && description == undefined) {
            CMDBuildUI.util.helper.ModelHelper.getModel(config.targetType, config.targetTypeName).then(function (m) {
                m.load(value, {
                    callback: function (r) {
                        if (config.record) {
                            config.record.set("_" + config.fieldName + "_description", r.get("Description"));
                        }
                    }
                });
            });
        } else {
            return '';
        }
    },


    /**
     * Render file field.
     *
     * @param {String} value
     * @param {Object} config
     * @param {Ext.data.Model} config.record The target record.
     * @param {String} config.fieldName Field name.
     * @param {Boolean} [config.showPreview=false] Show preview.
     * @param {String} [config.objectType] The target object type. Used if showPreview is true.
     * @param {String} [config.objectTypeName] The target object type name. Used if showPreview is true.
     * @param {String} [config.dmsCategory] Document category id. Used if showPreview is true.
     * @param {String} [config.dmsModel] Model Name. Used if showPreview is true.
     * @param {Ext.Component} [config.owner] Owner component. Used if showPreview is true.
     * @param {Boolean} [config.showInfo] Show attachment info.
     *
     * @returns {String}
     *
     */
    renderFileField: function (value, config) {
        if (value) {
            var container = '{0}';
            if (config.showInfo) {
                var containerId = Ext.id({}, "info-");
                container = '<div id="' + containerId + '" class="' + Ext.baseCSSPrefix + 'filefield-renderer" ' +
                    'data-objecttype="' + config.objectType + '" ' +
                    'data-objecttypename="' + config.objectTypeName + '" ' +
                    'data-objectid="' + config.record.get("_id") + '" ' +
                    'data-documentcategory="' + config.dmsCategory + '" ' +
                    'data-documentid="' + value + '" ' +
                    'data-filename="' + config.record.get("_" + config.fieldName + "_name") + '" ' +
                    'data-mimetype="' + config.record.get("_" + config.fieldName + "_MimeType") + '">{0}' +
                    ' <i class="fa fa-info-circle infoview" ' +
                    'style="visibility:hidden" ' +
                    'data-qtip="' + CMDBuildUI.locales.Locales.attachments.viewmetadata + '" ' +
                    'onclick="CMDBuildUI.util.helper.AttachmentsHelper.viewFileFieldDetails(this); return false;"></i>' +
                    '</div>';

                CMDBuildUI.util.helper.ModelHelper.getModel(
                    CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                    config.dmsModel
                ).then(function (model) {
                    var fields = CMDBuildUI.util.helper.FormHelper.getFormFields(model),
                        infoShow;
                    fields.forEach(function (field) {
                        if (!field.hidden) {
                            infoShow = true;
                        }
                    });
                    if (infoShow) {
                        document.getElementById(containerId).querySelector("i").style = "visibility:visible";
                    }
                });
            }
            if (config.showPreview && config.record && config.fieldName) {
                var previewid = Ext.id({}, "preview-");
                CMDBuildUI.util.helper.AttachmentsHelper.getPreview(
                    config.objectType,
                    config.objectTypeName,
                    config.record.get("_id"),
                    value,
                    config.dmsCategory
                ).then(function (src) {
                    if (src) {
                        var img = document.getElementById(previewid);
                        img.src = src;
                        if (config.owner) {
                            Ext.asap(function () {
                                config.owner.updateLayout();
                            });
                        }
                    }
                });
                return Ext.String.format(
                    container,
                    '<img src="" id="' + previewid + '" onclick="CMDBuildUI.util.helper.AttachmentsHelper.openFileFieldPreview(this); return false;" />'
                );
            } else {
                var label = Ext.String.format(
                    '<span class="label" onclick="CMDBuildUI.util.helper.AttachmentsHelper.openFileFieldPreview(this); return false;">{0}</span> <small>({1})</small>',
                    config.record.get("_" + config.fieldName + "_name"),
                    Ext.util.Format.fileSize(config.record.get("_" + config.fieldName + "_Size"))
                );
                return Ext.String.format(container, label);
            }
        }
        return value;
    },

    /**
     * Render Text field.
     *
     * @param {String} value
     * @param {Object} config
     * @param {Boolean} config.html True if the field must be displayed as HTML field. Default: false
     * @param {Boolean} config.markdown True if the editor type is a editor MARKDOWN, otherwise false
     * @param {String} config.fieldname The field name.
     * @param {Ext.data.Model} config.record The card/process instance/view/.. record.
     * @param {Boolean} [config.skipnewline=false] Skip the conversion of new lines in br tags.
     *
     * @returns {String}
     *
     */
    renderTextField: function (value, config) {
        config = config || {};
        if (config.html || config.markdown) {
            if (config.record) {
                value = config.record.get(Ext.String.format('_{0}_html', config.fieldname)) || value;
            }
        } else {
            value = Ext.String.htmlEncode(value);
            if (!config.skipnewline) {
                value = Ext.util.Format.nl2br(value);
            }
        }
        return value;
    },

    /**
     * Return the configuration for HTML editor.
     *
     * @param {Object} config View Ext.form.field.HtmlEditor configurations.
     *
     * @returns {Object}
     *
     */
    getHTMLEditor: function (config) {
        return Ext.applyIf(config || {}, {
            xtype: 'cmdbuildhtmleditor',
            enableAlignments: true,
            enableColors: true,
            enableFont: false,
            enableFontSize: false,
            enableFormat: true,
            enableLinks: true,
            enableLists: true,
            enableSourceEdit: true,
            enableSignature: false
        });
    },

    /**
     * Return the rendered value of property field    
     * 
     * @param {@param {Ext.data.field.Field}} field 
     * @param {Number|String} value 
     * @param {Ext.app.ViewModel} [vm]
     * @param {String} [linkName] The vm linkName.     
     * 
     * @returns {String}
     * 
     */
    renderAttributeValue: function (field, value, vm, linkName) {
        var record;
        switch (field.cmdbuildtype.toLowerCase()) {
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                return CMDBuildUI.util.helper.FieldsHelper.renderThreeStateBooleanField(value);

            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase():
                return CMDBuildUI.util.helper.FieldsHelper.renderDateField(value);

            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase():
                return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value, {
                    hideSeconds: !field.metadata.showSeconds
                });

            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time.toLowerCase():
                return CMDBuildUI.util.helper.FieldsHelper.renderTimeField(value, {
                    hideSeconds: !field.metadata.showSeconds
                });

            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal.toLowerCase():
                return CMDBuildUI.util.helper.FieldsHelper.renderDecimalField(value, {
                    scale: field.metadata.scale,
                    showThousandsSeparator: field.metadata.showThousandsSeparator,
                    unitOfMeasure: field.metadata.unitOfMeasure,
                    unitOfMeasureLocation: field.metadata.unitOfMeasureLocation
                });

            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double.toLowerCase():
                return CMDBuildUI.util.helper.FieldsHelper.renderDoubleField(value, {
                    visibleDecimals: field.metadata.visibleDecimals,
                    showThousandsSeparator: field.metadata.showThousandsSeparator,
                    unitOfMeasure: field.metadata.unitOfMeasure,
                    unitOfMeasureLocation: field.metadata.unitOfMeasureLocation
                });

            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint.toLowerCase():
                return CMDBuildUI.util.helper.FieldsHelper.renderBigIntegerField(value, {
                    showThousandsSeparator: field.metadata.showThousandsSeparator,
                    unitOfMeasure: field.metadata.unitOfMeasure,
                    unitOfMeasureLocation: field.metadata.unitOfMeasureLocation
                });

            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer.toLowerCase():
                return CMDBuildUI.util.helper.FieldsHelper.renderIntegerField(value, {
                    showThousandsSeparator: field.metadata.showThousandsSeparator,
                    unitOfMeasure: field.metadata.unitOfMeasure,
                    unitOfMeasureLocation: field.metadata.unitOfMeasureLocation
                });

            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                if (linkName) {
                    record = vm.get(linkName);
                }
                return CMDBuildUI.model.lookups.Lookup.getLookupValueById(field.lookupType, value);;

            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase():
                if (linkName) {
                    record = vm.get(linkName);
                }
                return CMDBuildUI.util.helper.FieldsHelper.renderReferenceField(record ? record.get(field.name) : value, {
                    fieldName: field.name,
                    isHtml: field._html,
                    targetType: field.targetType,
                    targetTypeName: field.targetClass,
                    record: record
                });

            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string.toLowerCase():
                if (linkName) {
                    record = vm.get(linkName);
                }
                return CMDBuildUI.util.helper.FieldsHelper.renderTextField(value, {
                    html: field._html,
                    record: record,
                    fieldname: field.name
                });
            default:
                return value;
        }
    },

    privates: {
        /**
         *
         * @param {Number} number
         * @param {Number} decimalsToShow
         * @param {Boolean} showThousandsSeparator
         */
        formatNumber: function (number, decimalsToShow, showThousandsSeparator) {
            if (typeof number !== "number" && isNaN(number)) {
                return number;
            } else if (typeof number !== "number") {
                number = parseFloat(number);
            }
            var strnumber = number.toString();
            if (!Ext.isEmpty(decimalsToShow)) {
                strnumber = number.toFixed(decimalsToShow);
            }
            if (CMDBuildUI.util.helper.UserPreferences.getDecimalsSeparator() !== '.') {
                strnumber = strnumber.replace(".", CMDBuildUI.util.helper.UserPreferences.getDecimalsSeparator());
            }
            if (showThousandsSeparator) {
                try {
                    var re = new RegExp(
                        "\\B(?<!\\" + CMDBuildUI.util.helper.UserPreferences.getDecimalsSeparator() + "\\d*)(?=(\\d{3})+(?!\\d))",
                        'g'
                    );
                    strnumber = strnumber.replace(re, CMDBuildUI.util.helper.UserPreferences.getThousandsSeparator());

                } catch (error) {
                    var numberParts = strnumber.split(CMDBuildUI.util.helper.UserPreferences.getDecimalsSeparator());
                    numberParts[0] = numberParts[0].replace(/\B(?=(\d{3})+(?!\d))/g, CMDBuildUI.util.helper.UserPreferences.getThousandsSeparator());
                    strnumber = numberParts.join(CMDBuildUI.util.helper.UserPreferences.getDecimalsSeparator());
                    CMDBuildUI.util.Logger.log('regex error', CMDBuildUI.util.Logger.levels.debug);
                }
            }
            return strnumber;
        },

        /**
         *
         * @param {Date} date
         */
        removeTimeStamp: function (date) {
            var dateStringUTC = date.toUTCString();
            return dateStringUTC.split(' ').slice(0, 5).join(' ');
        }
    }

});