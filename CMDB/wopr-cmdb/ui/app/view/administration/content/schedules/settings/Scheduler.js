Ext.define('CMDBuildUI.view.administration.content.schedules.settings.Scheduler', {
    extend: 'Ext.tab.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.schedules.settings.SchedulerController',
        'CMDBuildUI.view.administration.content.schedules.settings.SchedulerModel'
    ],

    alias: 'widget.administration-content-schedules-settings-scheduler',
    controller: 'administration-content-schedules-settings-scheduler',
    viewModel: {
        type: 'administration-content-schedules-settings-scheduler'
    },
    items: [],

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
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(this,
            "generalproperties",
            CMDBuildUI.locales.Locales.administration.classes.properties.title,
            [{
                xtype: 'administration-content-schedules-settings-tabs-generalproperties-form'
            }],
            0, {
                disabled: '{disabledTabs.generalproperties}'
            });
        tabPanelHelper.addTab(this,
            "generalpropertiesaa",
            CMDBuildUI.locales.Locales.administration.classes.properties.title,
            [{

                layout: 'column',
                items: [{
                    columnWidth: 0.5,
                    items: [{
                        /********************* org.cmdbuild.scheduler.enabled **********************/
                        xtype: 'checkbox',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                        },
                        name: 'isEnabled',
                        bind: {
                            value: '{theSetup.org__DOT__cmdbuild__DOT__scheduler__DOT__enabled}',
                            readOnly: '{actions.view}'
                        }
                    }]
                }]

            }],
            1, {
                disabled: '{disabledTabs.generalproperties}'
            });





    }

});