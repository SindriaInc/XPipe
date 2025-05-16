Ext.define('CMDBuildUI.view.administration.content.domains.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-tabpanel',
    requires: [
        'CMDBuildUI.util.administration.helper.TabPanelHelper'
    ],
    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.domains.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        // set view model variables
        var vm = this.getViewModel();

        var currentTabIndex = view.up('administration-content').getViewModel().get('activeTabs.domains') || 0;
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;

        tabPanelHelper.addTab(view, "properties", CMDBuildUI.locales.Locales.administration.domains.texts.properties, [{
            xtype: 'administration-content-domains-tabitems-properties-properties',
            objectTypeName: vm.get("objectTypeName"),
            objectId: vm.get("objectId"),
            autoScroll: true
        }], 0);

        tabPanelHelper.addTab(view, "attributes", CMDBuildUI.locales.Locales.administration.attributes.attributes, [{
            xtype: 'administration-content-domains-tabitems-attributes-attributes',
            objectTypeName: vm.get("objectTypeName"),
            objectId: vm.get("objectId")
        }], 1, { disabled: '{!actions.view}' });

        tabPanelHelper.addTab(view, "enabledClasses", CMDBuildUI.locales.Locales.administration.domains.texts.enabledclasses, [{
            xtype: 'administration-content-domains-tabitems-domains-classes'
        }], 2, { disabled: '{!actions.view}' });

        tabPanelHelper.addTab(view, "import_export", CMDBuildUI.locales.Locales.administration.importexport.texts.importexportfile, [{
            xtype: 'administration-content-importexport-datatemplates-view',
            viewModel: {
                data: {
                    targetName: vm.get("objectTypeName")
                }
            }
        }], 3, {
                disabled: '{!actions.view}'
            });

        vm.set("activeTab", currentTabIndex);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.domains.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.domains', this, view, newtab, oldtab, eOpts);
    },

    onItemCreated: function (record, eOpts) {
        // TODO: reload menu tree store
    },

    /**
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {Object} eOpts
     */
    onItemUpdated: function (record, eOpts) {

        Ext.ComponentQuery.query('domains-cards-grid-grid')[0].fireEventArgs('reload', [record, 'update']);
        this.redirectTo('domains/' + record.getRecordType() + '/cards/' + record.getRecordId(), true);
    },

    /**
     * @param {Object} eOpts
     */
    onCancelCreation: function (eOpts) {

        var detailsWindow = Ext.getCmp('CMDBuildManagementDetailsWindow');
        detailsWindow.fireEvent('closed');
    },

    /**
     * @param {Object} eOpts
     */
    onCancelUpdating: function (eOpts) {

        var detailsWindow = Ext.getCmp('CMDBuildManagementDetailsWindow');
        detailsWindow.fireEvent('closed');
    }
});
