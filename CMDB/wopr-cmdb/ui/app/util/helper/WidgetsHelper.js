/**
 * @file CMDBuildUI.util.helper.WidgetsHelper
 * @module CMDBuildUI.util.helper.WidgetsHelper
 * @author Tecnoteca srl 
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.WidgetsHelper', {
    singleton: true,

    /**
     * Execute actions "on target form open".
     * 
     * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} target 
     * @param {CMDBuildUI.model.WidgetDefinition[]} widgets 
     * @param {Object} config Config parameters required by the widget action.
     * 
     * @returns {Ext.promise.Promise<Boolean>} 
     * 
     */
    executeOnTargetFormOpen: function (target, widgets, config) {
        config = Ext.applyIf(config || {}, {
            formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read
        });
        return this._executeAction(target, widgets, config, "onTargetFormOpen");
    },

    /**
     * Execute actions "on before target save".
     * 
     * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} target 
     * @param {CMDBuildUI.model.WidgetDefinition[]} widgets 
     * @param {Object} config Config parameters required by the widget action.
     * 
     * @returns {Ext.promise.Promise<Boolean>} 
     * 
     */
    executeBeforeTargetSave: function (target, widgets, config) {
        config = Ext.applyIf(config || {}, {
            formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
            action: "save"
        });
        return this._executeAction(target, widgets, config, "beforeTargetSave");
    },

    /**
     * Execute actions "on after target save".
     * 
     * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} target 
     * @param {CMDBuildUI.model.WidgetDefinition[]} widgets 
     * @param {Object} config Config parameters required by the widget action.
     * 
     * @returns {Ext.promise.Promise<Boolean>} 
     * 
     */
    executeAfterTargetSave: function (target, widgets, config) {
        config = Ext.applyIf(config || {}, {
            formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
            action: "save"
        });
        return this._executeAction(target, widgets, config, "afterTargetSave");
    },

    /**
     * Execute actions "on execute target cancel".
     * 
     * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} target 
     * @param {CMDBuildUI.model.WidgetDefinition[]} widgets 
     * @param {Object} config Config parameters required by the widget action.
     * 
     * @returns {Ext.promise.Promise<Boolean>} 
     * 
     */
    executeOnTargetCancel: function (target, widgets, config) {
        config = Ext.applyIf(config || {}, {
            formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read
        });
        return this._executeAction(target, widgets, config, "onTargetCancel");
    },

    privates: {
        /**
         * 
         * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} target 
         * @param {CMDBuildUI.model.WidgetDefinition[]} widgets 
         * @param {Object} config 
         * @param {String} action Action name
         * 
         * @returns {Ext.promise.Promise} 
         */
        _executeAction: function (target, widgets, config, action) {
            var deferred = new Ext.Deferred();

            // define promises list
            var promises = [];

            // for each widget append in promises array 
            // the action if defined
            widgets.forEach(function (widget) {
                var cls = widget.getWidgetClass();
                if (cls && Ext.isFunction(cls[action])) {
                    promises.push(cls[action](target, widget, config));
                }
            })

            if (!Ext.isEmpty(promises)) {
                // wait for all promises
                Ext.Promise.all(promises).then(
                    function (response) {
                        var success = true;
                        response.forEach(function (r) {
                            success = success && r;
                        });
                        deferred.resolve(success);
                    },
                    function (error) {
                        deferred.reject();
                    }
                );
            } else {
                deferred.resolve(true);
            }

            return deferred.promise;
        }
    }
});
