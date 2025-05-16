Ext.define('CMDBuildUI.view.administration.content.bus.messages.viewinrow.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bus-messages-viewinrow-viewinrow',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#retryBtn': {
            click: 'onRetryBtnClick'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        vm.linkTo("theMessage", {
            type: 'CMDBuildUI.model.administration.BusLog',
            id: view.up('grid').getSelection()[0].get('_id')
        });
    },

    onRetryBtnClick: function (view) {

        var theMessage = this.getViewModel().get('theMessage');
        Ext.Ajax.request({
            url: Ext.String.format("{0}/{1}/retry", theMessage.getProxy().getUrl(), theMessage.getId()),
            method: 'POST',
            success: function (response) {
                var res = JSON.parse(response.responseText);
                if (res.success) {
                    view.up('grid').getView().refresh();
                } else {
                    // TODO: show server message
                    // 
                }
            }
        });
    }
});