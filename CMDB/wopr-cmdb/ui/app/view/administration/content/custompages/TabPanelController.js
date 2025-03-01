Ext.define('CMDBuildUI.view.administration.content.custompages.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-custompages-tabpanel',
    requires: [
        'CMDBuildUI.util.administration.helper.TabPanelHelper'
    ],

    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChage'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.custompages.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();

        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(view,
            "properties",
            CMDBuildUI.locales.Locales.administration.classes.properties.title,
            [{
                xtype: 'administration-content-custompages-view'
            }],
            0, {
            disabled: '{disabledTabs.properties}',
            hidden: '{hideForm}'
        });

        tabPanelHelper.addTab(view, "permissions", CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.permissions, [{
            xtype: 'administration-content-custompages-permissionstab'
        }], 1, {
            disabled: '{!theSession.rolePrivileges.admin_roles_view || disabledTabs.permissions}',
            hidden: '{hideForm}'
        });
        vm.set('activeTab', vm.get('activeTabs.custompages'));

    },

    /**
     * @param {CMDBuildUI.view.administration.content.custompages.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.custompages', this, view, newtab, oldtab, eOpts);
    },


    /**
      * On add custompage button click
      * @param {Ext.button.Button} button
      * @param {Event} e
      * @param {Object} eOpts
      */
    onAddBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomPageUrl(null, true);
        this.redirectTo(nextUrl);
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
        var searchValue = vm.getData().search.value;

        var allDashboardStore = vm.get("allCustompages");
        if (searchValue) {
            var filter = {
                "query": searchValue
            };
            allDashboardStore.getProxy().setExtraParam('filter', Ext.JSON.encode(filter));
            allDashboardStore.load();
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
        // var allDashboardStore = vm.get("allDashboards");
        // allDashboardStore.getProxy().setExtraParam('filter', Ext.JSON.encode([]));
        // allDashboardStore.load();
        // reset input
        field.reset();
    }
});