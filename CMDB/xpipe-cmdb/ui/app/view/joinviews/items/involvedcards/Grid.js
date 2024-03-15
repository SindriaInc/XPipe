
Ext.define('CMDBuildUI.view.joinviews.items.involvedcards.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.joinviews.items.involvedcards.GridController'
    ],

    alias: 'widget.joinviews-items-involvedcards-grid',
    controller: 'joinviews-items-involvedcards-grid',

    forceFit: true,

    bind: {
        store: '{involvedCards}'
    },

    columns: [{
        text: CMDBuildUI.locales.Locales.relations.type,
        dataIndex: 'typeName',
        align: 'left',
        renderer: function (value) {
            return CMDBuildUI.util.helper.FieldsHelper.renderTextField(value)
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.type'
        }
    }, {
        text: CMDBuildUI.locales.Locales.relations.code,
        dataIndex: 'code',
        align: 'left',
        renderer: function (value) {
            return CMDBuildUI.util.helper.FieldsHelper.renderTextField(value)
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.code'
        }
    }, {
        text: CMDBuildUI.locales.Locales.relations.description,
        dataIndex: 'description',
        align: 'left',
        renderer: function (value) {
            return CMDBuildUI.util.helper.FieldsHelper.renderTextField(value)
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.description'
        }
    }, {
        xtype: 'actioncolumn',
        minWidth: 60,
        hideable: false,
        items: [{
            iconCls: 'relations-grid-action x-fa fa-external-link',
            tooltip: CMDBuildUI.locales.Locales.relations.opencard,
            handler: function (grid, rowIndex, colIndex, item, eOpts, record) {
                grid.fireEvent("actionopencard", record);
            },
            localized: {
                toolitp: 'CMDBuildUI.locales.Locales.relations.opencard'
            }
        }]
    }]

});
