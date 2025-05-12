Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.grid.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-lookuptypes-tabitems-values-grid-grid',
    data: {
        selected: null,
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
                canModify: '{theSession.rolePrivileges.admin_lookups_modify}',
                theLookupType: '{theLookupType}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', !data.theLookupType.get('_is_system') && data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', !data.theLookupType.get('_is_system') && data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },
        lookupValuesProxy: {
            bind: '{theLookupType.name}',
            get: function (objectTypeName) {                
                if (objectTypeName && !this.get('theLookupType').phantom) {
                    return {
                        url: Ext.String.format("/lookup_types/{0}/values", CMDBuildUI.util.Utilities.stringToHex(objectTypeName)),
                        type: 'baseproxy',
                        extraParams: {
                            active: false
                        }
                    };
                }
            }
        }
    },
    stores: {
        allValues: {
            model: 'CMDBuildUI.model.lookups.Lookup',
            proxy: '{lookupValuesProxy}',
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0,
            sorters: [{
                property: 'index',
                direction: 'ASC'
            }]
        }
    }

});