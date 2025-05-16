Ext.define('CMDBuildUI.model.importexports.GateGisHandler', {
    requires: ['CMDBuildUI.model.importexports.GateHandler'],
    extend: 'CMDBuildUI.model.importexports.GateHandler',
    statics: {
        gatetemplatesStore: null,
        type: {
            cad: 'cad',
            script: 'script',
            database: 'database',
            ifc: 'ifc'
        },
        includeOrExclude: {
            all: 'all',
            include: 'include',
            exclude: 'exclude'
        }
    },

    fields: [{
        // if type === 'cad'
        name: 'shape_import_enabled',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: false
    }, {
        // if shape_import_enabled === true
        //shape import target class
        name: 'shape_import_target_class',
        type: 'string', // should be an array
        persist: true,
        critical: true
    }, {
        //shape import target layer
        name: 'shape_import_target_attr', // if shape_import_enabled === true
        type: 'string',
        persist: true,
        critical: true
    }, {
        // shape import key source
        name: 'shape_import_key_source', // if shape_import_enabled === true
        type: 'string',
        persist: true,
        critical: true
    }, {
        // shape import key attr
        name: 'shape_import_key_attr', // if shape_import_enabled === true
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'shape_import_source_layers_include',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'shape_import_source_layers_exclude',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: '_shape_import_include_or_exclude',
        type: 'string',
        persist: false,
        critical: false,
        convert: function (value, record) {
            if (!value) {
                if (record.get('shape_import_source_layers_exclude').length) {
                    return CMDBuildUI.model.importexports.GateGisHandler.includeOrExclude.exclude;
                } else if (record.get('shape_import_source_layers_include').length) {
                    return CMDBuildUI.model.importexports.GateGisHandler.includeOrExclude.include;
                } else {
                    return CMDBuildUI.model.importexports.GateGisHandler.includeOrExclude.all;
                }
            }
            return value;
        }
    }]
});
