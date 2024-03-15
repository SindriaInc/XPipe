Ext.define('CMDBuildUI.view.administration.components.attributes.actionscontainers.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-attributes-actionscontainers-card',
    data: {
        attributeGroups: [],
        attributes: [],
        theAttribute: null,
        isOtherPropertiesHidden: true,
        isMandatoryHidden: false,
        isGroupHidden: true,
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        types: {
            isBigInteger: false,
            isDate: false,
            isDatetime: false,
            isDecimal: false,
            isDouble: false,
            isForeignkey: false,
            isFormula: false,
            isInteger: false,
            isIpAddress: false,
            isLink: false,
            isLookup: false,
            isReference: false,
            isString: false,
            isText: false,
            isTime: false,
            isTimestamp: false
        },
        toolAction: {
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false,
            _canClone: false,
            _canOpen: false,
            _canAdd: false
        },
        formulaCodeLabel: '',
        showInGridLabel: null,
        showInGridHidden: false
    },

    formulas: {
        attriubuteTypes: {
            bind: '{objectTypeName}',
            get: function (objectTypeName) {
                if (objectTypeName) {
                    return CMDBuildUI.model.Attribute.getTypes(objectTypeName);
                }

            }
        },
        textContentSecurities: {
            bind: '{theAttribute.type}',
            get: function (attributeType) {
                if (attributeType) {
                    if (attributeType === 'string' && !this.get('theAttribute.textContentSecurity').length) {
                        this.set('theAttribute.textContentSecurity', CMDBuildUI.model.Attribute.textContentSecurity.plaintext);
                    }
                    return CMDBuildUI.model.Attribute.getTextContentSecurities(attributeType);
                }
            }
        },
        theAttributeManager: {
            bind: {
                theAttribute: '{theAttribute}',
                objectType: '{objectType}'
            },
            get: function (data) {
                if (data.theAttribute) {
                    this.setToolActionStatuses(data);
                }
                if (data.objectType && data.objectType === 'Process') {
                    this.set('isMandatoryHidden', true);
                }
            }
        },

        actions: {
            bind: '{action}',
            get: function (action) {
                return {
                    add: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    edit: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    view: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                };
            }
        },
        isGroupHiddenOrView: {
            bind: {
                isGroupHidden: '{isGroupHidden}'
            },
            get: function (data) {
                if (data.isGroupHidden || this.get('actions.view')) {
                    return true;
                }
                return false;
            }
        },
        isGroupHiddenOrNotView: {
            bind: {
                isGroupHidden: '{isGroupHidden}'
            },
            get: function (data) {
                if (data.isGroupHidden || !this.get('actions.view')) {
                    return true;
                }
                return false;
            }
        },
        pluralObjectType: {
            bind: '{objectType}',
            get: function (objectType) {
                return objectType && Ext.util.Inflector.pluralize(objectType).toLowerCase();
            }
        },

        setCurrentType: {
            bind: '{theAttribute.type}',
            get: function (type) {
                this.set('types.isBigInteger', type === CMDBuildUI.model.Attribute.types.bigInteger);
                this.set('types.isBoolean', type === CMDBuildUI.model.Attribute.types['boolean']);
                this.set('types.isDate', type === CMDBuildUI.model.Attribute.types.date);
                this.set('types.isDatetime', type === CMDBuildUI.model.Attribute.types.dateTime);
                this.set('types.isDecimal', type === CMDBuildUI.model.Attribute.types.decimal);
                this.set('types.isDouble', type === CMDBuildUI.model.Attribute.types['double']);
                this.set('types.isFile', type === CMDBuildUI.model.Attribute.types.file);
                this.set('types.isForeignkey', type === CMDBuildUI.model.Attribute.types.foreignKey);
                this.set('types.isFormula', type === CMDBuildUI.model.Attribute.types.formula);
                this.set('types.isInteger', type === CMDBuildUI.model.Attribute.types.integer);
                this.set('types.isIpAddress', type === CMDBuildUI.model.Attribute.types.ipAddress);
                this.set('types.isLink', type === CMDBuildUI.model.Attribute.types.link);
                this.set('types.isLookup', type === CMDBuildUI.model.Attribute.types.lookup || type === CMDBuildUI.model.Attribute.types.lookuparray);
                this.set('types.isReference', type === CMDBuildUI.model.Attribute.types.reference);
                this.set('types.isString', type === CMDBuildUI.model.Attribute.types.string);
                this.set('types.isText', type === CMDBuildUI.model.Attribute.types.text);
                this.set('types.isTime', type === CMDBuildUI.model.Attribute.types.time);
                this.set('types.isTimestamp', type === CMDBuildUI.model.Attribute.types.dateTime);
            }
        },

        otherFieldManager: {
            bind: '{objectType}',
            get: function (objectType) {
                this.set('showInGridLabel', objectType.toLowerCase() === CMDBuildUI.util.helper.ModelHelper.objecttypes.domain ? CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinmainform : CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showingrid);
                this.set('isOtherPropertiesHidden', objectType !== 'Domain' ? false : true);
                if (objectType.toLowerCase() === CMDBuildUI.util.helper.ModelHelper.objecttypes.domain) {
                    var domain = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(this.get('objectTypeName'));
                    this.set('showInGridHidden', domain && domain.get('cardinality') === 'N:N');
                }
            }
        },
        panelTitle: {
            bind: '{theAttribute.description}',
            get: function (attributeName) {
                if (this.get('theAttribute') && !this.get('theAttribute').phantom) {
                    var title = Ext.String.format(
                        '{0} - {1} - {2}',
                        CMDBuildUI.util.helper.ModelHelper.getObjectFromName(this.get('objectTypeName')).get('description'),
                        CMDBuildUI.locales.Locales.administration.attributes.attributes,
                        attributeName
                    );
                    this.getParent().set('title', title);
                } else {
                    this.getParent().set('title', CMDBuildUI.locales.Locales.administration.attributes.texts.newattribute);
                }
            }
        },
        attributeGroups: {
            bind: '{attributes}',
            get: function (attributes) {
                var attributeGroups = [],
                    data = [];
                var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(this.get('objectTypeName'));
                if (obj && obj.attributeGroups) {
                    attributeGroups = obj.attributeGroups().getRange();
                } else {
                    Ext.Array.each(attributes, function (attribute) {
                        if (attribute.get('group') && attribute.get('group').length > 0) {
                            if (!Ext.Array.contains(data, attribute.get('group'))) {
                                Ext.Array.include(data, attribute.get('group'));
                                Ext.Array.include(attributeGroups, {
                                    description: attribute.get('_group_description'),
                                    name: attribute.get('group')
                                });
                            }
                        }
                    });
                }
                return attributeGroups;
            }
        },

        domainExtraparamsChained: {
            bind: '{objectTypeName}',
            get: function (objectTypeName) {
                var type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectTypeName);
                var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName);
                if (type && type !== CMDBuildUI.util.helper.ModelHelper.objecttypes.domain) {
                    return [function (item) {
                        var sourceFilter = item.get('active') && ['N:1', "1:1"].indexOf(item.get('cardinality')) > -1 && obj.getHierarchy().indexOf(item.get('source')) > -1;
                        var destinationFilter = item.get('active') && ['1:N', "1:1"].indexOf(item.get('cardinality')) > -1 && obj.getHierarchy().indexOf(item.get('destination')) > -1;
                        return sourceFilter || destinationFilter;
                    }];
                }
                return [];
            }
        },
        activeInputManager: {
            bind: {
                attributeCanModify: '{theAttribute._can_modify}',
                objectCanModify: '{theObject._can_modify}',
                objectType: '{objectType}',
                isMandatory: '{theAttribute.mandatory}'
            },
            get: function (data) {
                var canAdd = data.objectCanModify;
                if (data.objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.domain) {
                    canAdd = true;
                }
                this.set('activeInputDisabled', !(data.attributeCanModify && canAdd));
            }
        },

        directions: {
            get: function () {
                return [{
                    value: "direct",
                    label: CMDBuildUI.locales.Locales.administration.attributes.texts.direct
                }, {
                    value: "inverse",
                    label: CMDBuildUI.locales.Locales.administration.attributes.texts.inverse
                }];
            }
        },
        attributeModes: {
            get: function () {
                return [{
                    value: "write",
                    label: CMDBuildUI.locales.Locales.administration.attributes.strings.editable
                }, {
                    value: "read",
                    label: CMDBuildUI.locales.Locales.administration.attributes.strings.readonly
                }, {
                    value: "hidden",
                    label: CMDBuildUI.locales.Locales.administration.attributes.strings.hidden
                }, {
                    value: "immutable",
                    label: CMDBuildUI.locales.Locales.administration.attributes.strings.immutable
                }];
            }
        },
        editorTypes: {
            bind: '{objectType}',
            get: function (objectType) {
                var typesAll = [{
                    value: "PLAIN",
                    label: CMDBuildUI.locales.Locales.administration.attributes.strings.plaintext
                }];

                var typeClassAndProcess = [{
                    value: "HTML",
                    label: CMDBuildUI.locales.Locales.administration.attributes.strings.editorhtml
                }, {
                    value: "MARKDOWN",
                    label: CMDBuildUI.locales.Locales.administration.attributes.strings.editormarkdown
                }];

                if (objectType && objectType.toLowerCase() !== CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.toLowerCase()) {
                    return Ext.Array.merge([], typesAll, typeClassAndProcess);
                }
                return typesAll;
            }
        },
        ipTypes: {
            get: function () {
                return [{
                    value: "ipv4",
                    label: CMDBuildUI.locales.Locales.administration.attributes.strings.ipv4
                }, {
                    value: "ipv6",
                    label: CMDBuildUI.locales.Locales.administration.attributes.strings.ipv6
                }, {
                    value: "any",
                    label: CMDBuildUI.locales.Locales.administration.attributes.strings.any
                }];
            }
        },
        unitOfMeasures: {
            get: function () {
                return CMDBuildUI.util.administration.helper.ModelHelper.getUnitOfMeasures();
            }
        },

        formulaTypes: {
            get: function () {
                return CMDBuildUI.util.administration.helper.ModelHelper.getFormulaTypes();
            }
        },

        functionsStoreFilters: {
            get: function () {
                return [function (item) {
                    return Ext.Array.contains(item.get('tags') || [], 'formulaField');
                }];
            }
        },

        showPassword: {
            get: function () {
                return CMDBuildUI.util.administration.helper.ModelHelper.getShowPassword();
            }
        },
        stringTypes: {
            get: function () {
                return CMDBuildUI.util.administration.helper.ModelHelper.getAttributeStringTypes();
            }
        },
        formulaCodeManager: {
            bind: {
                formulaCode: '{theAttribute.formulaCode}',
                formulaType: '{theAttribute.formulaType}'
            },
            get: function (data) {
                var label = '';
                switch (data.formulaType) {
                    case CMDBuildUI.model.Attribute.formulaTypes.sql:
                        label = CMDBuildUI.locales.Locales.administration.common.labels.funktion;
                        break;
                    default:
                        label = CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.formulacode;
                        break;
                }
                this.set('formulaCodeLabel', label);
            }
        },
        dmsCategoryValuesDataManager: {
            bind: {
                objectType: '{objectType}',
                objectTypeName: '{objectTypeName}'
            },
            get: function (data) {
                var vm = this,
                    objectType = data.objectType.toLowerCase();
                if ([CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, CMDBuildUI.util.helper.ModelHelper.objecttypes.process].indexOf(objectType) > -1) {
                    var categoryType = CMDBuildUI.util.helper.AttachmentsHelper.getCategoryType(
                        data.objectTypeName,
                        objectType
                    );
                    var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objectTypeName, objectType);
                    if (object) {
                        object.load({
                            callback: function (record, operation, success) {
                                if (success) {
                                    var values = record.get('dmsCategories') || [];
                                    // get object dms category type
                                    var modelsNames = [];
                                    var categoriesStoreData = [];
                                    categoryType.getCategoryValues(true).then(function (categories) {
                                        if (!vm.isDestroyed) {
                                            values.forEach(function (item, index, value) {
                                                var category = categories.findRecord('code', item.category);
                                                if (category) {
                                                    Ext.Array.include(modelsNames, category.get("modelClass"));
                                                    categoriesStoreData.push({
                                                        label: category.get("_description_translation"),
                                                        value: category.get("code"),
                                                        model: category.get("modelClass"),
                                                        index: category.get('index')
                                                    });
                                                }
                                            }, vm);
                                        }

                                        // set cagetories store data
                                        vm.set("dmsCategoryValuesData", categoriesStoreData);
                                    });
                                }
                            }
                        });
                    }

                }
            }
        },

        formulaWarningMessage: {
            bind: {
                type: '{theAttribute.type}',
                hideInGrid: '{theAttribute.hideInGrid}'
            },
            get: function (data) {
                if (data.type === 'formula' && !data.hideInGrid) {
                    return CMDBuildUI.locales.Locales.administration.attributes.strings.formulawarningmessage;
                }
            }
        }
    },

    stores: {
        domainsStore: {
            source: "domains.Domains",
            autoDestroy: true,
            filters: '{domainExtraparamsChained}'
        },
        lookupStore: {
            model: "CMDBuildUI.model.lookups.Lookup",
            proxy: {
                type: 'baseproxy',
                url: '/lookup_types/'
            },
            autoLoad: true,
            autoDestroy: true,
            fields: ['_id', 'name'],
            pageSize: 0
        },
        attributeGroupStore: {
            model: "CMDBuildUI.model.Attribute",
            proxy: {
                type: 'memory'
            },
            data: '{attributeGroups}',
            fields: ['label', 'value'],
            autoDestroy: true
        },
        attributeModeStore: {
            data: '{attributeModes}',
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label']
        },
        editorTypeStore: {
            data: '{editorTypes}',
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label']
        },
        ipTypeStore: {
            data: '{ipTypes}',
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label']
        },
        directionStore: {
            data: '{directions}'
        },
        attributetypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            data: '{attriubuteTypes}'
        },
        textContentSecurityStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{textContentSecurities}'
        },
        unitOfMeasuresStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            data: '{unitOfMeasures}'
        },
        formulaTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            data: '{formulaTypes}'
        },
        functionsStore: {
            source: 'Functions',
            autoLoad: true,
            filters: '{functionsStoreFilters}'
        },
        scriptsStore: {
            source: 'customcomponents.Scripts',
            autoLoad: true
        },
        // formulaCodeStore: {
        //     fields: ['name', 'description'],
        //     proxy: {
        //         type: 'memory'
        //     },
        //     sorters: ['label'],
        //     data: '{formulaCodeData}',
        //     autoDestroy: true
        // },

        showPasswordStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            data: '{showPassword}'
        },
        stringEditorTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            data: '{stringTypes}'
        },
        dmsCategoryValueStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            sorters: ['index'],
            data: '{dmsCategoryValuesData}'
        }
    },

    setToolActionStatuses: function (data) {

        var theAttribute = data.theAttribute;
        var theSession = this.get('theSession');

        var pluralname = Ext.util.Inflector.pluralize(this.get('objectType')).toLowerCase();
        var objectTypePerm = Ext.String.format('admin_{0}_modify', pluralname);
        var canUpdate = theAttribute.get('_can_modify');
        try {
            this.set('toolAction._canAdd', (
                theSession.get('rolePrivileges')[objectTypePerm] ||
                !theSession.get('rolePrivileges').admin_all_readonly
            ));
        } catch (error) {
            CMDBuildUI.util.Logger.log("Unable to set addButton privileges", CMDBuildUI.util.Logger.levels.debug);
        }
        this.set('toolAction._canAdd', !theSession.get('admin_all_readonly'));
        this.set('toolAction._canOpen', true);
        var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(this.get('objectTypeName'));
        var canAdd = obj.get('_can_modify');
        if (obj.objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.domain) {
            canAdd = true;
        }
        this.set('toolAction._canUpdate', canUpdate);
        this.set('toolAction._canActiveToggle', !this.get('theAttribute.mandatory') && (canUpdate && canAdd));
        this.set('toolAction._canClone', canAdd);

        if (!theAttribute.get('inherited')) {
            this.set('toolAction._canDelete', canUpdate && canAdd);
        }
        if (data.objectType.toLowerCase() !== 'domain' && theAttribute.getId() === 'Description') {
            this.set('toolAction._canActiveToggle', false);
        }
    }
});