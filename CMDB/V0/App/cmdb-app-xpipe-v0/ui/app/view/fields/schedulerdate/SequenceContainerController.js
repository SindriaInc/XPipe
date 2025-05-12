Ext.define('CMDBuildUI.view.fields.schedulerdate.SequenceContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-schedulerdate-sequencecontainer',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        'fields-schedulerdate-sequencecontainer #savebutton': {
            click: 'onSaveButtonClick'
        },
        'fields-schedulerdate-sequencecontainer #cancelbutton': {
            click: 'onCancelButtonClick'
        }
    },

    onBeforeRender: function () {
        // move this in the schedulerDateField
        this.getViewModel().bind('{sequences}', this.onSequencesReady)
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onSaveButtonClick: function () {
        this.getView().fireEvent('popupsave', this.getView());
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onCancelButtonClick: function (button, eOpts) {
        this.getView().fireEvent('popupclose', this.getView());
    },

    //move this in the schedulerDateField
    onSequencesReady: function (sequences) {
        var view = this.getView();
        var items = [];

        sequences.getRange().forEach(function (sequence) {
            var trigger = sequence.triggerRecord;
            delete sequence.triggerRecord;

            items.push({
                xtype: 'fields-schedulerdate-sequencefieldset',
                viewModel: {
                    data: {
                        theSequence: sequence,
                        theTrigger: trigger
                    }
                }
            })
        });

        view.add(items);
    }
});
