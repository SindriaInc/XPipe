Ext.define('CMDBuildUI.view.fields.schedulerdate.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-schedulerdate-edit',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#saveandclosebtn': {
            click: 'onSaveAndCloseBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * 
     * @param {*} view 
     * @param {*} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.bind('{events-event-edit.theEvent}', function () {
            view.generateForm.call(view);
        }, view);

        vm.bind('{events-event-create.theEvent.date}', function (dateChange) {
            var theEvent = this.getTheEvent()
            theEvent.set('begin', dateChange)
        }, view)
    },

    /**
   * @param {Ext.button.Button} button
   * @param {Event} e
   * @param {Object} eOpts
   */
    onSaveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var theEvent = view.getTheEvent();
        view.fireEvent('popupsave', view, theEvent);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndCloseBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var theEvent = view.getTheEvent();
        view.fireEvent('popupsaveandclose', view, theEvent);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var theEvent = view.getTheEvent();
        view.fireEvent('popupclose', view, theEvent);
    },

    /**
     * 
     * @param {*} originalEvent 
     */
    savePopup: function (originalEvent) {
        var theEvent = this.getView().getTheEvent();
        var modified = theEvent.modified;

        //sets the modified fields of the form --> in the original theEvent record
        if (Object.getOwnPropertyNames(modified).length != 0) {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();
            var sequence = originalEvent.store.associatedEntity
            for (var field in modified) {
                if (field != '_category_code' && field != '_priority_code') {
                    originalEvent.set(field, theEvent.get(field));
                    sequence._dirty_edit = true;
                }
            }
            CMDBuildUI.util.helper.FormHelper.endSavingForm();
        }
    }
});
