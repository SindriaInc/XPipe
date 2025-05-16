/**
 * @file CMDBuildUI.util.helper.GridHelper
 * @module CMDBuildUI.util.helper.GridHelper
 * @author Tecnoteca srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.GridHelper', {
    singleton: true,

    /**
     * Get columns for given type.
     *
     * @param {String} objectType Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} objectTypeName Class/Process/View/... name.
     * @param {Object} config Config object used in CMDBuildUI.util.helper.GridHelper.getColumns().
     *
     * @returns {Ext.promise.Promise<Ext.grid.column.Column[]>} Resolve method has as argument an
     *      instance of Ext.grid.column.Column. Reject method has as argument
     *      a String containing error message.
     *
     */
    getColumnsForType: function (objectType, objectTypeName, config) {
        var deferred = new Ext.Deferred(),
            item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);
        if (item) {
            config = Ext.applyIf(config || {}, {
                preferences: CMDBuildUI.util.helper.UserPreferences.getGridPreferences(objectType, objectTypeName),
                addTypeColumn: item.get("prototype"),
                objectType: objectType,
                objectTypeName: objectTypeName
            });

            CMDBuildUI.util.helper.ModelHelper.getModel(objectType, objectTypeName).then(function (model) {
                var columns = CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), config);
                deferred.resolve(columns);
            });
        } else {
            deferred.reject();
        }
        return deferred.promise;
    },

    /**
     * Returns columns definition for grids.
     *
     * @param {Ext.data.field.Field[]} fields Array of model fields.
     * @param {Object} config
     * @param {String[]|Boolean} config.allowFilter An array of columns on which enable filters or true to enable filter for each column.
     * @param {Boolean} config.addTypeColumn If `true` a new column is added as first item with object type.
     * @param {Boolean} config.reducedGrid If true shows the reducedGrid columns.
     * @param {Object} config.preferences Grid preferences.
     * @param {String[]} config.aggregate The columns that must show the sum.
     * @param {String} config.objectType the object type
     * @param {String} config.objectTypeName the name object
     *
     * @returns {Ext.grid.column.Column[]} An array of Ext.grid.column.Column definitions.
     *
     */
    getColumns: function (fields, config) {
        var columns = [],
            me = this;

        config = Ext.applyIf(config || {}, {
            allowFilter: false,
            addTypeColumn: false,
            reducedGrid: false,
            aggregate: []
        });

        var haspreferences = config.preferences && !Ext.isEmpty(config.preferences.columns);

        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled)) {
            var type = fields[0].owner.objectType,
                name = fields[0].owner.objectTypeName,
                objectdefinition = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(name, type),
                multitenantMode = objectdefinition ? objectdefinition.get("multitenantMode") : null;
            if (
                multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.always ||
                multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.mixed
            ) {
                // get hidden and flex properties from preferences
                var hidden = true,
                    flex = 0.8,
                    filter, orderIndex = -2;
                if (haspreferences) {
                    var tenantcolumn = Ext.Array.findBy(config.preferences.columns, function (item) {
                        return item.attribute === '_tenant';
                    });
                    hidden = tenantcolumn ? false : true;
                    flex = tenantcolumn ? tenantcolumn.width : 0;
                    orderIndex = tenantcolumn && tenantcolumn.orderIndex >= 0 ? tenantcolumn.orderIndex : -2;
                }

                // add filter
                if (config && config.allowFilter !== undefined && (
                    (Ext.isBoolean(config.allowFilter) && config.allowFilter === true) ||
                    (Ext.isArray(config.allowFilter) && config.allowFilter.indexOf(field.name) !== -1)
                )) {
                    filter = {
                        type: 'list',
                        store: {
                            proxy: 'memory',
                            data: CMDBuildUI.util.helper.SessionHelper.getActiveTenants()
                        },
                        idField: 'code',
                        labelField: 'description'
                    }
                }

                // add column
                columns.push({
                    text: CMDBuildUI.util.Utilities.getTenantLabel(),
                    dataIndex: "_tenant",
                    attributename: '_tenant',
                    hidden: hidden,
                    flex: flex,
                    filter: filter,
                    orderIndex: orderIndex,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        var tenants = CMDBuildUI.util.helper.SessionHelper.getActiveTenants(),
                            t = Ext.Array.findBy(tenants, function (i) {
                                return i.code == value;
                            });
                        return t && t.description;
                    }
                });

            }
        }

        if (config.addTypeColumn) {
            var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(config.objectTypeName, config.objectType),
                children = object.getChildren(true),
                filterItems = Ext.Array.map(children, function (item, index, array) {
                    return { _id: item.getId(), text: item.get("_description_translation") || item.get("description") };
                }),
                filter = {
                    type: 'list',
                    cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    options: Ext.Array.sort(filterItems, function (a, b) {
                        return a.text < b.text ? -1 : a.text > b.text ? 1 : 0;
                    }),
                    idField: '_id',
                    labelField: 'text',
                    menuDefaults: {
                        scrollable: true,
                        layout: {
                            type: 'vbox',
                            align: 'stretchmax',
                            overflowHandler: null
                        }
                    }
                },
                hidden, flex, orderIndex;

            if (!haspreferences) {
                hidden = false;
                flex = 0.8;
                orderIndex = -1;
            } else {
                var typecolumn = Ext.Array.findBy(config.preferences.columns, function (item) {
                    return item.attribute === '_type';
                });

                hidden = typecolumn ? false : true;
                flex = typecolumn ? typecolumn.width : 0;
                orderIndex = typecolumn && typecolumn.orderIndex >= 0 ? typecolumn.orderIndex : -1;
            }

            columns.push({
                text: CMDBuildUI.locales.Locales.common.grid.subtype,
                dataIndex: '_type',
                attributename: '_type',
                hidden: hidden,
                flex: flex,
                filter: filter,
                orderIndex: orderIndex,
                renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                    return CMDBuildUI.util.helper.ModelHelper.getObjectDescription(value);
                }
            });
        }

        var hasvisiblefields = false;
        Ext.Array.each(fields, function (field, index) {
            var column = me.getColumn(field, config);
            if (column) {
                // override column based on preferences
                if (haspreferences) {
                    var name = column.attributename || column.dataIndex,
                        colpref = Ext.Array.findBy(config.preferences.columns, function (item) {
                            return item.attribute === name;
                        });
                    if (colpref) {
                        column.hidden = false;
                        column.flex = colpref.width;
                        column.orderIndex = colpref.orderIndex >= 0 ? colpref.orderIndex : index;
                    } else {
                        column.hidden = true;
                        column.orderIndex = index;
                    }
                }
                // add column
                columns.push(column);
                hasvisiblefields = !column.hidden ? true : hasvisiblefields;
            }
        });

        if (!hasvisiblefields) {
            var desccol = Ext.Array.findBy(columns, function (c) {
                return c.dataIndex === "Description" || c.dataIndex === "Name" || c.dataIndex === "Code";
            });
            if (desccol) {
                desccol.hidden = false;
            }
            var namecol = Ext.Array.findBy(columns, function (c) {
                return c.dataIndex === "Name";
            });
            if (namecol) {
                namecol.hidden = false;
            }
        }
        // sort attributes on orderIndex property
        return columns.sort(function (a, b) {
            return a.orderIndex - b.orderIndex;
        });
    },

    /**
     * Returns column definition.
     *
     * @param {Ext.data.field.Field} field Model field.
     * @param {Object} config
     * @param {String[]|Boolean} config.allowFilter An array of columns on which enable filters or true to enable filter for each column.
     * @param {Boolean} config.reducedGrid If true shows the reducedGrid columns.
     * @param {String} config.objectType the object type
     * @param {String} config.objectTypeName the name object    
     *
     * @returns {Ext.grid.column.Column} An Ext.grid.column.Column definition.
     *
     */
    getColumn: function (field, config) {
        var column,
            me = this,
            attrconf = field.attributeconf;
        config = config || {};
        if (!Ext.String.startsWith(field.name, "_") && !field.hidden && !attrconf.hideInGrid) {
            column = {
                text: field.isInstance ? field.getDescription() : attrconf._description_translation || field.description || field.name,
                dataIndex: field.name,
                attributename: field.attributename,
                hidden: config.reducedGrid ? !attrconf.showInReducedGrid : !attrconf.showInGrid,
                sortable: attrconf.sortingEnabled
            };

            if (field.attributename && field.name !== field.attributename) {
                column.sorter = {
                    property: field.attributename
                };
            }

            // add resize listener
            column.listeners = {
                resize: function (column) {
                    if (column.textEl.dom.scrollWidth > column.textEl.dom.clientWidth) {
                        column.el.dom.dataset.qtip = column.text;
                        column.el.dom.dataset.qalign = 'tl-bl';
                    } else if (column.el.dom.dataset.qtip) {
                        delete column.el.dom.dataset.qtip;
                    }
                }
            };

            switch (field.cmdbuildtype.toLowerCase()) {
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                    column.flex = 0.3;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderThreeStateBooleanField(value);
                    };
                    break;
                /**
                 * Date fields
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase():
                    column.flex = 0.5;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderDateField(value);
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time.toLowerCase():
                    column.flex = 0.5;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderTimeField(value, {
                            hideSeconds: !field.attributeconf.showSeconds
                        });
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase():
                    column.flex = 0.8;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value, {
                            hideSeconds: !field.attributeconf.showSeconds
                        });
                    };
                    break;
                /**
                 * Numeric fields
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal.toLowerCase():
                    column.flex = 0.5;
                    column.tdCls = Ext.baseCSSPrefix + "numericcell";
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (!Ext.isEmpty(value)) {
                            return Ext.String.format(
                                "<div class=\"{0}cell-content\">{1}</div>",
                                Ext.baseCSSPrefix,
                                CMDBuildUI.util.helper.FieldsHelper.renderDecimalField(value, {
                                    scale: field.attributeconf.scale,
                                    showThousandsSeparator: field.attributeconf.showThousandsSeparator,
                                    unitOfMeasure: field.attributeconf.unitOfMeasure,
                                    unitOfMeasureLocation: field.attributeconf.unitOfMeasureLocation
                                })
                            );
                        }
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double.toLowerCase():
                    column.flex = 0.5;
                    column.tdCls = Ext.baseCSSPrefix + "numericcell";
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (!Ext.isEmpty(value)) {
                            return Ext.String.format(
                                "<div class=\"{0}cell-content\">{1}</div>",
                                Ext.baseCSSPrefix,
                                CMDBuildUI.util.helper.FieldsHelper.renderDoubleField(value, {
                                    visibleDecimals: field.attributeconf.visibleDecimals,
                                    showThousandsSeparator: field.attributeconf.showThousandsSeparator,
                                    unitOfMeasure: field.attributeconf.unitOfMeasure,
                                    unitOfMeasureLocation: field.attributeconf.unitOfMeasureLocation
                                })
                            );
                        }
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint.toLowerCase():
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer.toLowerCase():
                    column.flex = 0.5;
                    column.tdCls = Ext.baseCSSPrefix + "numericcell";
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (!Ext.isEmpty(value)) {
                            return Ext.String.format(
                                "<div class=\"{0}cell-content\">{1}</div>",
                                Ext.baseCSSPrefix,
                                CMDBuildUI.util.helper.FieldsHelper.renderIntegerField(value, {
                                    showThousandsSeparator: field.attributeconf.showThousandsSeparator,
                                    unitOfMeasure: field.attributeconf.unitOfMeasure,
                                    unitOfMeasureLocation: field.attributeconf.unitOfMeasureLocation
                                })
                            );
                        }
                    };
                    break;
                /**
                 * Relation fields
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookupArray.toLowerCase():
                    column.flex = 0.8;
                    CMDBuildUI.model.lookups.LookupType.loadLookupValues(field.attributeconf.lookupType);
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (value) {
                            return CMDBuildUI.util.helper.FieldsHelper.renderLookupArrayField(value, {
                                lookupType: field.attributeconf.lookupType,
                                fieldName: field.name,
                                record: record
                            });
                        }
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                    column.flex = 0.8;
                    CMDBuildUI.model.lookups.LookupType.loadLookupValues(field.attributeconf.lookupType);
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderLookupField(value, {
                            lookupIdField: field.attributeconf.lookupIdField,
                            lookupType: field.attributeconf.lookupType,
                            fieldName: field.name,
                            record: record
                        });
                    };
                    break;

                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase():
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                    column.flex = 1;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderReferenceField(value, {
                            fieldName: field.name,
                            isHtml: field.attributeconf._html,
                            targetType: field.attributeconf.targetType,
                            targetTypeName: field.attributeconf.targetClass,
                            stripTags: true,
                            record: record
                        });
                    };
                    break;
                /**
                 * File field
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file.toLowerCase():
                    column.flex = 0.8;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderFileField(value, {
                            record: record,
                            fieldName: field.name
                        });
                    };
                    break;
                /**
                 * IP Address
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress.toLowerCase():
                    column.flex = 0.8;
                    if (field.attributeconf.ipType === "ipv4") {
                        column.flex = 0.5;
                    }
                    break;
                /**
                 * Text fields
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char.toLowerCase():
                    column.flex = 0.3;
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string.toLowerCase():
                    column.flex = 1;
                    if (field.attributeconf.maxLength < 100 && field.attributeconf.maxLength >= 50) {
                        column.flex = 0.8;
                    } else if (field.attributeconf.maxLength < 50) {
                        column.flex = 0.5;
                    }
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (field.attributeconf.password && record.get(Ext.String.format('_{0}_has_value', field.name))) {
                            value = "•••••";
                        }
                        return me.renderTextColumn(value, field.attributeconf._html);
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
                    column.flex = 1;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return me.renderTextColumn(value, field.attributeconf._html);
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link.toLowerCase():
                    column.flex = 0.8;
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return me.renderTextColumn(value, true);
                    };
                    break;
            }

            // add column filter
            if (config && config.allowFilter !== undefined && (
                (Ext.isBoolean(config.allowFilter) && config.allowFilter === true) ||
                (Ext.isArray(config.allowFilter) && config.allowFilter.indexOf(field.name) !== -1)
            )) {
                switch (field.cmdbuildtype.toLowerCase()) {
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                        column.filter = {
                            type: 'boolean',
                            serializer: function (value) {
                                return !!value;
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char.toLowerCase():
                        column.filter = {
                            type: 'string',
                            itemDefaults: {
                                maxLength: 1
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase():
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time.toLowerCase():
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase():
                        column.filter = {
                            type: 'date'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal.toLowerCase():
                        column.filter = {
                            type: 'numeric',
                            itemDefaults: {
                                decimalPrecision: field.attributeconf.scale
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double.toLowerCase():
                        column.filter = {
                            type: 'numeric'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint.toLowerCase():
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer.toLowerCase():
                        column.filter = {
                            type: 'numeric',
                            itemDefaults: {
                                decimalPrecision: 0
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress.toLowerCase():
                        column.filter = {
                            type: 'string'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookupArray.toLowerCase():
                        var store;
                        if (config.objectType && [CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, CMDBuildUI.util.helper.ModelHelper.objecttypes.process].indexOf(config.objectType) > -1) {
                            store = Ext.create('Ext.data.Store', {
                                model: 'CMDBuildUI.model.lookups.Lookup',
                                proxy: {
                                    type: 'baseproxy',
                                    url: CMDBuildUI.util.api.Lookups.getLookupValues(field.attributeconf.lookupType),
                                    extraParams: {
                                        forClass: config.objectTypeName,
                                        forAttr: attrconf.name
                                    }
                                },
                                pageSize: 0,
                                autoLoad: true,
                                autoDestroy: true
                            });
                        } else {
                            var lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(field.attributeconf.lookupType);
                            store = lt ? lt.values() : [];
                        }
                        column.filter = {
                            type: 'list',
                            cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookupArray,
                            store: store,
                            idField: '_id',
                            labelField: 'text',
                            loadOnShow: false,
                            operator: 'overlap',
                            menuDefaults: {
                                scrollable: true,
                                layout: {
                                    type: 'vbox',
                                    align: 'stretchmax',
                                    overflowHandler: null
                                }
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                        var store;
                        if (config.objectType && [CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, CMDBuildUI.util.helper.ModelHelper.objecttypes.process].indexOf(config.objectType) > -1) {
                            store = Ext.create('Ext.data.Store', {
                                model: 'CMDBuildUI.model.lookups.Lookup',
                                proxy: {
                                    type: 'baseproxy',
                                    url: CMDBuildUI.util.api.Lookups.getLookupValues(field.attributeconf.lookupType),
                                    extraParams: {
                                        forClass: config.objectTypeName,
                                        forAttr: attrconf.name
                                    }
                                },
                                pageSize: 0,
                                autoLoad: true,
                                autoDestroy: true
                            });
                        } else {
                            var lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(field.attributeconf.lookupType);
                            store = lt ? lt.values() : [];
                        }
                        column.filter = {
                            type: 'list',
                            cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            store: store,
                            idField: '_id',
                            labelField: 'text',
                            loadOnShow: false,
                            menuDefaults: {
                                scrollable: true,
                                layout: {
                                    type: 'vbox',
                                    align: 'stretchmax',
                                    overflowHandler: null
                                }
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase():
                        column.filter = {
                            type: 'reference'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string.toLowerCase():
                        if (!field.attributeconf.password) {
                            column.filter = {
                                type: 'string'
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
                        column.filter = {
                            type: 'string'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link.toLowerCase():
                        column.filter = {
                            type: 'string'
                        };
                        break;
                }
                // use original attribute name for filter
                if (column.filter) {
                    column.filter.dataIndex = field.attributeconf.name;
                }
            }
            // add column sum
            if (config.aggregate && config.aggregate.indexOf(field.name) !== -1) {
                column.summaryType = 'sum';
                column.summaryRenderer = function (value) {
                    return column.renderer.call({}, value);
                };
            }

        }
        return column;
    },

    /**
     * Returns column editor definition for given field.
     * This method gets the field configuration from CMDBuild.util.helper.FormHelper.getEditorForField()
     * and adapts for the column.
     *
     * @param {Ext.data.field.Field} field Model field.
     * @param {Object} config Config object to use in CMDBuild.util.helper.FormHelper.getEditorForField() method.
     *
     * @returns {Ext.form.field.Field} An Ext.form.field.Field definition.
     *
     */
    getEditorForField: function (field, config) {
        var editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(field, config);

        switch (field.cmdbuildtype.toLowerCase()) {
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                editor.xtype = "referencecombofield";
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                editor.padding = "auto auto auto 10px";
                break;
        }
        return editor;
    },

    /**
     * Return Print button definition.
     *
     * @param {Object} config View Ext.button.Button for custom configuration.
     *
     * @returns {Ext.button.Button} Print button definition.
     *
     */
    getPrintButtonConfig: function (config) {
        config = config || {};
        var buttonCongif = {
            xtype: 'button',
            ui: 'management-action',
            iconCls: 'x-fa fa-print',
            tooltip: CMDBuildUI.locales.Locales.common.grid.print,
            arrowVisible: false,
            autoEl: {
                'data-testid': 'grid-printbtn'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.grid.print'
            },
            menu: [{
                iconCls: 'x-fa fa-file-pdf-o',
                itemId: 'printPdfBtn',
                text: CMDBuildUI.locales.Locales.common.grid.printpdf,
                printformat: 'pdf',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.grid.printpdf'
                }
            }, {
                iconCls: 'x-fa fa-file-excel-o',
                itemId: 'printCsvBtn',
                text: CMDBuildUI.locales.Locales.common.grid.printcsv,
                printformat: 'csv',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.grid.printcsv'
                }
            }, {
                iconCls: 'x-fa fa-map-o',
                itemId: 'printMapBtn',
                text: CMDBuildUI.locales.Locales.common.grid.printmap,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.grid.printmap'
                },
                hidden: true,
                bind: {
                    hidden: '{!btnHide}'
                }
            }]
        };

        return Ext.applyIf(config, buttonCongif);
    },

    /**
     * Return Buffered grid counter definition.
     *
     * @param {String} storeName
     *
     * @returns {Object}
     *
     */
    getBufferedGridCounterConfig: function (storeName, config) {
        if (storeName) {
            var config = config || {},
                bind = Ext.applyIf({
                    store: '{' + storeName + '}'
                }, config.bind);

            return Ext.applyIf({
                xtype: 'bufferedgridcounter',
                padding: '0 20 0 0',
                bind: bind
            }, config);
        }
    },

    /**
     * Get process flow status column
     *
     * @returns {Ext.grid.column.Column} Column definition.
     *
     */
    getProcessFlowStatusColumn: function () {
        return {
            dataIndex: "status",
            enableColumnHide: false,
            hideable: false,
            draggable: false,
            sortable: false,
            menuDisabled: true,
            width: "38px",
            maxWidth: "38px",
            minWidth: "38px",
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                var output = "";
                // get formatted description
                if (value) {
                    var lookupvalue = CMDBuildUI.model.lookups.Lookup.getLookupValueById(CMDBuildUI.model.processes.Process.flowstatus.lookuptype, value);
                    if (lookupvalue) {
                        var icon = lookupvalue.get("icon_font") || "x-fa fa-square",
                            icon_color = lookupvalue.get("icon_color") || "inherit",
                            txt = lookupvalue.get("_description_translation") || lookupvalue.get("description");

                        output = Ext.String.format(
                            "<span class=\"{0}\" style=\"color: {1}; cursor: help;\" data-qtip=\"{2}\"></span>",
                            icon,
                            icon_color,
                            txt
                        );
                    }
                }
                return output;
            }
        };
    },

    /**
     * Return Save grid preferences tool definition.
     *
     * @returns {Ext.panel.Tool}
     *
     */
    getSaveGridPreferencesTool: function () {
        return {
            xtype: 'tool',
            cls: Ext.baseCSSPrefix + 'tool-gray',
            itemId: 'savePreferencesBtn',
            tooltip: CMDBuildUI.locales.Locales.main.preferences.gridpreferencessave,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.main.preferences.gridpreferencessave'
            },
            bind: {
                hidden: '{btnHide}'
            }
        };
    },

    /**
     * Set the icon for grid preferences
     * @param {*} view 
     */
    setIconGridPreferences: function (view) {
        var vm = view.getViewModel(),
            toolSave = view.down("#savePreferencesBtn");
        if (Ext.Object.isEmpty(CMDBuildUI.util.helper.UserPreferences.getGridPreferences(vm.get("objectType"), vm.get("objectTypeName")))) {
            toolSave.setIconCls("x-fa fa-save");
        } else {
            toolSave.setIconCls("cmdbuildicon-save");
        }
    },

    /**
     * Save grid preferences.
     *
     * @param {Ext.grid.Panel} grid
     * @param {Ext.panel.Tool} tool
     * @param {String} objectType Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} objectTypeName Class/Process/View/... name.
     *
     */
    saveGridPreferences: function (grid, tool, objectType, objectTypeName) {
        function savePreferences() {
            var config = {
                columns: [],
                defaultOrder: []
            };
            // columns
            var columns = grid.getVisibleColumns();
            columns.forEach(function (column, index) {
                var attribute = column.attributename || column.initialConfig.dataIndex;
                if (attribute) {
                    config.columns.push({
                        attribute: attribute,
                        width: Math.round(column.getWidth() / grid.getWidth() * 100) / 100,
                        orderIndex: index
                    });
                }
            });
            // sorters
            grid.getStore().getSorters().getRange().forEach(function (sorter) {
                config.defaultOrder.push({
                    attribute: sorter.getProperty(),
                    direction: sorter.getDirection() === 'ASC' ? 'ascending' : 'descending'
                });
            });

            // update preferences
            CMDBuildUI.util.helper.UserPreferences.updateGridPreferences(objectType, objectTypeName, config).then(function () {
                CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.main.preferences.gridpreferencessaved);
                tool.setIconCls("cmdbuildicon-save");
            });
        }

        function clearPreferences() {
            // clear preferences
            CMDBuildUI.util.helper.UserPreferences.updateGridPreferences(objectType, objectTypeName, {
                columns: undefined,
                defaultOrder: undefined
            }).then(function () {
                CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.main.preferences.gridpreferencescleared);
                tool.setIconCls("x-fa fa-save");
            });
        }

        if (Ext.Object.isEmpty(CMDBuildUI.util.helper.UserPreferences.getGridPreferences(objectType, objectTypeName))) {
            // save preferences if not saved yet
            savePreferences();
        } else {
            // show menu to chose action update or clear
            if (tool.menu) {
                tool.menu.show();
            } else {
                tool.menu = Ext.create('Ext.menu.Menu', {
                    autoShow: true,
                    items: [{
                        text: CMDBuildUI.locales.Locales.main.preferences.gridpreferencesupdate,
                        iconCls: 'x-fa fa-save',
                        handler: function () {
                            savePreferences();
                        }
                    }, {
                        text: CMDBuildUI.locales.Locales.main.preferences.gridpreferencesclear,
                        iconCls: 'x-fa fa-remove',
                        handler: function () {
                            clearPreferences();
                        }
                    }]
                });
                tool.menu.alignTo(tool.el.id, 't-b?');
            }
        }
    },

    /**
     * Return Form in row widget definition
     *
     * @param {String} type Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {Object} config Base configuration.
     *
     * @returns {Ext.tab.Panel}
     *
     */
    getFormInRowWidget: function (type, config) {
        var xtype;
        config = config || {};

        switch (type) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                xtype = 'classes-cards-tabpanel';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                xtype = 'processes-instances-tabpanel';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar:
                xtype = 'events-tabpanel';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                xtype = 'dms-tabpanel';
                break;
        }

        var baseconf = {
            xtype: xtype,
            ui: 'managementlighttabpanel',
            cls: 'tabbarwithtools',
            padding: '0 0 8 0',
            tabPosition: 'top',
            readOnlyTabs: true,
            bind: {}
        };
        return Ext.apply(baseconf, config);
    },

    privates: {
        /**
         * Remove tags if is html or convert tags to string if is plain.
         *
         * @param {String} value
         * @param {Boolean} html
         */
        renderTextColumn: function (value, html) {
            if (!html) {
                value = CMDBuildUI.util.helper.FieldsHelper.renderTextField(value);
            }
            return Ext.util.Format.stripTags(value);
        }
    }
});