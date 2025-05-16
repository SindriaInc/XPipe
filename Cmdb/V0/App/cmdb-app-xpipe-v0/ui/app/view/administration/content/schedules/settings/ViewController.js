Ext.define('CMDBuildUI.view.administration.content.schedules.settings.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-schedules-settings-view',
    control: {
        '#': {            
            tabchange: 'onTabChage',
            enabletabs: 'onEnableTabs',
            disabletabs: 'onDisableTabs'
        }
    },    
    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.dmssettings', this, view, newtab, oldtab, eOpts);
    },

    onDisableTabs: function () {
        var view = this.getView();
        var activeTab = view.getActiveTab();
        view.items.each(function (tab) {
            if (tab.reference !== activeTab.reference) {
                tab.setDisabled(true);
            }
        });
    },

    onEnableTabs: function () {
        var view = this.getView();
        view.items.each(function (tab) {
            tab.setDisabled(false);
        });
    }

});