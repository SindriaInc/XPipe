Ext.define('CMDBuildUI.view.administration.content.lookuptypes.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabpanel',

    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.lookuptypes.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {

        var vm = this.getViewModel();
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(view, "properties", CMDBuildUI.locales.Locales.administration.lookuptypes.strings.lookuplist, [{
            xtype: 'administration-content-lookuptypes-tabitems-type-properties',
            objectTypeName: vm.get("objectTypeName"),
            objectId: vm.get("objectId"),
            autoScroll: true
        }], 0, { disabled: '{disabledTabs.list}' });

        tabPanelHelper.addTab(view, "attributes", CMDBuildUI.locales.Locales.administration.lookuptypes.strings.values, [{
            xtype: 'administration-content-lookuptypes-tabitems-values-values',
            objectTypeName: vm.get("objectTypeName"),
            objectId: vm.get("objectId"),

            parentLookupsStore: vm.get("parentLookupsStore")

        }], 1, { disabled: '{disabledTabs.values}' });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.lookuptypes', this, view, newtab, oldtab, eOpts);
    }
});