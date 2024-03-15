/**
 * @file CMDBuildUI.model.AttributeGrouping
 * @class CMDBuildUI.model.AttributeGrouping
 * @author Tecnoteca srl 
 * @access public
 */
Ext.define('CMDBuildUI.model.AttributeGrouping', {
    extend: 'Ext.data.Model',

    statics: {
        /**
         * @static
         * 
         * @type {String}
         * 
         */
        nogroup: '_nogroup',

        /**
         * @static
         * 
         * @type {Object}
         * @property {String} open
         * @property {String} closed
         * 
         */
        displayMode: {
            open: 'open',
            closed: 'closed'
        }
    },
    fields: [{
        name: 'name',
        type: 'string',
        defaultValue: ''
    }, {
        name: 'description',
        type: 'string',
        defaultValue: ''
    }, {
        name: 'index',
        type: 'number',
        defaultValue: null
    }, {
        name: 'defaultDisplayMode',
        type: 'string',
        defaultValue: 'open'
    }],
    proxy: {
        type: 'memory'
    }
});