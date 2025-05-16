Ext.define('CMDBuildUI.view.events.notes.NotesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-notes-notes',
    control: {
        '#editbtn': {
            click: 'onEditBtnClick'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {Ext.button.Button} button Edit button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, event, eOpts) {
        this.getViewModel().set("editmode", true);
    },

    /**
     * @param {Ext.button.Button} button Save button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var view = this.getView(),
            vm = this.getViewModel(),
            theEvent = view.getTheEvent(),
            theEventClone = theEvent.clone(),
            cancelBtn = view.down("#cancelbtn");

        theEventClone.reject();
        theEventClone.set('notes', theEvent.get('notes'));

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, cancelBtn]);

        theEventClone.save({
            success: function (record, operation) {
                //delete theEventClone; //FIXME: view if can remove the clone in this way
                vm.set('tabcounters.notes', CMDBuildUI.util.Utilities.extractTextFromHTML(theEvent.get('notes') || null));
                theEventClone.destroy();
                vm.set('editmode', false);
            },
            callback: function (record, operation, success) {
                CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        });
    },

    /**
     * @param {Ext.button.Button} button Cancel button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        var theEvent = this.getView().getTheEvent();

        //If the record has the notes field modified
        if (!Ext.isEmpty(theEvent.getModified('notes'), true)) {
            //sets the old value;
            theEvent.set('notes', theEvent.getModified('notes'));
        }

        this.getViewModel().set("editmode", false);
    }

});
