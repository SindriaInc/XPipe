Ext.define('CMDBuildUI.view.administration.content.gis.thematisms.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gis-thematisms-grid',

    control: {
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Object} event 
     * @param {Object} eOpts 
     */
    onEditBtnClick: function (button, event, eOpts) {
        var vm = button.lookupViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Object} event 
     * @param {Object} eOpts 
     */
    onSaveBtnClick: function (button, event, eOpts) {
        var vm = button.lookupViewModel();        
        vm.getStore('thematismsStore').sync({
            /**
             * 
             * @param {Ext.data.Batch} operations 
             * @param {Object} options 
             */
            callback: function (operations, options) {
                // TODO: refresh grid?
                // server currently does not save global value. issue #
            }
        });
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**     
     * @param {Ext.button.Button} button 
     * @param {Object} event 
     * @param {Object} eOpts 
     */
    onCancelBtnClick: function (button, event, eOpts) {
        var vm = button.lookupViewModel();
        vm.getStore('thematismsStore').rejectChanges();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        var searchValue = field.getValue();
        var store = vm.get("thematismsStore");
        if (searchValue) {
            var filter = {
                "query": searchValue
            };
            store.getProxy().setExtraParam('filter', Ext.JSON.encode(filter));
            store.load();
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // clear store filter
        var store = vm.get("thematismsStore");
        store.getProxy().setExtraParam('filter', Ext.JSON.encode([]));
        store.load();
        // reset input
        field.reset();
    }
});