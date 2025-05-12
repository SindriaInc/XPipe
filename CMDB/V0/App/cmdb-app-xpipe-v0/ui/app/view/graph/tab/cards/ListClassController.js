Ext.define('CMDBuildUI.view.graph.tab.cards.ListClassController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-tab-cards-listclass',
    listen: {
        component: {
            '#': {
                selectionchange: 'onSelectionChange'
            }
        },
        controller: {
            'graph-graphcontainer': {
                acquisitionend: 'onAcquisitionEnd'
            }
        },
        store: {
            '#relationStore': {
                clear: 'onRelationStoreClear'
            }
        }
    },
    /**
     * This function handles the event in wich the store finisced loading.
     * To fill it's information in the store it refers to listClassStore
     */
    onAcquisitionEnd: function () {
        var selected = this.saveSelected();

        var store = this.getViewModel().get('listClassStore');
        var listCardStore = this.getStore('listCardStore');// this.getViewModel().get('listCardStore');

        store.removeAll(); //doesn't fire event

        // variable declaration
        var classTarget, recordIndex, newRecord, oldRecord, newValue, destTypeDescription;

        var records = listCardStore.getRange();
        records.forEach(function (record) {
            classTarget = record.get('_destinationType');
            recordIndex = store.find('classTarget', classTarget);

            if (recordIndex == -1) {//inserty new record
                destTypeDescription = record.get('destTypeDescription');

                newRecord = Ext.create('CMDBuildUI.model.graph.ListClass', {
                    classTarget: classTarget,
                    qt: 1,
                    destTypeDescription: destTypeDescription
                });

                store.insert(0, newRecord);

            } else {
                //increment quantity
                oldRecord = store.getAt(recordIndex);
                newValue = oldRecord.get('qt') + 1;
                oldRecord.set('qt', newValue);
            }

        }, this);

        store.commitChanges(); //avoid dirty records
        this.restoreSelected(selected, store);
    },

    /**
     * This function handles the selection from GRID -> CANVAS
     * @param {Ext.selection.Model} selectionModel
     * @param {[Ext.data.Model]} selected
     * @param {Object} eOpts
     */
    onSelectionChange: function (selectionModel, selected, eOpts) {
        if (!selected.length) return;
        var ids = [];
        var store = this.getViewModel().get('relationStore');
        var classTarget = selected[0].get('classTarget');

        store.getRange().forEach(function (record) {
            if (record.get('_destinationType') == classTarget) {
                ids.push(record.get('_destinationId'));
            }
        }, this);

        CMDBuildUI.graph.threejs.SceneUtils.setSelectedNode(ids, false);

    },

    /**
     * @param {Ext.data.Model} store
     */
    saveSelected: function () {
        var selectionModel = this.getView().getSelectionModel();
        var selected = selectionModel.getSelected().items[0];
        if (selected) {
            return {
                classTarget: selected.get('classTarget'),
                qt: selected.get('qt')
            };
        }
        return null;
    },

    /**
     * 
     */
    restoreSelected: function (selected, store) {
        if (!selected) return;

        var selectionModel = this.getView().getSelectionModel();
        var record = store.findRecord('classTarget', selected.classTarget);

        if (record && record.get('qt') == selected.qt) {
            selectionModel.select(record, false, true);
        }

    },

    /**
     * @param {String}
     */
    getStore: function (storeName) {
        var v = this.getView();
        var viewParent = v.up('graph-tab-tabpanel');
        var viewSibling = viewParent.lookupReference('graph-tab-cards-listcard');
        var store = viewSibling.getViewModel().get(storeName);
        return store;
    },


    /**
     * @param {Ext.data.Store} relationStore
     * @param {Object} eOpts 
     */
    onRelationStoreClear: function (relationStore, eOpts) {
        var vm = this.getViewModel();
        var store = vm.get('listClassStore');

        store.removeAll();
    }

});
