Ext.define('CMDBuildUI.view.joinviews.configuration.items.DataSortingController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-configuration-items-datasorting',

    control: {
        '#': {
            classaliaschange: 'onClassAliasChange',
            domainchange: 'onDomainChange'
        }
    },

    onAddNewDefaultOrderBtn: function (view, rowIndex, colIndex) {
        var attribute = view.down("#defaultOrderAttribute");
        var direction = view.down("#defaultOrderDirection");
        var orderGrid = view.lookupReferenceHolder().lookupReference("defaultOrderGrid");
        var orderStore = orderGrid.getStore();
        var newRecordStore = view.getStore();
        var required = [CMDBuildUI.locales.Locales.administration.common.messages.thisfieldisrequired];
        if (!direction.getValue()) {
            direction.markInvalid(required);
        }

        if (!attribute.getValue()) {
            attribute.markInvalid(required);
        }
        if (attribute.getValue() && direction.getValue()) {
            Ext.suspendLayouts();
            orderStore.add(CMDBuildUI.model.AttributeOrder.create({
                property: attribute.getValue(),
                direction: direction.getValue()
            }));
            newRecordStore.removeAll();
            newRecordStore.add(CMDBuildUI.model.AttributeOrder.create({
                direction: 'ASC'
            }));
            orderGrid.getView().refresh();
            Ext.resumeLayouts();
        }
    },

    moveUp: function (grid, rowIndex, colIndex) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        var record = store.getAt(rowIndex);
        rowIndex--;
        if (!record || rowIndex < 0) {
            return;
        }

        store.remove(record);
        store.insert(rowIndex, record);

        grid.refresh();
        Ext.resumeLayouts();
    },
    moveDown: function (grid, rowIndex, colIndex) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        var record = store.getAt(rowIndex);
        rowIndex++;
        if (!record || rowIndex >= store.getCount()) {
            return;
        }
        store.remove(record);
        store.insert(rowIndex, record);

        grid.refresh();
        Ext.resumeLayouts();
    },
    deleteRow: function (grid, rowIndex, colIndex, item, event, record, row) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        store.remove(record);
        grid.grid.reconfigure(store);
        grid.refresh();
        Ext.resumeLayouts();
    },

    onClassAliasChange: function (input, newValue, oldValue) {
        this.getView().down("#defaultOrderGrid").getView().refresh();
    },

    onDomainChange: function (record, context, eOpts) {
        this.getView().down("#defaultOrderGrid").getView().refresh();
    }

});