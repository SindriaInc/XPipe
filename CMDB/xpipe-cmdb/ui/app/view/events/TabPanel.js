
Ext.define('CMDBuildUI.view.events.TabPanel', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    alias: 'widget.events-tabpanel',

    requires: [
        'CMDBuildUI.view.events.TabPanelController',
        'CMDBuildUI.view.events.TabPanelModel'
    ],

    mixins: [
        'CMDBuildUI.mixins.DetailsTabPanel'
    ],

    controller: 'events-tabpanel',
    viewModel: {
        type: 'events-tabpanel'
    },

    bind: {
        activeItem: '{action}'
    },

    config: {
        eventId: undefined,
        showInPopup: false
    },
    publishes: 'eventId',
    reference: 'events-tabpanel',

    ui: 'management',
    border: false,
    tabPosition: 'left',
    tabRotation: 0,
    header: false, //very important configuration

    defaults: {
        textAlign: 'left',
        bodyPadding: 10,
        scrollable: true,
        border: false
    },
    layout: 'fit',

    //Those configurations are used in DetailsTabPanel mixin
    //are valorized in the controller

    /**
     * 
     */
    _objectLinkName: undefined,

    /**
     * is defined in the controller
     */
    _objectFormReference: undefined,

    tabtools: CMDBuildUI.view.events.Util.getTools()
});
