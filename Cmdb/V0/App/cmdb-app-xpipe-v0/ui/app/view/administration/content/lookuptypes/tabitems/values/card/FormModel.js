Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-lookuptypes-tabitems-values-card',

    data: {
        theValue: null,
        actions: {
            view: false,
            add: false,
            edit: false
        },
        valueIconType: {
            isImageOrNone: false,
            isFontOrNone: false,
            isNone: false
        },
        isIconFileRequired: false,
        parentTypeName: null,
        storedata: {
            url: null,
            autoLoad: false
        },
        theTranslation: null,
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },

    formulas: {
        action: {
            bind: {
                theValue: '{theValue}',
                view: '{actions.view}',
                add: '{actions.add}',
                edit: '{actions.edit}'
            },
            get: function (data) {
                if (data.theValue.get('_type')) {
                    this.set('theLookupType', Ext.getStore('lookups.LookupTypes').findRecord('name', this.get('theValue._type')));
                }
                if (data.edit) {
                    this.set('formModeCls', 'formmode-edit');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.add) {
                    this.set('formModeCls', 'formmode-add');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    this.set('formModeCls', 'formmode-view');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },
        iconTypeIsImage: {
            bind: '{theValue.icon_type}',
            get: function (iconType) {
                if (iconType && iconType === CMDBuildUI.model.lookups.Lookup.icontypes.image) {
                    return true;
                }
                return false;
            }
        },
        iconTypeIsFont: {
            bind: '{theValue.icon_type}',
            get: function (iconType) {
                if (iconType && iconType === 'font') {
                    return true;
                }
                return false;
            }
        },
        parentDescriptionData: {
            bind: {
                parentId: '{theValue.parent_id}',
                store: '{parentLookupsStoreCard}'
            },
            get: function (data) {
                var me = this;
                var parent;
                if (data.store.getData().getRange().length) {
                    parent = data.store.getData().getRange().find(function (element) {
                        return element.get('_id') === data.parentId.toString();
                    });
                    me.set('parentDescription', (parent) ? parent.get('description') : '');
                } else if (!data.store.isLoaded()) {
                    data.store.on('load', function () {
                        parent = data.store.getData().getRange().find(function (element) {
                            return element.get('_id') === data.parentId.toString();
                        });
                        me.set('parentDescription', (parent) ? parent.get('description') : '');
                    });
                }

            }
        },

        iconTypeManager: {
            bind: {
                iconType: '{theValue.icon_type}',
                iconImage: '{theValue.icon_image}'
            },
            get: function (data) {
                this.set('valueIconType.isImageOrNone', data.iconType && (data.iconType === CMDBuildUI.model.lookups.Lookup.icontypes.image || data.iconType === CMDBuildUI.model.lookups.Lookup.icontypes.none));
                this.set('valueIconType.isFontOrNone', data.iconType && (data.iconType === CMDBuildUI.model.lookups.Lookup.icontypes.font || data.iconType === CMDBuildUI.model.lookups.Lookup.icontypes.none));
                this.set('valueIconType.isNone', data.iconType && data.iconType === CMDBuildUI.model.lookups.Lookup.icontypes.none);
                this.set('isIconFileRequired', data.iconType && data.iconType === CMDBuildUI.model.lookups.Lookup.icontypes.image && !data.iconImage.length);
            }
        },

        panelTitle: {
            bind: {
                description: '{theValue.description}',
                lookupType: '{theLookupType}'
            },
            get: function (data) {
                var title;
                if (data.description) {
                    title = Ext.String.format(
                        '{0} - {1}',
                        data.lookupType.get('description'),
                        data.description
                    );

                } else {
                    title = Ext.String.format(
                        '{0}',
                        data.lookupType.get('description')
                    );
                }
                this.getParent().set('title', title);
            }
        },

        iconTypes: {
            get: function () {
                return CMDBuildUI.model.lookups.Lookup.getIconTypes();
            }
        },
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_lookups_modify}',
                _is_system: '{theLookupType._is_system}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', !data._is_system && data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', !data._is_system && data.canModify === true);
                this.set('toolAction._canActiveToggle', !data._is_system && data.canModify === true);
            }
        }
    },
    stores: {
        iconTypeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            fields: ['value', 'label'],
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{iconTypes}'
        },
        parentValuesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: [],
            autoLoad: true,
            autoDestroy: true
        }
    }
});