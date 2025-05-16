Ext.define('CMDBuildUI.view.filters.attachments.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.filters-attachments-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#metadatacombo': {
            select: 'onMetadataComboSelect'
        },
        '#removebutton': {
            click: 'onRemoveButtonClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.filters.attachments.Panel} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        // view._fieldsetsreferences = [];
        var me = this,
            vm = view.lookupViewModel();
        vm.bind({
            objecttype: '{objectType}',
            objecttypename: '{objectTypeName}'
        }, function (params) {
            // get class/process definition
            var categoryType = CMDBuildUI.util.helper.AttachmentsHelper.getCategoryType(
                params.objecttypename,
                params.objecttype
            );
            var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(params.objecttypename, params.objecttype);
            var values = object.get('dmsCategories');

            // get object dms category type
            var modelsNames = [];
            categoryType.getCategoryValues().then(function (categories) {
                var categoriesStoreData = [];
                values.forEach(function (item, index, value) {
                    var category = categories.findRecord('code', item.category);

                    Ext.Array.include(modelsNames, category.get("modelClass"));
                    categoriesStoreData.push({
                        label: category.get("_description_translation"),
                        value: category.get("_id"),
                        model: category.get("modelClass")
                    });
                }, this);

                // set cagetories store data
                vm.set("catetoriesvalues", categoriesStoreData);

                // // prepare store data
                if (modelsNames.length > 0) {
                    var promises = [],
                        hierachies = {},
                        multipleModels = modelsNames.length > 1;

                    // add promises for DMSModel model generator
                    modelsNames.forEach(function (modelName) {
                        // add promise to get DMS model model
                        promises.push(
                            CMDBuildUI.util.helper.ModelHelper.getModel(
                                CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                                modelName
                            )
                        );

                        // if there are multiple models get model hierarchy
                        if (multipleModels) {
                            var modelDefinition = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(modelName);
                            hierachies[modelName] = modelDefinition.getHierarchy();
                        }
                    });

                    // get common model for multiple models
                    if (multipleModels) {
                        var intersect = Ext.Array.intersect.apply(null, Ext.Object.getValues(hierachies)),
                            commonSuperModel = intersect[intersect.length - 1];

                        Ext.Array.insert(promises, 0, [CMDBuildUI.util.helper.ModelHelper.getModel(
                            CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                            commonSuperModel
                        )]);
                    }

                    // generate models
                    Ext.Promise.all(promises).then(function (models) {
                        var commonModel = Ext.Array.splice(models, 0, 1),
                            commonFields = commonModel[0].getFields(),
                            commonFieldsNames = [],
                            modelsFields = {},
                            storedata = [],
                            allfields = {};

                        // get common fields names
                        commonFields.forEach(function (cf) {
                            var name = cf.name;
                            commonFieldsNames.push(name);

                            if (!Ext.String.startsWith(name, "_") && !cf.hidden && !cf.attributeconf.hideInFilter) {
                                storedata.push({
                                    value: name,
                                    label: cf.attributeconf.description_localized || cf.description,
                                    category_description: null
                                });

                                allfields[name] = cf;
                            }
                        });

                        storedata.push({
                            value: "Category",
                            label: CMDBuildUI.locales.Locales.attachments.category,
                            category_description: null
                        }, {
                            value: "FileName",
                            label: CMDBuildUI.locales.Locales.attachments.filename,
                            category_description: null
                        });

                        allfields.Category = {
                            cmdbuildtype: 'dmscategory',
                            attributeconf: {
                                _description_translation: CMDBuildUI.locales.Locales.attachments.category
                            }
                        };
                        allfields.FileName = {
                            cmdbuildtype: 'string',
                            attributeconf: {
                                _description_translation: CMDBuildUI.locales.Locales.attachments.filename,
                                maxLength: 100
                            }
                        };

                        if (multipleModels) {
                            // get specific fields for other models
                            models.forEach(function (model) {
                                var modelFields = model.getFields(),
                                    fields = [];
                                modelFields.forEach(function (field) {
                                    if (
                                        !Ext.String.startsWith(field.name, "_") &&
                                        !field.hidden &&
                                        !field.attributeconf.hideInFilter &&
                                        Ext.Array.indexOf(commonFieldsNames, field.name) === -1
                                    ) {
                                        fields.push(field);
                                    }
                                });
                                if (fields.length) {
                                    modelsFields[model.objectTypeName] = fields;
                                }
                            });

                            // add missinig fields for categories
                            categoriesStoreData.forEach(function (category) {
                                var fields = modelsFields[category.model];
                                if (fields) {
                                    fields.forEach(function (field) {
                                        var v = me.getCategoryValueId(category.value, field.name);
                                        storedata.push({
                                            value: v,
                                            label: field.attributeconf.description_localized || field.description,
                                            category_description: category.label,
                                            category: category.value,
                                            model: category.model
                                        });
                                        allfields[v] = field;
                                    });
                                }
                            });
                        }

                        vm.set("allfields", allfields);
                        vm.set("metadatavalues", storedata);

                        me._populateWithFilterData(allfields, categoriesStoreData);
                    });
                }
            });
        });
    },

    onRemoveButtonClick: function (button, e, eOpts) {
        // get fieldset
        var fielset = button.up("fieldset");
        var fieldsetid = fielset.getReference();
        // destroy row
        var parent = button.up("filters-attributes-row");
        parent.destroy();
        // first child
        var firstrow = fielset.child("filters-attributes-row");
        if (!firstrow) {
            fielset.destroy();
        }
    },

    /**
     * Clear text
     */
    onSearchTextClearClick: function () {
        this.getViewModel().set("attachments.searchtext", null);
    },

    /**
     * 
     * @param {Ext.form.field.ComboBox} combo 
     * @param {CMDBuildUI.model.base.ComboItem} record 
     * @param {Object} eOpts 
     */
    onMetadataComboSelect: function (combo, record, eOpts) {
        if (record) {
            this._addFilterRow({
                attribute: record.get("value"),
                label: record.get("label"),
                category: record.get("category"),
                category_description: record.get("category_description"),
                model: record.get("model")
            });
            combo.setValue();
        }
    },

    privates: {
        /**
         * 
         * @param {Number|String} category 
         * @param {String} attrName 
         */
        getCategoryValueId: function (category, attrName) {
            return Ext.String.format("{0}_{1}", category, attrName);
        },

        /**
         * 
         * @param {Object} values
         * @param {String} values.attribute
         * @param {String} values.label
         * @param {String} values.operator
         * @param {Boolean} values.typeinput
         * @param {*} values.value1
         * @param {*} values.value2
         * @param {String} [values.category_description]
         * @param {String} [values.model]
         */
        _addFilterRow: function (values) {
            this.getViewModel().set("attachments.operator_label", true);
            // get attribute fieldset
            var fieldsetid = "metadatacontainer-" + values.attribute;
            var fieldset = this.lookup(fieldsetid);
            if (!fieldset) {
                var label = values.label;
                if (values.category_description) {
                    label = Ext.String.format("{0} - {1}", values.category_description, values.label);
                }
                fieldset = Ext.create("Ext.form.FieldSet", {
                    reference: fieldsetid,
                    title: label,
                    ui: 'formpagination',
                    collapsible: false,
                    viewModel: {
                        data: {
                            fielsetid: fieldsetid
                        }
                    },
                    items: [CMDBuildUI.view.filters.attributes.Row.getHeader({
                        hideInputLabel: true
                    })]
                });
                this.lookup('attributescontainer').add(fieldset);
            }
            // add the row to fieldset
            fieldset.add({
                xtype: 'filters-attributes-row',
                allowInputParameter: false,
                viewModel: {
                    data: {
                        values: Ext.clone(values)
                    }
                }
            });
        },

        /**
         * 
         * @param {Object} allfields 
         * @param {Object[]} categoriesdata 
         */
        _populateWithFilterData: function (allfields, categoriesdata) {
            var me = this,
                filter = this.getViewModel().get("theFilter"),
                config = filter.get("configuration"),
                metadata = CMDBuildUI.util.helper.FiltersHelper.decodeAttachmentsMetadataFilter(config.attachment);

            function prepareFilterRow(attrName, attr, field) {
                var row = {
                    attribute: attrName,
                    label: field.attributeconf._description_translation,
                    operator: attr.operator,
                    value1: Ext.isArray(attr.value) && attr.value[0] || null,
                    value2: Ext.isArray(attr.value) && attr.value[1] || null
                };

                if (
                    (field.cmdbuildtype === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup ||
                        field.cmdbuildtype === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookupArray) &&
                    CMDBuildUI.util.helper.FiltersHelper.isOperatorForRefernceOrLookupDescription(row.operator)
                ) {
                    row.value1 = null;
                    row.referencetext = Ext.isArray(attr.value) && attr.value[0] || null;
                }
                return row;
            }

            if (metadata.attributes) {
                Ext.Object.getKeys(metadata.attributes).forEach(function (attrName) {
                    var field = allfields[attrName];
                    metadata.attributes[attrName].forEach(function (attr) {
                        me._addFilterRow(prepareFilterRow(attrName, attr, field));
                    });
                });
            }
            if (metadata.categories) {
                Ext.Object.getKeys(metadata.categories).forEach(function (categoryId) {
                    var category = Ext.Array.findBy(categoriesdata, function (c) {
                        return c.value == categoryId;
                    });
                    Ext.Object.getKeys(metadata.categories[categoryId].attributes).forEach(function (attrName) {
                        var fieldName = me.getCategoryValueId(categoryId, attrName),
                            field = allfields[fieldName];
                        metadata.categories[categoryId].attributes[attrName].forEach(function (attr) {
                            var row = prepareFilterRow(fieldName, attr, field);
                            row.category = categoryId;
                            row.category_description = category && category.label;
                            row.model = metadata.categories[categoryId].model;
                            me._addFilterRow(row);
                        });
                    });
                });
            }
        }
    }

});