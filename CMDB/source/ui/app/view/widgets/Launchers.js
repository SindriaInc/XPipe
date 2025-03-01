Ext.define('CMDBuildUI.view.widgets.Launchers', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.widgets.LaunchersController',
        'CMDBuildUI.view.widgets.LaunchersModel'
    ],

    statics: {
        /**
         * Returns the widget definition
         *
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} target
         * @param {Ext.form.Panel} view
         * @returns {Object}
         */
        getWidgetConfig: function (widget, target, view) {
            var widgettype = widget.get("_type"),
                xtype = CMDBuildUI.util.Config.widgets[widgettype];

            // search widget in custom widgets store
            if (!xtype) {
                var store = Ext.StoreManager.get("customcomponents.Widgets");
                var w = store.findRecord("name", widgettype);
                xtype = w.get("alias").replace("widget.", "");
            }

            // break execution and show warning if widget does not exists
            if (!xtype) {
                CMDBuildUI.util.Msg.alert('Warning', 'Widget not found');
                return;
            }

            // check dms feature if widget is attachments widget
            if (
                widgettype === 'openAttachment' &&
                !CMDBuildUI.util.helper.Configurations.getEnabledFeatures().dms
            ) {
                CMDBuildUI.util.Msg.alert('Warning', 'Attachments disabled!');
                return;
            }

            // return widget config
            var widgetConfig = {
                xtype: xtype,
                itemId: Ext.String.format('{0}-{1}', xtype, widget.getId()),
                widgetId: widget.getId(),
                output: widget.get("_output"),
                formMode: view.formmode,
                _widgetOwner: view,
                viewModel: {
                    data: {
                        theWidget: widget,
                        theTarget: target
                    }
                },
                bind: {
                    target: '{theTarget}'
                }
            };
            return widgetConfig;
        },

        /**
         * Check if the widget is addable.
         *
         * @param {String} formmode
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         */
        isWidgetAddable: function(formmode, widget) {
            switch(formmode) {
                case CMDBuildUI.util.helper.FormHelper.formmodes.read:
                    return widget.get('_alwaysenabled');
                case CMDBuildUI.util.helper.FormHelper.formmodes.create:
                    return !widget.get('_hideincreation');
                case CMDBuildUI.util.helper.FormHelper.formmodes.update:
                    return !widget.get('_hideinedit');
            }
        },

        /**
         * Add inline widgets in form items
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition[]} widgets
         * @param {String} formmode
         * @param {Ext.Component[]} items
         */
        addInlineWidgets: function (target, widgets, form, items) {
            if (widgets.count()) {
                function getGroupIndex(groupId) {
                    var group = Ext.Array.findBy(items, function (i) {
                        return i && i.groupId == groupId;
                    });
                    return group ? Ext.Array.indexOf(items, group) : -1;
                }

                widgets.getRange().forEach(function (widget) {
                    if (CMDBuildUI.view.widgets.Launchers.isWidgetAddable(form.formmode, widget) && widget.get("_active")) {
                        var widgettype = widget.get("_type"),
                            wxtype = CMDBuildUI.util.Config.widgets[widgettype];

                        if (!wxtype) {
                            var store = Ext.StoreManager.get("customcomponents.Widgets"),
                                w = store.findRecord("name", widgettype);
                            if (w) {
                                wxtype = w.get("alias").replace("widget.", "");
                            } else {
                                CMDBuildUI.util.Logger.log(
                                    "Widget " + widgettype + " not found!",
                                    CMDBuildUI.util.Logger.levels.error
                                );
                                return;
                            }
                        }

                        var index, position,
                            fieldset = {
                                xtype: 'widgets-fieldset',
                                title: widget.get("_label_translation"),
                                required: widget.get("_required"),
                                collapsed: widget.get("_inlineclosed"),
                                items: [CMDBuildUI.view.widgets.Launchers.getWidgetConfig(
                                    widget,
                                    target,
                                    form
                                )],
                                listeners: {
                                    added: function (fieldset, container, pos, eOpts) {
                                        widget._ownerPanel = fieldset;

                                        if (widget.get("_output")) {
                                            fieldset.lookupViewModel().bind({
                                                bindTo: '{theObject.' + widget.get("_output") + '}'
                                            }, function (_output) {
                                                fieldset.setValue(_output);
                                                fieldset.isValid();
                                            });
                                        }
                                    }
                                }
                            };

                        if (widget.get("_inlineafter")) {
                            index = getGroupIndex(widget.get("_inlineafter"));
                            position = 'after';
                        } else if (widget.get("_inlinebefore")) {
                            index = getGroupIndex(widget.get("_inlinebefore"));
                            position = 'before';
                        }

                        if (index !== -1 && position === 'after') {
                            Ext.Array.insert(items, index + 1, [fieldset]);
                        } else if (index !== -1 && position === 'before') {
                            Ext.Array.insert(items, index, [fieldset]);
                        } else {
                            items.push(fieldset);
                        }
                    }
                });
            }
        }
    },

    alias: 'widget.widgets-launchers',
    controller: 'widgets-launchers',
    viewModel: {
        type: 'widgets-launchers'
    },

    cls: Ext.baseCSSPrefix + 'widgets-launchers-container',

    config: {
        /**
         * @cfg {Ext.data.Store} [widgets]
         * A store of {CMDBuildUI.model.WidgetDefinition} instances.
         */
        widgets: null,

        /**
         * @cfg {String} [formmode]
         * Possible values are `read`, `update` and `create`.
         * Default value is `read`.
         */
        formMode: 'read',

        /**
         * @cfg {String} [targetLinkName="theObject"]
         * The link name to use to pass the target to the widget.
         */
        targetLinkName: 'theObject'
    },
    publishes: [
        'widgets',
        'formMode'
    ],

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    hidden: true,

    bind: {
        hidden: '{hideLaunchersPanel}'
    },

    /**
     * @event widgetschanged
     * Fires when widgets property is changed.
     *
     * @param {Ext.Component} this
     * @param {Ext.data.Store} newvalue
     * @param {Ext.data.Store} oldvalue
     * @param {Object} eOpts
     */

    /**
     * @event widgetbuttonclick
     * Fires when widget button is clicked.
     *
     * @param {Ext.Component} this
     * @param {Ext.button.Button} button
     * @param {CMDBuildUI.model.WidgetDefinition} widget
     * @param {Event} e
     * @param {Object} eOpts
     */

    /**
     * @param {Ext.data.Store} newvalue
     * @param {Ext.data.Store} oldvalue
     */
    updateWidgets: function (newvalue, oldvalue) {
        this.fireEvent("widgetschanged", this, newvalue, oldvalue);
    }
});