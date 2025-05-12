Ext.define('CMDBuildUI.view.bim.xeokit.Canvas', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.bim.xeokit.CanvasController'
    ],

    mixins: [
        'CMDBuildUI.view.bim.xeokit.Mixin'
    ],

    alias: 'widget.bim-xeokit-canvas',
    controller: 'bim-xeokit-canvas',

    html: '<canvas style="position:absolute;width: 100%;height: 100%" id="bimCanvas"></canvas><canvas style="position:absolute;width:150px;height:150px;bottom:50px;right:10px;z-index:200000;" id="bimNavCubeCanvas"></canvas>',

    fbar: [{
        xtype: 'tool',
        width: 32,
        height: 32,
        cls: 'management-tool bim-xeokit-tool',
        iconCls: Ext.baseCSSPrefix + 'fa fa-home',
        tooltip: CMDBuildUI.locales.Locales.bim.menu.resetView,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.bim.menu.resetView'
        },
        callback: function (owner, tool, event) {
            owner.up().fireEvent("resetView", tool);
        }
    }, {
        xtype: 'tool',
        width: 32,
        height: 32,
        cls: 'management-tool bim-xeokit-tool',
        iconCls: Ext.baseCSSPrefix + 'fa fa-arrows',
        tooltip: CMDBuildUI.locales.Locales.bim.menu.panXeokit,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.bim.menu.panXeokit'
        },
        disabled: true,
        bind: {
            disabled: '{vision2D}'
        },
        callback: function (owner, tool, event) {
            owner.up().fireEvent("modalityNavigation", tool);
        }
    }, {
        xtype: 'tbseparator'
    }, {
        xtype: 'tool',
        itemId: 'twoD',
        width: 32,
        height: 32,
        cls: 'management-tool bim-xeokit-tool',
        iconCls: 'cmdbuildicon-planimetry',
        tooltip: CMDBuildUI.locales.Locales.bim.menu.twoD,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.bim.menu.twoD'
        },
        callback: function (owner, tool, event) {
            owner.up().fireEvent("switchDimension", tool);
        }
    }, {
        xtype: 'tool',
        itemId: 'perspective',
        width: 32,
        height: 32,
        cls: 'management-tool bim-xeokit-tool',
        iconCls: Ext.baseCSSPrefix + 'fa fa-th',
        tooltip: CMDBuildUI.locales.Locales.bim.menu.ortho,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.bim.menu.ortho'
        },
        disabled: true,
        bind: {
            disabled: '{vision2D}'
        },
        callback: function (owner, tool, event) {
            owner.up().fireEvent("switchPerspective", tool);
        }
    }, {
        xtype: 'tbseparator'
    }, {
        xtype: 'tool',
        itemId: 'slice',
        width: 32,
        height: 32,
        cls: 'management-tool bim-xeokit-tool',
        iconCls: Ext.baseCSSPrefix + 'fa fa-scissors',
        tooltip: CMDBuildUI.locales.Locales.bim.menu.enableslice,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.bim.menu.enableslice'
        },
        disabled: true,
        bind: {
            disabled: '{vision2D}'
        },
        callback: function (owner, tool, event) {
            owner.up().fireEvent("cutElement", tool);
        }
    }, {
        xtype: 'tool',
        width: 32,
        height: 32,
        cls: 'management-tool bim-xeokit-tool',
        iconCls: Ext.baseCSSPrefix + 'fa fa-eraser',
        tooltip: CMDBuildUI.locales.Locales.bim.menu.resetSlice,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.bim.menu.resetSlice'
        },
        disabled: true,
        bind: {
            disabled: '{vision2D || cutEmpty}'
        },
        callback: function (owner, tool, event) {
            owner.up().fireEvent("resetSlice", tool);
        }
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tool',
        width: 32,
        height: 32,
        cls: 'management-tool bim-xeokit-tool',
        iconCls: Ext.baseCSSPrefix + 'fa fa-save',
        tooltip: CMDBuildUI.locales.Locales.bim.menu.save,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.bim.menu.save'
        },
        hidden: true,
        callback: function (owner, tool, event) {
            owner.up().fireEvent("saveView", tool);
        }
    }, {
        xtype: 'tool',
        width: 32,
        height: 32,
        cls: 'management-tool no-action bim-xeokit-tool',
        iconCls: Ext.baseCSSPrefix + 'fa fa-question-circle',
        listeners: {
            afterrender: function (tool, eOpts) {
                tool.ownerCt.ownerCt.fireEvent("tooltipHelp", tool);
            }
        }
    }]

});