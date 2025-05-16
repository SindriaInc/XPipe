Ext.define('CMDBuildUI.view.administration.content.lookuptypes.ViewModel', {
    imports: [
        'CMDBuildUI.util.Utilities'
    ],
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-lookuptypes-view',
    data: {
        activeTab: 0,
        objectTypeName: null,
        action: null,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        disabledTabs: {
            list: false,
            values: false
        },
        allLookupTypesProxy: {
            type: 'memory'
        },
        parentTypeName: null,
        parentstoredata: {
            url: null,
            autoLoad: false
        },
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },

    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_lookups_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', !this.get('theLookupType._is_system') && data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },
        lookupLabel: {
            bind: '{theLookupType.description}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.lookuptypes.toolbar.classLabel;
            }
        },
        tabManager: {
            bind: {
                action: '{action}',
                theLookupType: '{theLookupType}'
            },
            get: function (bind) {
                this.set('activeTab', this.getView().up('administration-content').getViewModel().get('activeTabs.lookuptypes') || 0);
                this.set('actions.view', bind.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', bind.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', bind.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);

                if (bind.action !== CMDBuildUI.util.administration.helper.FormHelper.formActions.view) {
                    this.set('allLookupTypesProxy', {
                        url: "/lookup_types",
                        type: 'baseproxy',
                        extraParams: {
                            active: true
                        }
                    });
                } else {
                    this.set('allLookupTypesProxy', {
                        type: 'memory'
                    });
                }

                if (bind.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add || bind.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit) {
                    this.set('disabledTabs.list', false);
                    this.set('disabledTabs.values', true);

                } else {
                    this.set('disabledTabs.list', false);
                    this.set('disabledTabs.values', false);
                }
            }
        },

        

        parentLookupValuesProxy: {
            bind: '{theLookupType.parent}',
            get: function (parentTypeName) {
                if (parentTypeName && !this.get('theLookupType').phantom) {
                    this.set('parentstoredata.url', Ext.String.format("/lookup_types/{0}/values", CMDBuildUI.util.Utilities.stringToHex(parentTypeName)));
                    this.set('parentstoredata.autoLoad', true);
                }
            }
        }

    },
    stores: {
        allLookupsStore: {
            source: "lookups.LookupTypes",
            sorters: [
                'name'
            ],
            autoDestroy: true
        },
        parentLookupsStore: {
            model: "CMDBuildUI.model.lookups.Lookup",
            proxy: {
                url: '{parentstoredata.url}',
                type: 'baseproxy'
            },
            extraParams: {
                active: false
            },
            pageSize: 0, // disable pagination
            fields: ['_id', 'description'],
            autoLoad: '{parentstoredata.autoLoad}',
            sorters: ['description']
        }
    }
});