
Ext.define('CMDBuildUI.view.management.navigation.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.management.navigation.ContainerController',
        'CMDBuildUI.view.management.navigation.ContainerModel',

        'CMDBuildUI.view.management.navigation.Tree'
    ],

    xtype: 'management-navigation-container',
    controller: 'management-navigation-container',
    viewModel: {
        type: 'management-navigation-container'
    },

    title: CMDBuildUI.locales.Locales.main.navigation,
    layout: 'container',
    width: 250,
    scrollable: true,
    collapseFirst: false,
    cls: Ext.baseCSSPrefix + 'panel-bold-header',

    bind: {
        title: '{navTitle}'
    },

    tools: [{
        type: 'search',
        tooltip: 'Search',
        handler: function (event, toolEl, panelHeader) {
            var vm = this.lookupViewModel();
            vm.set('showsearchfield', !vm.get('showsearchfield'));
        }
    }],

    items: [{
        xtype: 'management-navigation-tree'
    }],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        hidden: true,
        itemId: 'navTopBar',
        bind: {
            hidden: '{!showsearchfield}'
        },
        items: [{
            xtype: 'groupedcombo',
            itemId: 'navSearchField',
            flex: 1,

            displayField: 'text',
            hideLabel: true,
            hideTrigger: true,
            anyMatch: true,
            queryMode: 'local',
            queryDelay: 250,

            emptyText: CMDBuildUI.locales.Locales.common.actions.searchtext,
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.common.actions.searchtext'
            },

            listConfig: {
                emptyText: CMDBuildUI.locales.Locales.errors.notfound,
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.errors.notfound'
                }
            },

            bind: {
                store: '{menusearch}'
            }
        }]
    }, {
        xtype: 'management-chat-conversationslist',
        dock: 'bottom'
    }],

    autoEl: {
        'data-testid': 'management-navigation-container'
    },

    /**
     * Open CMDBuild resource by node.
     *
     * @param {CMDBuildUI.model.menu.MenuItem} node
     */
    openResourceByNode: function (node) {
        var menutype = node.get("menutype"),
            navtree = false,
            url;

        switch (menutype) {
            case CMDBuildUI.model.menu.MenuItem.types.folder:
            case CMDBuildUI.model.menu.MenuItem.types.favourites:
                // do nothing
                break;
            case CMDBuildUI.model.menu.MenuItem.types.klass:
                url = CMDBuildUI.util.Navigation.getClassBaseUrl(node.get("objecttypename"), null, null, true);
                break;
            case CMDBuildUI.model.menu.MenuItem.types.process:
                url = CMDBuildUI.util.Navigation.getProcessBaseUrl(node.get("objecttypename"), null, null, null, true);
                break;
            case CMDBuildUI.model.menu.MenuItem.types.custompage:
                url = 'custompages/' + node.get("objecttypename");
                break;
            case CMDBuildUI.model.menu.MenuItem.types.report:
                url = 'reports/' + node.get("objecttypename");
                break;
            case CMDBuildUI.model.menu.MenuItem.types.reportpdf:
                url = 'reports/' + node.get("objecttypename") + '/pdf';
                break;
            case CMDBuildUI.model.menu.MenuItem.types.reportcsv:
                url = 'reports/' + node.get("objecttypename") + '/csv';
                break;
            case CMDBuildUI.model.menu.MenuItem.types.view:
                url = 'views/' + node.get("objecttypename") + '/items';
                break;
            case CMDBuildUI.model.menu.MenuItem.types.dashboard:
                url = 'dashboards/' + node.get("objecttypename");
                break;
            case CMDBuildUI.model.menu.MenuItem.types.calendar:
                url = 'events';
                break;
            case CMDBuildUI.model.menu.MenuItem.types.navtree:
            case CMDBuildUI.model.menu.MenuItem.types.navtreeitem:
                url = 'navigation/' + node.get("objecttypename");
                navtree = true;
                break;
            default:
                CMDBuildUI.util.Msg.alert('Warning', 'Menu type not implemented!');
        }

        if (url !== undefined) {
            CMDBuildUI.util.Navigation.clearCurrentContext();
            CMDBuildUI.util.Navigation.updateCurrentRowTab();
            CMDBuildUI.util.Utilities.redirectTo(url, true);
            if (navtree) {
                Ext.asap(function () {
                    Ext.GlobalEvents.fireEventArgs("menunavtreeitemchanged", [node]);
                });
            }
        }
    }
});