Ext.define('CMDBuildUI.view.emails.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.emails-container',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            show: 'onShow'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.emails.Container} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var me = this,
            vm = view.lookupViewModel(),
            parentTabPanel = view.up("tabpanel"),
            object = parentTabPanel.getFormObject();

        if (object) {
            initGrid(object, parentTabPanel.getFormMode());
        } else {
            var fn;
            switch (vm.get("objectType")) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    fn = CMDBuildUI.util.api.Client.getRemoteCard;
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                    fn = CMDBuildUI.util.api.Client.getRemoteProcessInstance;
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar:
                    vm.bind({
                        bindTo: "{events-tabpanel.eventId}",
                        single: true
                    }, function () {
                        me.onBeforeRender(view, eOpts);
                    });
                    break;
            }
            if (fn) {
                fn(vm.get("objectTypeName"), vm.get("objectId")).then(function (record) {
                    initGrid(record, CMDBuildUI.util.helper.FormHelper.formmodes.read);
                });
            }
        }

        /**
         * 
         * @param {Ext.data.Model} target 
         * @param {String} formMode 
         */
        function initGrid(target, formMode) {
            vm.set("theTarget", target);
            vm.set("emails", target.emails());
            view.add({
                xtype: 'emails-grid',
                formMode: formMode,
                readOnly: view.getReadOnly()
            });
        }

    },

    /**
     * 
     * @param {CMDBuildUI.view.emails.Container} view 
     * @param {Object} eOpts 
     */
    onShow: function (view, eOpts) {
        var obj = view.lookupViewModel().get("theTarget");
        if (obj) {
            obj.loadTemplates().then(function (templates) {
                obj.updateObjEmailsFromTemplates();
            });
        }
    }

});