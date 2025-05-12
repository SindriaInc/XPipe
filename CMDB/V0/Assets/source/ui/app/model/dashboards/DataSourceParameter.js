Ext.define('CMDBuildUI.model.dashboards.DataSourceParameter', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        types: {
            integer: 'integer',
            'long': 'long',
            string: 'string',
            date: 'date'
        },
        getTypes: function () {
            return [{
                value: CMDBuildUI.model.dashboards.DataSourceParameter.types.integer,
                label: CMDBuildUI.locales.Locales.administration.dashboards.integer
            }, {
                value: CMDBuildUI.model.dashboards.DataSourceParameter.types.string,
                label: CMDBuildUI.locales.Locales.administration.dashboards.string
            }];
        },
        integerFieldTypes: {
            card: 'card',
            free: 'free',
            // classes will not be used anymore
            // classes: 'classes',
            lookup: 'lookup'
        },
        getIntegerFieldTypes: function () {
            return [{
                value: CMDBuildUI.model.dashboards.DataSourceParameter.integerFieldTypes.free,
                label: CMDBuildUI.locales.Locales.administration.dashboards.freestring
            }, {
                value: CMDBuildUI.model.dashboards.DataSourceParameter.integerFieldTypes.lookup,
                label: CMDBuildUI.locales.Locales.administration.dashboards.selectfromlookup
            }, {
                value: CMDBuildUI.model.dashboards.DataSourceParameter.integerFieldTypes.card,
                label: CMDBuildUI.locales.Locales.administration.dashboards.card
            }];
        },

        stringFieldTypes: {
            classes: 'classes',
            free: 'free',
            group: 'group',
            user: 'user'
        },
        getStringFieldTypes: function () {
            return [{
                value: CMDBuildUI.model.dashboards.DataSourceParameter.stringFieldTypes.free,
                label: CMDBuildUI.locales.Locales.administration.dashboards.freestring
            }, {
                value: CMDBuildUI.model.dashboards.DataSourceParameter.stringFieldTypes.classes,
                label: CMDBuildUI.locales.Locales.administration.dashboards.selectfromallclasses
            }, {
                value: CMDBuildUI.model.dashboards.DataSourceParameter.stringFieldTypes.user,
                label: CMDBuildUI.locales.Locales.administration.dashboards.currentuser
            }, {
                value: CMDBuildUI.model.dashboards.DataSourceParameter.stringFieldTypes.group,
                label: CMDBuildUI.locales.Locales.administration.dashboards.currentgroup
            }];
        }
    },

    fields: [{
        name: 'name',
        type: 'string',
        critical: true
    }, {
        name: 'description',
        type: 'string',
        convert: function (value, record) {
            return value || record.get('name');
        }
    }, {
        name: 'type',
        type: 'string',
        critical: true
    }, {
        name: 'required',
        type: 'boolean',
        critical: true,
        defaultValue: false
    }, {
        name: 'fieldType',
        type: 'string',
        critical: true
    }, {
        name: 'defaultValue',
        type: 'string',
        critical: true,
        defaultValue: '' // classname or lookup value or other values
    }, {
        name: 'lookupType',
        type: 'string',
        critical: true,
        defaultValue: ''
    }, {
        name: 'classToUseForReferenceWidget',
        type: 'string',
        critical: true,
        defaultValue: ''
    }, {
        name: 'filter',
        type: 'auto',
        critical: true,
        defaultValue: {
            expression: ''
        }
    }, {
        name: 'filterexpression',
        type: 'string',
        mapping: 'filter.expression',
        critical: false,
        persist: false
    }, {
        name: 'preselectIfUnique',
        type: 'boolean'
    }],

    proxy: {
        type: 'memory'
    },

    getDescription: function () {
        return this._description_translation || this.name;
    }
});