/**
 * @file CMDBuildUI.view.widgets.gotocard
 * @module CMDBuildUI.view.widgets.gotocard
 * @author Tecnoteca srl
 * @access public
 */
Ext.define('CMDBuildUI.view.widgets.gotocard.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.widgets.gotocard.PanelController'
    ],

    alias: 'widget.widgets-gotocard-panel',
    controller: 'widgets-gotocard-panel',

    mixins: [
        'CMDBuildUI.view.widgets.Mixin'
    ],

    /**
    * @constant {String} ClassName
    * The name of the class to redirect to.
    */
    ClassName: null,

    /**
     * @constant {String} ObjId
     * The id of the class card to redirect to.
     */
    ObjId: {},

    /**
     * @constant {Boolean} IsAttachment
     * If True the redirection is done in the attachments tab.
     */
    IsAttachment: false

});