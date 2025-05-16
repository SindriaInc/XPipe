/**
 * @file CMDBuildUI.view.widgets.Mixin
 * @mixin CMDBuildUI.view.widgets.Mixin
 * @author Tecnoteca srl
 * @access public
 *
 * @classdesc
 * <p>
 * This mixin must be used in the main view of a custom widgets.
 * </p>
 * <p>
 * To get the widget definition you can use the method vm.get("theWidget")
 * which returns the instance of {@link CMDBuildUI.model.WidgetDefinition}.
 * </p>
 * <p>
 * To get the target on which the widget is called you can use the method
 * vm.get("theTarget") which returns an instance of {@link CMDBuildUI.model.classes.Card}
 * or {@link CMDBuildUI.model.processes.Instance}.
 * </p>
 */
Ext.define('CMDBuildUI.view.widgets.Mixin', {
    mixinId: 'cmdbuildwidgets-mixin',

    mixins: ['Ext.mixin.Bindable'],

    statics: {
        /**
         * This function must be overrinden as static.
         * Function executed when the form opens.
         *
         * @name onTargetFormOpen
         * @function
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         * @param {Ext.form.Panel} config.form
         *
         * @returns {Ext.promise.Promise}
         *
         */
        // onTargetFormOpen: function(target, widget, config) {
        //     var deferred = new Ext.Deferred();
        //     deferred.resolve();
        //     return deferred;
        // }

        /**
         * This function must be overrinden as static.
         * Function executed before save/execute action.
         *
         * @name beforeTargetSave
         * @function
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         * @param {String} config.action One of `save` and `execute`
         *
         * @returns {Ext.promise.Promise}
         *
         */
        // beforeTargetSave: function(target, widget, config) {
        //     var deferred = new Ext.Deferred();
        //     deferred.resolve();
        //     return deferred;
        // }

        /**
         * This function must be overrinden as static.
         * Function executed after save/execute action
         *
         * @name afterTargetSave
         * @function
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         * @param {String} config.action One of `save` and `execute`
         *
         * @returns {Ext.promise.Promise}
         *
         */
        // afterTargetSave: function(target, widget, config) {
        //     var deferred = new Ext.Deferred();
        //     deferred.resolve();
        //     return deferred;
        // }

        /**
         * This function must be overrinden as static.
         * Function executed before cancel action.
         *
         * @name onTargetCancel
         * @function
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         *
         * @returns {Ext.promise.Promise}
         *
         */
        // onTargetCancel: function(target, widget, config) {
        //     var deferred = new Ext.Deferred();
        //     deferred.resolve();
        //     return deferred;
        // }
    },

    config: {
        /**
         * The id of the widget. <br />
         * Use `getWidgetId()` to get the value of this property. <br />
         * Use `setWidgetId()` to set the value of this property.
         *
         * @type {String}
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         */
        widgetId: null,

        /**
         * The target object on which the widget is called. <br />
         * Use `getTarget()` to get the value of this property. <br />
         * Use `setTarget()` to set the value of this property.
         *
         * @type {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance}
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         */
        target: null,

        /**
         * The target attribute where output will be saved. <br />
         * Use `getOutput()` to get the value of this property. <br />
         * Use `setOutput()` to set the value of this property.
         *
         * @type {String}
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         */
        output: null,

        /**
         * Form mode. <br />
         * Use `getFormMode()` to get the value of this property. <br />
         * Use `setFormMode()` to set the value of this property.
         *
         * @type {String}
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         */
        formMode: null
    },

    publishes: [
        "target"
    ],

    twoWayBindable: [
        "target"
    ],

    /**
     * Fired to close popup.
     *
     * @method
     * @listens CMDBuildUI.view.widgets.Mixin:popupclose
     *
     * @memberof CMDBuildUI.view.widgets.Mixin
     */

    /**
     * Close the widget popup.
     *
     * @fires popupclose
     *
     * @memberof CMDBuildUI.view.widgets.Mixin
     */
    closePopup: function () {
        this.fireEvent("popupclose");
    },

    /**
     * Show message for not supported inline widgets:
     * createModifyCard | createReport | manageEmail | presetFromCard | sequenceView | startWorkflow
     *
     * @memberof CMDBuildUI.view.widgets.Mixin
     */
    showNotSupportedInlineMessage: function () {
        var me = this;

        me.removeAll();
        me.getDockedItems().forEach(function(item) {
            me.removeDocked(item);
        });
        me.add({
            xtype: 'panel',
            cls: Ext.baseCSSPrefix + 'selectable',
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            html: Ext.String.format('<i>{0}</i>', CMDBuildUI.locales.Locales.widgets.notsupportedinline)
        });
    }
});