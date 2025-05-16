Ext.define('CMDBuildUI.view.fields.schedulerdate.GridController', {
    extend: 'CMDBuildUI.view.events.GridController',
    alias: 'controller.fields-schedulerdate-gridcontroller',
    control: {
        '#': {
            rowdblclick: 'onRowDblClick'
        }
    },

    /**
     * @param {Ext.selection.RowModel} element
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {HTMLElement} rowIndex
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRowDblClick: function (element, record, rowIndex, e, eOpts) {
        var me = this;
        var gridview = me.getView();
        var theEvent = record;
        var theEventClone = theEvent.copy(null);

        var popup = CMDBuildUI.util.Utilities.openPopup('editEventPopup', CMDBuildUI.locales.Locales.calendar.editevent, {
            xtype: 'events-event-edit',
            theEvent: theEventClone,
            hideTools: true,
            controller: 'fields-schedulerdate-edit',
            listeners: {
                // this.getView().fireEvent('popupsave', this.getView());
                popupsave: function (view, theEventClone) {
                    this.getController().savePopup(theEvent);
                    // me.onOpenBtnClick();

                    gridview.updateRowWithExpader(theEvent);
                    popup.close();
                },
                popupsaveandclose: function (view, theEventClone) {
                    this.getController().savePopup(theEvent);

                    gridview.updateRowWithExpader(theEvent);
                    popup.close();
                },
                popupclose: function (view, theEventClone) {
                    popup.close();
                }
            }
        });
    }
})