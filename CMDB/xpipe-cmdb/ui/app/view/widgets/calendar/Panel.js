
Ext.define('CMDBuildUI.view.widgets.calendar.Panel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.widgets.calendar.PanelController'
    ],

    mixins: [
        'CMDBuildUI.view.widgets.Mixin'
    ],

    alias: 'widget.widgets-calendar-panel',
    controller: 'widgets-calendar-panel',

    layout: "fit"

    /**
     * @cfg {String} theWidget.ClassName 
     * Class name or process name to get data.
     */

    /**
     * @cfg {String} theWidget.Filter 
     * The filter to apply to the list request.
     */

    /**
     * @cfg {String} theWidget.EventStartDate
     * The field to use as event start date. 
     */

    /**
     * @cfg {String} theWidget.EventEndDate
     * The field to use as event end date. 
     */

    /**
     * @cfg {String} theWidget.EventTitle
     * The field to use as event title. 
     */

    /**
     * @cfg {String} theWidget.EventType
     * The field to use as event type. 
     */

    /**
     * @cfg {String} theWidget.EventTypeLookup
     * The LookUp type to use as event type info. 
     */

    /**
     * @cfg {String} theWidget.DefaultDate
     * The field in current class/process to use as opening date in calendar. 
     */
});