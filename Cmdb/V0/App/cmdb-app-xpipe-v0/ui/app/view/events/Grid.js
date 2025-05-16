
Ext.define('CMDBuildUI.view.events.Grid', {
    extend: 'Ext.grid.Panel',
    mixins: [
        'CMDBuildUI.mixins.grids.Grid'
    ],
    requires: [
        'CMDBuildUI.view.events.GridController',
        'CMDBuildUI.view.events.GridModel',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],
    alias: 'widget.events-grid',
    controller: 'events-grid',
    viewModel: {
        type: 'events-grid'
    },
    rowViewModel: {
        type: 'events-tabpanel'
    },

    plugins: [
        'gridfilters', {
            pluginId: 'forminrowwidget',
            ptype: 'forminrowwidget',
            // id: 'forminrowwidget',
            expandOnDblClick: true,
            removeWidgetOnCollapse: true,
            widget:
                CMDBuildUI.util.helper.GridHelper.getFormInRowWidget(
                    CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar, {
                    showInPopup: false,
                    formmode: 'read',
                    readOnly: true,
                    bind: {
                        eventId: '{events-grid.selectedId}'
                    },
                    padding: '0 10 8 0'
                })
        }
    ],

    selModel: {
        pruneRemoved: false, // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
        selType: 'checkboxmodel',
        checkOnly: true,
        mode: 'SINGLE'
    },

    // ----- //
    /**
     * this tree configurations are needed to publish on the view model the variable events-grid.eventsdata
     */
    config: {
        eventsStore: {
            $value: null,
            evented: true
        },

        /**
         * @cfg {Boolean} maingrid
         * 
         * Set to true when the grid is added in main content.
         */
        maingrid: false,

        /**
         * @cfg {Boolean}
         * Configuration used for the plugin
         */
        hideTools: false,

        /**
         * The id of the selected record
         */
        selectedId: {
            lazy: true,
            $value: undefined
        }
    },
    publishes: [
        'eventsStore',
        'selectedId'
    ],

    twoWayBindable: [
        'selectedId'
    ],

    reference: 'events-grid',
    // ----- //

    viewConfig: {
        markDirty: false
    },

    /**
     * the store is binded to variable events-grid.eventsdata
     */
    bind: {
        store: '{events-grid.eventsStore}'
    },

    forceFit: true,

    /**
     * Return true if the grid has been added in main container.
     * @return {Boolean}
     */
    isMainGrid: function () {
        return this.maingrid;
    },

    /**
     * 
     */
    initComponent: function () {
        var p = this.findPlugin('forminrowwidget');

        p.widget.hideTools = this.config.hideTools;
        var eventsStore = this.getEventsStore();
        if (eventsStore && !eventsStore.isStore && Ext.isObject(eventsStore)) {
            var eventsStoreObject = eventsStore;
            eventsStore = Ext.create('Ext.data.BufferedStore', eventsStoreObject);
            this.setEventsStore(eventsStore);
        }
        this.callParent(arguments);
    },

    applySelectedId: function (value, oldvalue) {
        var eventsStore = this.getEventsStore();
        if (eventsStore) {

            if (value) {
                if (eventsStore.getById(value)) {
                    return value;
                } else {
                    this.deferredApplySelectedId(value);
                }
            } else {
                return value;
            }
        } else {
            this.addListener('eventsstorechange', this.setSelectedId, this, {
                args: [value]
            });
        }
    },

    updateSelectedId: function (value, oldvalue) {
        var record = null;
        var oldRecord;
        var plugin = this.findPlugin('forminrowwidget');
        if (value) {
            record = this.getEventsStore().findRecord('_id', value);
            if (!plugin.recordsExpanded[record.internalId]) {
                plugin.toggleRow(-1, record);
            }
        } else {

            if (oldvalue) {
                oldRecord = this.getEventsStore().findRecord('_id', oldvalue);
                if (oldRecord && plugin.recordsExpanded[oldRecord.internalId]) {
                    plugin.toggleRow(-1, oldRecord);
                }
            }
        }
    },

    deferredApplySelectedId: function (value) {
        var eventsStore = this.getEventsStore();
        this.relayEvents(eventsStore, ['load'], 'events-grid-');

        function callback() {
            this.setSelectedId(value);
        }
        if (this.hasListeners['events-grid-load']) {
            this.clearListeners('events-grid-load', callback);
        }

        this.addListener('events-grid-load', callback, this, {
            single: true
        });
    }
});
