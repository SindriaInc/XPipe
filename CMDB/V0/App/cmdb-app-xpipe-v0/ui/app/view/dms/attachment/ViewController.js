Ext.define('CMDBuildUI.view.dms.attachment.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-attachment-view',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = this.getViewModel();

        vm.bind({
            DMSClass: '{dms-attachment-view.DMSClass}',
            DMSModelClass: '{dms-attachment-view.DMSModelClass}'
        }, this.itemsUpdate, this);
    },

    itemsUpdate: function (data) {

        var me = this;

        if (data.DMSModelClass && data.DMSClass) {
            CMDBuildUI.util.helper.FormHelper.renderFormForType(CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                data.DMSClass.getId(), {
                mode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
                linkName: 'dms-attachment-view.theObject',
                showAsFieldsets: true,
                readonly: true
            }).then(function (items) {

                var view = me.getView();
                var formView = view.down('#formpanel');

                formView.removeAll(true);
                formView.add(view.getFormItems(items));

            })
        }
    }
});
