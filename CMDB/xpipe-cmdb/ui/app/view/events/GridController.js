Ext.define('CMDBuildUI.view.events.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-grid',
    listen: {
        global: {
            carddeleted: 'onCardDeleted',
            cardcreated: 'onCardCreated',
            cardupdated: 'onCardUpdated'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            rowdblclick: 'onRowDblClick',
            selectionchange: 'onSelectionChange'

        },
        tableview: {
            expandbody: 'onExpandBody'
        }
    },

    /**
     * @param {} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var model = Ext.ClassManager.get('CMDBuildUI.model.calendar.Event'),
            vm = this.getViewModel(),
            columns = CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                reducedGrid: !view.getMaingrid(),
                preferences: CMDBuildUI.util.helper.UserPreferences.getGridPreferences(vm.get("objectType"), vm.get("objectTypeName"))
            });

        //This code make possible change the color for the column "missingDays" to set it red if che value is < 0
        var missingdayscol = Ext.Array.findBy(columns, function (element, index) {
            if (element.dataIndex == 'missingDays') {
                return true;
            }
        }, this);
        missingdayscol.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
            var color = (value < 0) ? 'red' : 'black',
                content = '<span style="color:' + color + ';">' + value + '</span>';
            return Ext.String.format(
                "<div class=\"{0}cell-content\">{1}</div>",
                Ext.baseCSSPrefix,
                content
            );
        };

        var typeCol = Ext.Array.findBy(columns, function (item, index) {
            return item.dataIndex == 'Type';
        });
        typeCol.sortable = false;

        view.reconfigure(null, columns);
        // hide selection column
        setTimeout(function () {
            if (!view.isMultiSelectionEnabled() && view.selModel.column) {
                view.selModel.column.hide();
            }
        }, 200);
    },

    /**
     * @param {Ext.selection.RowModel} element
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {HTMLElement} rowIndex
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRowDblClick: function (element, record, rowIndex, e, eOpts) {
        this.redirectTo(
            CMDBuildUI.util.Navigation.getScheduleBaseUrl(
                record.getId(),
                CMDBuildUI.mixins.DetailsTabPanel.actions.edit),
            true);
        return false;
    },

    /**
     * @param {CMDBuildUI.model.calendar.Event} record
     */
    onCardUpdated: function (record) {
        this.getView().updateRowWithExpader(record);
    },

    /**
     * 
     */
    onCardDeleted: function () {
        var store = this.getView().getStore();
        store.load();
    },

    /**
     * 
     * @param {CMDBuildUI.model.calendar.Event} record 
     */
    onCardCreated: function (record) {
        var view = this.getView(),
            store = this.getView().getStore(),
            newid = record.getId();

        // add event listener. Use event listener instaed of callback
        // otherwise the load listener used within afterLoadWithPosition
        store.on({
            load: {
                fn: function () {
                    view.expandRowAfterLoadWithPosition(store, newid); //TODO: make an afterLoadWithPosition function like in class/gridController.i
                },
                scope: this,
                single: true
            }
        })

        store.load({
            proxy: {
                extraParams: {
                    positionOf: record.getId(),
                    positionOf_goToPage: false
                }
            }
        });
    },

    /**
     * 
     * @param {*} view 
     * @param {*} selected 
     * @param {*} eOpts 
     */
    onSelectionChange: function (selectionModel, selected, eOpts) {
        var view = this.getView();
        if (view.isMultiSelectionEnabled()) {
            return;
        } else {
            var selectedId = null;
            if (selected.length == 1) {
                selectedId = selected[0].getId();
                view.setSelectedId(selectedId);

            } else if (selected.length == 0) {
                view.setSelectedId(selectedId);
            }

            if (view.getMaingrid()) {
                if (!CMDBuildUI.util.Navigation.getManagementDetailsWindow(false)) {

                    if (!Ext.util.History.currentToken.includes(selectedId)) {
                        //if the url is not yet set from other components

                        var currentAction = CMDBuildUI.util.Navigation.getCurrentContext().currentaction,
                            url = CMDBuildUI.util.Navigation.getScheduleBaseUrl(selectedId, currentAction, false);

                        Ext.util.History.add(url);
                    }
                }
            }
        }
    },

    onExpandBody: function (rowNode, record, expandRow, eOpts) {
        var view = this.getView();
        if (view.isMultiSelectionEnabled()) {
            view.setSelectedId(record.getId());
        }
    }
});
