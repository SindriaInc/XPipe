Ext.define('CMDBuildUI.view.bim.bimserver.tab.cards.LayersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-bimserver-tab-cards-layers',
    listen: {
        component: {
            '#gridActionColumn': {
                'click': 'onLayerCheckDidChangeHandler'
            },
            '#topMenuShowAll': {
                'click': 'onShowAll'
            },
            '#topMenuHideAll': {
                'click': 'onHideAll'
            },
            '#': {
                'beforeselect': function (grid, record, index, eOpts) {
                    return false;
                }
            }
        },
        global: {
            highlitedifcobject: 'onHighlitedIfcObject'
        }
    },

    /**
     * @param {Ext.view.Table} tableView
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} event 
     * @param {Ext.data.Model} record
     * @param {HTMLElement} row
     */
    onLayerCheckDidChangeHandler: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {
        // var clicks = (record.get('clicks') + 1) % 3;
        //FIXME: has been disabled the middle stata. reactive when issue #35 of bimsurfer will be closed
        var clicks;
        record.get('clicks') == 0 ? clicks = 2 : clicks = 0;
        record.set('clicks', clicks);

        var objects = record.get('objects');
        Ext.Array.forEach(objects, function (item, index, array) {
            item.trans.mode = clicks;
        }, this);
        CMDBuildUI.util.bim.Viewer.updateVisibility(objects);

        this.onLayerCheckDidChange([record], clicks);
    },

    /**
     * @param {Ext.view.Table} tableView
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} event 
     * @param {Ext.data.Model} record
     * @param {HTMLElement} row
     */
    onShowAll: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {
        var store = this.getView().getStore();
        store.each(function (element) {
            var clicks = 0;
            element.set('clicks', clicks); // set each action column with the correct value

            var objects = element.get('objects');
            Ext.Array.forEach(objects, function (item, index, array) {
                item.trans.mode = clicks;
            }, this);
            CMDBuildUI.util.bim.Viewer.updateVisibility(objects);
        }, this);

        this.onLayerCheckDidChange(store.getRange());
    },

    /**
     * @param {Ext.view.Table} tableView
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} event 
     * @param {Ext.data.Model} record
     * @param {HTMLElement} row
     */
    onHideAll: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {
        var store = this.getView().getStore();

        store.each(function (element) {
            var clicks = 2;
            element.set('clicks', clicks); // set each action column with the correct value
            var objects = element.get('objects');
            Ext.Array.forEach(objects, function (item, index, array) {
                item.trans.mode = clicks;
            }, this);
            CMDBuildUI.util.bim.Viewer.updateVisibility(objects);
        }, this);

        this.onLayerCheckDidChange(store.getRange());
    },

    /**
     * @param {[Stringi]} ifcNames contains the name of ifcLayer wich will change the transparence
     * @param {Number} value the state of transparence: 0, 1, 2 
     */
    onLayerCheckDidChange: function (records) {
        var view = this.getView();
        var hiddenTypes = view.getHiddenTypes();

        hiddenTypes.beginUpdate();
        hiddenTypes.endUpdate();
    },

    /**
     * 
     */
    onHighlitedIfcObject: function (highlited) {
        var store = this.getView().getStore();

        if (store) {
            var name = highlited.getType().replace('Ifc', "");
            var record = store.findRecord('name', name);
            var view = this.getView();

            if (view && record) {
                view.ensureVisible(record, {
                    select: true,
                    focus: true
                });
                this.getView().getSelectionModel().select(record, false, true);
            }
        }
    }
});
