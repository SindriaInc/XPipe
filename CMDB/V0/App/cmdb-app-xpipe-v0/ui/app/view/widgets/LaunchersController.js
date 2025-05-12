Ext.define('CMDBuildUI.view.widgets.LaunchersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-launchers',

    control: {
        '#': {
            widgetschanged: 'onWidgetsChanged',
            widgetbuttonclick: 'onWidgetButtonClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.Launchers} view
     * @param {Ext.data.Store} newvalue
     * @param {Ext.data.Store} oldvalue
     */
    onWidgetsChanged: function (view, newvalue, oldvalue) {
        var vm = view.lookupViewModel(),
            addedwidgets = 0;
        if (newvalue && newvalue.getData().length) {
            Ext.Array.each(newvalue.getRange(), function (widget, index) {
                if (widget.get("_active") && !widget.get("_inline")) {
                    // add value binding
                    var readmode = view.getFormMode() === CMDBuildUI.util.helper.FormHelper.formmodes.read
                    widgettype = widget.get("_type");

                    var wconf = {
                        xtype: 'widgets-button',
                        reference: 'widgetbutton_' + index,
                        itemId: 'widgetbutton_' + widget.get("_id"),
                        text: widget.get("_label_translation") + (widget.get("_required") ? ' *' : ''),
                        disabled: !CMDBuildUI.view.widgets.Launchers.isWidgetAddable(view.getFormMode(), widget),
                        required: widget.get("_required"),
                        handler: function (button, e) {
                            view.fireEvent("widgetbuttonclick", view, button, widget, e);
                        },
                        listeners: {
                            render: function (button) {
                                button.lookupViewModel().bind({
                                    bindTo: '{theObject.' + widget.get("_output") + '}'
                                }, function (_output) {
                                    button.setValue(_output);
                                    button.isValid();
                                });
                            }
                        }
                    };

                    var isCustomWidget = !CMDBuildUI.util.Config.widgets[widgettype];
                    if (isCustomWidget) {
                        var widgetsStore = Ext.StoreManager.get("customcomponents.Widgets");

                        function loadCustomWidget() {
                            var w = widgetsStore.findRecord("name", widgettype);
                            if (w) {
                                Ext.require(Ext.String.format("CMDBuildUI.{0}",
                                    w.get("componentId")
                                ));
                            } else {
                                CMDBuildUI.util.Msg.alert(
                                    'Warning',
                                    Ext.String.format('Widget <strong>{0}</strong> not implemented!', widgettype)
                                );
                            }
                        }

                        if (!widgetsStore.isLoaded()) {
                            widgetsStore.load({
                                callback: loadCustomWidget
                            });
                        } else {
                            loadCustomWidget();
                        }
                    }

                    try {
                        // add widget button
                        widget._ownerButton = view.add(wconf);
                        addedwidgets += 1;
                    } catch (e) {
                        CMDBuildUI.util.Logger.log(
                            "Malformed widget configuration.",
                            CMDBuildUI.util.Logger.levels.warn,
                            null,
                            wconf
                        );
                    }
                }
            });

            vm.bind({
                theObject: '{theObject}'
            }, function(data) {
                CMDBuildUI.util.helper.WidgetsHelper.executeOnTargetFormOpen(
                    data.theObject,
                    newvalue.getRange(), {
                        formmode: view.getFormMode(),
                        form: view
                    }
                );
            }, {
                single: true
            });

            // show panel
            vm.set("hideLaunchersPanel", !addedwidgets);
        }
    },

    /**
     * Return the name of the model used by the widget.
     * @return {String}
     */
    getModelName: function (theWidget) {
        return 'CMDBuildUI.model.customform.' + theWidget.getId();
    },

    /**
     * @param {Ext.Component} view
     * @param {Ext.button.Button} button
     * @param {CMDBuildUI.model.WidgetDefinition} widget
     * @param {Event} e
     * @param {Object} eOpts
     */
    onWidgetButtonClick: function (view, button, widget, e, eOpts) {
        // update ajax action id
        CMDBuildUI.util.Ajax.setActionId(Ext.String.format(
            'widget.open.{0}.{1}',
            widget.get("_type"),
            widget.getId()
        ));

        var popup;
        // create widget configuration
        var config = CMDBuildUI.view.widgets.Launchers.getWidgetConfig(
            widget,
            view.lookupViewModel().get(view.getTargetLinkName()),
            view.up("form")
        );

        // custom event listener
        config.listeners = {
            /**
             * Custom event to close popup directly from widget
             */
            popupclose: function (eOpts) {
                popup.close();
            }
        };

        // custom panel listeners
        var listeners = {
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            beforeclose: function (panel, eOpts) {
                panel.removeAll(true);
            },
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                button.fireEvent('validitychange', button, button.isValid());
            }
        };

        // open popup
        popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            widget.get("_label_translation"),
            config,
            listeners
        );
    }
});