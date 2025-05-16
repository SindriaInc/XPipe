
Ext.define('CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoattributesGrid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoattributesGridController',
        'CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoattributesGridModel'
    ],

    controller: 'map-tab-cards-geoattributesgrid-geoattributesgrid',
    viewModel: {
        type: 'map-tab-cards-geoattributesgrid-geoattributesgrid'
    },

    mixins: [
        'CMDBuildUI.view.map.Mixin'
    ],

    alias: 'widget.map-tab-cards-geoattributesgrid-geoattributesgrid',

    reference: 'map-geoattributes-grid',

    /**
     * source is set in the viewModel `storeSourceCalculation`
     */
    config: {
        theObject: undefined
    },

    store: null,
    publishes: [
        'theObject'
    ],

    disableSelection: true,

    columns: [{
        menudDisabled: true,
        align: 'left',
        dataIndex: 'text',
        flex: 1
    }, {
        menuDisabled: true,
        xtype: 'actioncolumn',
        items: [{
            glyph: 'f055@FontAwesome', //plus icon
            tooltip: CMDBuildUI.locales.Locales.common.actions.add,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.actions.add'
            },
            iconCls: 'margin-left', //5 px margin
            handler: 'onAddGeoValue',
            isDisabled: function (tableview, rowindex, colindex, item, record) {
                switch (record.get('_type')) {
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.shape:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.point:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.polygon:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.linestring:
                        var canupdate = record.get('_can_write');
                        if (canupdate && !record.hasValues()) {
                            return false;
                        }
                        return true;
                }
            }
        }, {
            glyph: 'xf040@FontAwesome', //pencil icon
            tooltip: CMDBuildUI.locales.Locales.common.actions.edit,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.actions.edit'
            },
            iconCls: 'margin-left', //5 px margin
            handler: 'onEditGeoValue',
            isDisabled: function (tableview, rowindex, colindex, item, record) {
                switch (record.get('_type')) {
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.shape:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.point:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.polygon:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.linestring:
                        var canupdate = record.get('_can_write');
                        if (canupdate && record.hasValues()) {
                            return false;
                        }
                        return true;
                }
            }
        }, {
            glyph: 'f057@FontAwesome', //X icon
            tooltip: CMDBuildUI.locales.Locales.common.actions.remove,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.actions.remove'
            },
            iconCls: 'margin-left', //5 px margin
            handler: 'onRemoveGeoValue',
            isDisabled: function (tableview, rowindex, colindex, item, record) {
                switch (record.get('_type')) {
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.shape:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.point:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.polygon:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.linestring:
                        var canupdate = record.get('_can_write');
                        if (canupdate && record.hasValues()) {
                            return false;
                        }
                        return true;
                }
            }
        }, {
            glyph: 'f05b@FontAwesome', //crosshairs icon
            tooltip: CMDBuildUI.locales.Locales.gis.view,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.gis.view'
            },
            iconCls: 'margin-left', //5 px margin
            handler: 'onViewGeoValue',
            isDisabled: function (tableview, rowindex, colindex, item, record) {
                switch (record.get('_type')) {
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.shape:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.point:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.polygon:
                    case CMDBuildUI.model.gis.GeoAttribute.subtype.linestring:
                        if (record.hasValues()) {
                            return false;
                        }
                        return true;
                }
            }
        }]
    }],

    buttons: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.common.actions.save,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        id: 'saveButton',
        ui: 'management-action',
        reference: 'saveButton',
        handler: 'onSaveButtonClick',
        bind: {
            disabled: '{buttonsSaveCancel.saveDisabled}',
            hidden: '{buttonsSaveCancel.hidden}'
        },
        margin: '0px 5px 0px 0px'
    }, {
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        ui: 'secondary-action',
        id: 'cancelButton',
        itemId: 'cancelButton',
        reference: 'cancelButton',
        handler: 'onCancelButtonClick',
        disabled: false,
        bind: {
            hidden: '{buttonsSaveCancel.hidden}'
        }
    }],

    _filterObjectTypeName: 'objectTypeName'
});
