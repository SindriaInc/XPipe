/**
 * @file CMDBuildUI.util.helper.ContextMenuHelper
 * @module CMDBuildUI.util.helper.ContextMenuHelper
 * @author Tecnoteca srl 
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.ContextMenuHelper', {
    singleton: true,

    /**
     * Open custom context menu.
     * 
     * @param {CMDBuildUI.model.ContextMenuItem} item
     * @param {CMDBuildUI.model.classes.Card[]|CMDBuildUI.model.processes.Instance[]} selection
     * @param {CMDBuildUI.view.classes.cards.grid.Grid | CMDBuildUI.view.processes.instances.Grid} grid
     * 
     */
    openCustomComponent: function (item, selection, grid) {
        if (item) {
            Ext.require(Ext.String.format("CMDBuildUI.{0}",
                item.get("jscomponent")
            ), function () {
                var popup,
                    // create widget configuration
                    config = {
                        xtype: item.get("alias").replace("widget.", ""),
                        selection: selection,
                        ownerGrid: grid,
                        theComponent: item,
                        listeners: {
                            /**
                             * Custom event to close popup directly from widget
                             */
                            popupclose: function (eOpts) {
                                popup.close();
                            }
                        }
                    },
                    // custom panel listeners
                    listeners = {
                        /**
                         * @param {Ext.panel.Panel} panel
                         * @param {Object} eOpts
                         */
                        beforeclose: function (panel, eOpts) {
                            panel.removeAll(true);
                        }
                    };
                // open popup
                popup = CMDBuildUI.util.Utilities.openPopup(
                    null,
                    item.get("_label_translation") || item.get("label"),
                    config,
                    listeners
                );
            });
        }
    }
});