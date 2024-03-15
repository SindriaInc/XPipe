Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.layers.LayersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabitems-layers-layers',
    control: {

        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onEditBtnClick: function (button, event, eOpts) {
        this.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);

        var vm = button.up('administration-content-classes-view').getViewModel();
        vm.toggleEnableTabs(5);       

    },

    onSaveBtnClick: function (button, event, eOpts) {
        button.setDisabled(true);
        var me = this;
        var vm = button.lookupViewModel();
        var currentClassName = vm.get('objectTypeName');
        var layers = this.getView().getStore().getData().getRange();
        var visibles = [];
        Ext.Array.forEach(layers, function (element, index) {
            if (element.get('visibility').indexOf(currentClassName) !== -1) {
                visibles.push(element.get('_id'));
            }
        });
        /**
         * save configuration via custom ajax call
         */
        Ext.Ajax.request({
            url: Ext.String.format('{0}/classes/{1}/geoattributes/visibility', CMDBuildUI.util.Config.baseUrl, currentClassName),
            method: 'POST',
            jsonData: visibles,
            success: function (transport) {
                me.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                var vmParent = vm.getParent();
                vmParent.configDisabledTabs();
            },
            callback: function (reason) {
                if (button.el.dom) {
                    button.setDisabled(false);
                }
            }
        });
    },
    onCancelBtnClick: function (button, event, eOpts) {
        this.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        var vm = button.lookupViewModel().getParent();
        vm.configDisabledTabs();
    },

    privates: {
        setFormMode: function (mode) {
            var vm = this.getViewModel();
            vm.set('actions.edit', mode === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
            vm.set('actions.view', mode === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        }
    }

});