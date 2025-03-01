/**
 * @file CMDBuildUI.util.api.Languages
 * @module CMDBuildUI.util.api.Languages
 * @author Tecnoteca srl 
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Languages', {
    singleton: true,

    /**
     * Get available laguages url.
     * 
     * @returns {String}
     */
    getLanguagesUrl: function () {
        return '/configuration/languages';
    }
});