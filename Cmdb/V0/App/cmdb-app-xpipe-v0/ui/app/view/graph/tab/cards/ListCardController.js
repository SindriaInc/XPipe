Ext.define('CMDBuildUI.view.graph.tab.cards.ListCardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-tab-cards-listcard',
    listen: {
        component: {
            '#': {
                selectionchange: 'onSelectionChange'
            }
        },
        store: {
            '#relationStore': {
                add: 'onRelationStoreAdd',
                remove: 'onRelationStoreRemove',
                clear: 'onRelationStoreClear'
            }
        }
    },

    /**
     * This function handles the selection from GRID -> CANVAS
     * @param {Ext.selection.Model} selectionModel
     * @param {[Ext.data.Model]} selected
     * @param {Object} eOpts
     */
    onSelectionChange: function (selectionModel, selected, eOpts) {
        var ids = [];
        selected.forEach(function (select) {
            ids.push(select.get('_destinationId'));
        }, this);

        CMDBuildUI.graph.threejs.SceneUtils.setSelectedNode(ids, false);
    },

    /**
     * This function handles this store
     * @param {Ext.data.store} relationStore 
     * @param {Ext.data.Model} records 
     * @param {Number} index
     * @param {Object} eOpts 
     */
    onRelationStoreAdd: function (store, records, index, eOpt) {
        var st = this.getViewModel().get('listCardStore');
        var foundRecord;

        records.forEach(function (record) {
            foundRecord = st.findRecord('_destinationId', record.get('_destinationId'));
            if (!foundRecord) {
                var newRecord = record.clone();
                var bool = false;

                //handles compound name in destination. Used to get class description from class.Class store
                var destinationType = newRecord.get('_destinationType');

                if (destinationType.includes('compound_')) {
                    destinationType = destinationType.replace('compound_', '');
                    bool = true; //make another operation
                }

                //get class description and set it
                var CMDBClass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(destinationType);
                var destTypeDescription = CMDBClass.getTranslatedDescription();
                newRecord.set('destTypeDescription', destTypeDescription); //NOTE: wait for specification definitio

                //Extra operation for compound Node 
                if (bool === true) {
                    newRecord.set('destTypeDescription', Ext.String.format(
                        '{0} Compound',
                        destinationType
                    ));

                    newRecord.set('_destinationDescription', Ext.String.format(
                        '{0} {1}',
                        destinationType,
                        record.nodes().getRange().length
                    ));
                }

                st.insert(0, newRecord);
            }
        }, this);
        st.commitChanges();

    },
    /**
     * This function handles this store
     * @param {Ext.data.store} store 
     * @param {[Ext.data.Model]} records 
     * @param {Number} index
     * @param {Boolean} isMove
     * @param {Object} eOpts 
     */
    onRelationStoreRemove: function (store, records, index, isMove, eOpts) {
        var st = this.getViewModel().get('listCardStore');
        var foundRecord;
        records.forEach(function (record) {//Is possible to avoid the forEach loop and remove all the records
            foundRecord = st.findRecord('_destinationId', record.get('_destinationId'));
            if (foundRecord) {
                st.remove(foundRecord);
            }
        }, this);
    },
    /**
     * @param {Ext.data.Store} relationStore
     * @param {Object} eOpts 
     */
    onRelationStoreClear: function (relationStore, eOpts) {
        var vm = this.getViewModel();
        var store = vm.get('listCardStore');

        store.removeAll();
    }
});
