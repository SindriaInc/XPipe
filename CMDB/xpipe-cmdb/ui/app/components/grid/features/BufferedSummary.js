Ext.define('CMDBuildUI.components.grid.features.BufferedSummary', {
    extend: 'Ext.grid.feature.Summary',
    alias: 'feature.bufferedsummary',

    dock: 'bottom',

    createSummaryRecord: function (view) {
        var me = this,
            columns = view.headerCt.getGridColumns(),
            remoteRoot = me.remoteRoot,
            summaryRecord = me.summaryRecord,
            colCount = columns.length,
            i, column,
            dataIndex, summaryValue, modelData, summaryValuePromises = [];

        if (!summaryRecord) {
            modelData = {
                id: view.id + '-summary-record'
            };
            summaryRecord = me.summaryRecord = new Ext.data.Model(modelData);
        }

        // Set the summary field values
        summaryRecord.beginEdit();

        if (remoteRoot) {
            summaryValue = me.generateSummaryData();

            if (summaryValue) {
                summaryRecord.set(summaryValue);
            }
        } else {
            for (i = 0; i < colCount; i++) {
                column = columns[i];

                // In summary records, if there's no dataIndex, then the value in regular rows must come from a renderer.
                // We set the data value in using the column ID.
                dataIndex = column.dataIndex || column.getItemId();

                // We need to capture this value because it could get overwritten when setting on the model if there
                // is a convert() method on the model.
                summaryValue = me.getSummary(view.store, column.summaryType, dataIndex);
                if (summaryValue && summaryValue.$className === "Ext.promise.Promise") {
                    summaryValuePromises.push(summaryValue);
                } else {
                    summaryRecord.set(dataIndex, summaryValue);
                    // Capture the columnId:value for the summaryRenderer in the summaryData object.
                    me.setSummaryData(summaryRecord, column.getItemId(), summaryValue);
                }
            }
        }

        summaryRecord.endEdit(true);
        // It's not dirty
        summaryRecord.commit(true);
        summaryRecord.isSummary = true;

        if (!Ext.isEmpty(summaryValuePromises)) {
            Ext.Promise.all(summaryValuePromises).then(function (attributes) {
                summaryRecord.beginEdit();
                attributes.forEach(function(attribute) {
                    summaryRecord.set(attribute[0], attribute[1]);
                    me.setSummaryData(summaryRecord, attribute[0], attribute[1]);
                });
                summaryRecord.endEdit(true);
                // It's not dirty
                summaryRecord.commit(true);
                var newRowDom = Ext.fly(view.createRowElement(summaryRecord, -1)).down(me.summaryRowSelector, true);
                var p = me.summaryBar.item.dom.firstChild;
                var oldRowDom = p.firstChild;
                
                p.insertBefore(newRowDom, oldRowDom);
                p.removeChild(oldRowDom);

                // If docked, the updated row will need sizing because it's outside the View
                me.onColumnHeaderLayout();
            });
        }

        return summaryRecord;
    }
});