Ext.define('CMDBuildUI.view.map.tab.tabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-tabpanel',

    control: {

        '#map-legend': {
            disable: 'onLegendDisable',
            enable: 'onLegendEnable'
        }
    },

    /**
     * @param tabPanel, 
     * @param newCard, 
     * @param oldCard, 
     * @param eOpts
     */
    onTabChange: function (tabPanel, newCard, oldCard, eOpts) {
        var n = tabPanel.items.findIndex('id', newCard.id);
        this.getViewModel().getParent().getParent().getParent().set('activeMapTabPanel', n);
    },

    /**
     * 
     * @param {Ext.Componend} legendTab 
     * @param {eOpts} eOpts 
     */
    onLegendDisable: function (legendTab, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();

        //if the active tab get's disabled change the active one
        if (view.getActiveTab().getId() == legendTab.getId()) {
            view.setActiveTab(0);
        }

        vm.set('checkchange.check', true);
        legendTab.tab.hide();
    },

    /**
     * 
     * @param {Ext.Component} legendTab 
     * @param {eOpts} eOpts 
     */
    onLegendEnable: function (legendTab, eOpts) {
        legendTab.tab.show();
        this.getView().setActiveTab(legendTab);
    }
});