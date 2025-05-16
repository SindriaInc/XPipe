Ext.define('CMDBuildUI.view.joinviews.configuration.items.ContextMenusFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.joinviews-configuration-items-contextmenusfieldset',

    data: {
        contextMenuCount: 0
    },
    formulas: {

        contextMenuItemsStoreNewData: {
            bind: {
                theView: '{theView}'
            },
            get: function (data) {
                if (data.theView) {
                    var cleanRecord = Ext.create('CMDBuildUI.model.ContextMenuItem');
                    return [CMDBuildUI.util.administration.helper.ModelHelper.setReadState(cleanRecord)];
                }
                return [];
            }
        },
        contextMenuItemData: {
            bind: {
                theView: '{theView}'
            },
            get: function (data) {
                return (data.theView && data.theView.contextMenuItems()) ? data.theView.contextMenuItems() : [];
            }
        },
        contexMenuTypes: {
            get: function () {
                return CMDBuildUI.model.ContextMenuItem.getTypes();
            }
        },
        contextMenuApplicabilities: {
            get: function () {
                return CMDBuildUI.model.ContextMenuItem.getVisibilities();

            }
        },
        countersManager: {
            bind: {
                theView: '{theView}'
            },
            get: function (data) {
                this.set('contextMenuCount', data.theView.contextMenuItems().getCount());
            }
        }
    },
    stores: {
        contextMenuItemsStoreNew: {
            model: 'CMDBuildUI.model.ContextMenuItem',
            proxy: {
                type: 'memory'
            },
            data: '{contextMenuItemsStoreNewData}',
            autoDestroy: true
        },
        contextMenuComponentStore: {
            model: 'CMDBuildUI.model.base.Base',
            source: 'customcomponents.ContextMenus',
            pageSize: 0
        },
        contextMenuItemsStore: {
            model: 'CMDBuildUI.model.ContextMenuItem',
            proxy: {
                type: 'memory'
            },
            data: '{contextMenuItemData}',
            autoDestroy: true
        },
        contextMenuItemTypeStore: {
            autoLoad: true,
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            autoDestroy: true,
            data: '{contexMenuTypes}'
        },

        contextMenuApplicabilityStore: {
            type: 'common-applicability',
            data: '{contextMenuApplicabilities}'
        }
    }
});