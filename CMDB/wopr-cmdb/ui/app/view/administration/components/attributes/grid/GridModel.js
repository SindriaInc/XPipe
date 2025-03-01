Ext.define('CMDBuildUI.view.administration.components.attributes.grid.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-attributes-grid-grid',
    data: {
        selected: null,
        showInGridText: null,
        isOtherPropertiesHidden: true
    },

    formulas: {
        pluralObjectType: {
            bind: '{objectType}',
            get: function (objectType) {
                this.set("showInGridText", objectType.toLowerCase() === CMDBuildUI.util.helper.ModelHelper.objecttypes.domain ? CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinmainform : CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showingrid);
                var pluralname = Ext.util.Inflector.pluralize(objectType).toLowerCase();
                var theSession = this.get('theSession');
                var objectTypePerm;
                switch (pluralname) {
                    case 'processes':
                    case 'classes':
                        objectTypePerm = Ext.String.format('admin_{0}_modify', pluralname);
                        try {
                            this.set('toolAction._canAdd', (
                                theSession.get('rolePrivileges')[objectTypePerm] &&
                                CMDBuildUI.util.helper.ModelHelper.getObjectFromName(this.get('objectTypeName')).get('_can_modify')
                            ));
                        } catch (error) {
                            CMDBuildUI.util.Logger.log("Unable to set addButton privileges", CMDBuildUI.util.Logger.levels.debug);
                        }
                        break;
                    case 'domains':
                        objectTypePerm = Ext.String.format('admin_{0}_modify', pluralname);
                        try {
                            this.set('toolAction._canAdd', theSession.get('rolePrivileges')[objectTypePerm]);
                        } catch (error) {
                            CMDBuildUI.util.Logger.log("Unable to set addButton privileges", CMDBuildUI.util.Logger.levels.debug);
                        }
                        break;
                    case 'dmsmodels':
                        try {
                            this.set('toolAction._canAdd', (
                                CMDBuildUI.util.helper.ModelHelper.getObjectFromName(this.get('objectTypeName')).get('_can_modify') &&
                                theSession.get('rolePrivileges').admin_dms_modify
                            ));
                        } catch (error) {
                            CMDBuildUI.util.Logger.log("Unable to set addButton privileges", CMDBuildUI.util.Logger.levels.debug);
                        }
                        break;

                    default:
                        break;
                }

                return;
            }
        },
        canDelete: {
            bind: {
                isInherited: '{theAttribute.inherited}'
            },
            get: function (data) {
                return (data.isInherited) ? false : true;
            }
        },

        setCurrentType: {
            bind: '{theAttribute.type}',
            get: function (type) {
                this.set('types.isBoolean', type === CMDBuildUI.model.Attribute.types['boolean']);
                this.set('types.isDate', type === CMDBuildUI.model.Attribute.types.date);
                this.set('types.isDatetime', type === CMDBuildUI.model.Attribute.types.dateTime);
                this.set('types.isDecimal', type === CMDBuildUI.model.Attribute.types.decimal);
                this.set('types.isDouble', type === CMDBuildUI.model.Attribute.types['double']);
                this.set('types.isForeignkey', type === CMDBuildUI.model.Attribute.types.foreignKey);
                this.set('types.isInteger', type === CMDBuildUI.model.Attribute.types.integer);
                this.set('types.isIpAddress', type === CMDBuildUI.model.Attribute.types.ipAddress);
                this.set('types.isLookup', type === CMDBuildUI.model.Attribute.types.lookup || type === CMDBuildUI.model.Attribute.types.lookuparray);
                this.set('types.isReference', type === CMDBuildUI.model.Attribute.types.reference);
                this.set('types.isString', type === CMDBuildUI.model.Attribute.types.string);
                this.set('types.isText', type === CMDBuildUI.model.Attribute.types.text);
                this.set('types.isTime', type === CMDBuildUI.model.Attribute.types.time);
                this.set('types.isTimestamp', type === CMDBuildUI.model.Attribute.types.dateTime);
                this.set('types.isBigInteger', type === CMDBuildUI.model.Attribute.types.bigInteger);
                this.set('types.isLink', type === CMDBuildUI.model.Attribute.types.link);
            }
        },
        allAtrributesGorups: function (get) {
            var allAttributesStore = get('allAttributesStore');

            var groups = [],
                data = [];

            Ext.Array.each(allAttributesStore, function (attribute) {
                var attributeData = attribute.getData();
                if (attributeData.group && attributeData.group.length > 0) {
                    if (!Ext.Array.contains(groups, attributeData.group)) {
                        Ext.Array.include(groups, attributeData.group);
                        Ext.Array.include(data, {
                            label: attributeData.group,
                            value: attributeData.group
                        });
                    }
                }
            });

            return data;
        }
    }

});