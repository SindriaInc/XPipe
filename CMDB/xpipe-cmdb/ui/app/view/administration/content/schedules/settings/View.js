Ext.define('CMDBuildUI.view.administration.content.schedules.settings.View', {
    extend: 'Ext.tab.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.schedules.settings.ViewController',
        'CMDBuildUI.view.administration.content.schedules.settings.ViewModel'
    ],
    alias: 'widget.administration-content-schedules-settings-view',
    controller: 'administration-content-schedules-settings-view',
    viewModel: {
        type: 'administration-content-schedules-settings-view'
    },

    tabPosition: 'top',
    tabRotation: 0,
    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',
    scrollable: true,
    forceFit: true,
    layout: 'fit',

    bind: {
        activeTab: '{activeTab}'
    },

    initComponent: function () {
        this.callParent(arguments);
        this.up('administration-content').getViewModel().set('title', Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.administration.navigation.schedules, CMDBuildUI.locales.Locales.administration.navigation.settings));
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(this,
            "generalproperties",
            CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            [{
                xtype: 'administration-content-schedules-settings-tabs-generalproperties-form'
            }],
            0, {
                disabled: '{disabledTabs.generalproperties}'
            });

        tabPanelHelper.addTab(this, "statuses", CMDBuildUI.locales.Locales.administration.schedules.statuses, [{
            xtype: 'administration-content-lookuptypes-tabitems-values-values',
            objectTypeName: CMDBuildUI.util.Utilities.stringToHex('CalendarEventStatus'),

            viewModel: {
                links: {
                    theLookupType: {
                        type: 'CMDBuildUI.model.lookups.LookupType',
                        id: CMDBuildUI.util.Utilities.stringToHex('CalendarEventStatus')
                    }
                }
            }
        }], 1, {
            disabled: '{disabledTabs.statuses}'
        });

        tabPanelHelper.addTab(this, "categories", CMDBuildUI.locales.Locales.administration.schedules.categories, [{
            xtype: 'administration-content-lookuptypes-tabitems-values-values',
            objectTypeName: CMDBuildUI.util.Utilities.stringToHex('CalendarCategory'),

            viewModel: {
                links: {
                    theLookupType: {
                        type: 'CMDBuildUI.model.lookups.LookupType',
                        id: CMDBuildUI.util.Utilities.stringToHex('CalendarCategory')
                    }
                }
            }
        }], 2, {
            disabled: '{disabledTabs.categories}'
        });

        tabPanelHelper.addTab(this, "priorities", CMDBuildUI.locales.Locales.administration.schedules.priorities, [{
            xtype: 'administration-content-lookuptypes-tabitems-values-values',
            objectTypeName: CMDBuildUI.util.Utilities.stringToHex('CalendarPriority'),

            viewModel: {
                links: {
                    theLookupType: {
                        type: 'CMDBuildUI.model.lookups.LookupType',
                        id: CMDBuildUI.util.Utilities.stringToHex('CalendarPriority')
                    }
                }
            }
        }], 3, {
            disabled: '{disabledTabs.priorities}'
        });
        tabPanelHelper.addTab(this, "manualschedules", CMDBuildUI.locales.Locales.administration.schedules.manualschedules, [{
            xtype: 'administration-content-schedules-settings-tabs-manualschedules-form'            
        }], 4, {
            disabled: '{disabledTabs.manualschedules}'
        });

    }
});