Ext.define('CMDBuildUI.view.fields.schedulerdate.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-schedulerdate-create',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
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
        //hides the save and close button
        var saveCloseButton = view.down('#saveandclosebtn');
        saveCloseButton.setHidden(true);

        //generates the form
        var vm = view.getViewModel();
        vm.bind('{events-event-create.theEvent}', view.generateForm, view);

        //binds 'begin' and 'end' to the value of 'date' field
        vm.bind('{events-event-create.theEvent.date}', function (dateChange) {
            var theEvent = this.getTheEvent()
            theEvent.set('begin', dateChange)
        }, view);
    },

    /**
     * Save and close button click
     * 
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var theEvent = this.getView().getTheEvent();
        this.getView().fireEventArgs('popupsave', [theEvent]);
    },

    /**
     * Save and close button click
     * 
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getView().fireEventArgs('popupclose');
    }

});
