Ext.define('CMDBuildUI.view.fields.schedulerdate.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-schedulerdate-view',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#opentool': {
            click: 'onOpenBtnClick'
        },
        '#editBtn': {
            click: 'onEditButton'
        },
        '#deleteBtn': {
            click: 'onDeleteBtn'
        }
    },


    /**
     * 
     * @param {*} view 
     * @param {*} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.bind('{events-event-view.theEvent}', view.generateForm, view);
    },

    /**
    * Triggered on open tool click.
    * 
    * @param {Ext.panel.Tool} tool
    * @param {Ext.Event} event
    * @param {Object} eOpts
    */
    onOpenBtnClick: function (tool, event, eOpts) {
        var me = this;
        var view = this.getView();

        var popup = CMDBuildUI.util.Utilities.openPopup(null, CMDBuildUI.locales.Locales.calendar.viewevent, {
            xtype: 'events-event-view',
            shownInPopup: true,
            theEvent: view.getTheEvent(),
            viewModel: {
                data: {
                    hiddenbtns: {
                        open: true
                    }
                }
            },
            controller: 'fields-schedulerdate-view',
            listeners: {
                // the scope is the events-event-view view opened in the popup
                deletebutton: function (tool, event, eOpts) {
                    this.getController().onDeleteBtn(tool, event, eOpts);
                    popup.close();
                }
            }
        });
    },

    /**
     * Triggered on edit tool click.
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onEditButton: function () {
        var me = this;
        var view = me.getView();
        var theEvent = view.getTheEvent();
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
                    me.onOpenBtnClick();

                    me.getView().up('events-grid').updateRowWithExpader(theEvent);
                    popup.close();
                },
                popupsaveandclose: function (view, theEventClone) {
                    this.getController().savePopup(theEvent);

                    me.getView().up('events-grid').updateRowWithExpader(theEvent);
                    popup.close();
                },
                popupclose: function (view, theEventClone) {
                    popup.close();
                }
            }
        });
    },

    /**
    * Triggered on open tool click.
    * 
    * @param {Ext.panel.Tool} tool
    * @param {Ext.Event} event
    * @param {Object} eOpts
    */
    onDeleteBtn: function (tool, event, eOpts) {
        var theEvent = this.getView().getTheEvent();
        var theSequence = theEvent.store.associatedEntity;

        //field like dirty. This information tells that there are removed records.
        theSequence._dirty_delete = true;

        theEvent.drop()
    }
});
