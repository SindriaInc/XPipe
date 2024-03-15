Ext.define('Overrides.view.Table', {
    override: 'Ext.view.Table',
    compatibility: ['6.2.0-6.5.0'],
    handleUpdate: function (store, record, operation, changedFieldNames, info, allColumns) {
        var me = this,
            recordIndex = me.store.indexOf(record),
            rowTpl = me.rowTpl,
            markDirty = me.markDirty,
            dirtyCls = me.dirtyCls,
            columnsToUpdate = [],
            hasVariableRowHeight = me.variableRowHeight,
            updateTypeFlags = 0,
            ownerCt = me.ownerCt,
            cellFly = me.cellFly || (me.self.prototype.cellFly = new Ext.dom.Fly()),
            oldItemDom, oldDataRow, newItemDom, newAttrs, attLen, attName, attrIndex,
            overItemCls, columns, column, len, i, cellUpdateFlag, cell, fieldName, value,
            clearDirty, defaultRenderer, scope, elData, emptyValue;
        operation = operation || Ext.data.Model.EDIT;
        clearDirty = operation !== Ext.data.Model.EDIT;
        if (me.viewReady) {
            me.updatingRows = true;
            oldItemDom = me.getNodeByRecord(record);
            if (oldItemDom) {
                if (record.isCollapsedPlaceholder) {
                    Ext.fly(oldItemDom).syncContent(me.createRowElement(record, me.indexOfRow(record)));
                    return;
                }
                overItemCls = me.overItemCls;
                columns = me.ownerCt.getVisibleColumnManager().getColumns();
                if (allColumns) {
                    columnsToUpdate = columns;
                    updateTypeFlags = 1;
                } else {
                    for (i = 0, len = columns.length; i < len; i++) {
                        column = columns[i];
                        if (column.preventUpdate) {
                            cell = Ext.fly(oldItemDom).down(column.getCellSelector(), true);
                            if (cell && !clearDirty && markDirty) {
                                cellFly.attach(cell);
                                if (record.isModified(column.dataIndex)) {
                                    cellFly.addCls(dirtyCls);
                                    if (column.dirtyTextElementId) {
                                        cell.setAttribute('aria-describedby', column.dirtyTextElementId);
                                    }
                                } else {
                                    cellFly.removeCls(dirtyCls);
                                    cell.removeAttribute('aria-describedby');
                                }
                            }
                        } else {
                            cellUpdateFlag = me.shouldUpdateCell(record, column, changedFieldNames);
                            if (cellUpdateFlag) {
                                updateTypeFlags = updateTypeFlags | cellUpdateFlag;
                                columnsToUpdate[columnsToUpdate.length] = column;
                                hasVariableRowHeight = hasVariableRowHeight || column.variableRowHeight;
                            }
                        }
                    }
                }
                me.fireEvent('beforeitemupdate', record, recordIndex, oldItemDom, columnsToUpdate);
                if (me.getRowClass || !me.getRowFromItem(oldItemDom) ||
                    (updateTypeFlags & 1) ||
                    (oldItemDom.tBodies[0].childNodes.length > 1)) {
                    elData = oldItemDom._extData;
                    newItemDom = me.createRowElement(record, me.indexOfRow(record), columnsToUpdate);
                    if (Ext.fly(oldItemDom, '_internal').hasCls(overItemCls)) {
                        Ext.fly(newItemDom).addCls(overItemCls);
                    }
                    if (Ext.isIE9m && oldItemDom.mergeAttributes) {
                        oldItemDom.mergeAttributes(newItemDom, true);
                    } else {
                        newAttrs = newItemDom.attributes;
                        attLen = newAttrs.length;
                        for (attrIndex = 0; attrIndex < attLen; attrIndex++) {
                            attName = newAttrs[attrIndex].name;
                            if (attName !== 'id') {
                                oldItemDom.setAttribute(attName, newAttrs[attrIndex].value);
                            }
                        }
                    }
                    if (elData) {
                        elData.isSynchronized = false;
                    }
                    if (columns.length && (oldDataRow = me.getRow(oldItemDom))) {
                        me.updateColumns(oldDataRow, Ext.fly(newItemDom).down(me.rowSelector, true), columnsToUpdate, record);
                    }
                    while (rowTpl) {
                        var notSync = Ext.isArray(changedFieldNames) ? !Ext.Array.contains(changedFieldNames, "_checkAttachment") : true;
                        if (rowTpl.syncContent && notSync) {
                            if (rowTpl.syncContent(oldItemDom, newItemDom, changedFieldNames ? columnsToUpdate : null) === false) {
                                break;
                            }
                        }
                        rowTpl = rowTpl.nextTpl;
                    }
                } else {
                    for (i = 0, len = columnsToUpdate.length; i < len; i++) {
                        column = columnsToUpdate[i];
                        fieldName = column.dataIndex;
                        value = record.get(fieldName);
                        cell = Ext.fly(oldItemDom).down(column.getCellSelector(), true);
                        cellFly.attach(cell);
                        if (!clearDirty && markDirty) {
                            if (record.isModified(column.dataIndex)) {
                                cellFly.addCls(dirtyCls);
                                if (column.dirtyTextElementId) {
                                    cell.setAttribute('aria-describedby', column.dirtyTextElementId);
                                }
                            } else {
                                cellFly.removeCls(dirtyCls);
                                cell.removeAttribute('aria-describedby');
                            }
                        }
                        defaultRenderer = column.usingDefaultRenderer;
                        scope = defaultRenderer ? column : column.scope;
                        if (column.updater) {
                            Ext.callback(column.updater, scope, [cell, value, record, me, me.dataSource], 0, column, ownerCt);
                        } else {
                            if (column.renderer) {
                                value = Ext.callback(column.renderer, scope, [value, null, record, 0, 0, me.dataSource, me], 0, column, ownerCt);
                            }
                            emptyValue = value == null || value.length === 0;
                            value = emptyValue ? column.emptyCellText : value;
                            if (column.producesHTML || emptyValue) {
                                cellFly.down(me.innerSelector, true).innerHTML = value;
                            } else {
                                cellFly.down(me.innerSelector, true).childNodes[0].data = value;
                            }
                        }
                        if (me.highlightClass) {
                            Ext.fly(cell).addCls(me.highlightClass);
                            if (!me.changedCells) {
                                me.self.prototype.changedCells = [];
                                me.prototype.clearChangedTask = new Ext.util.DelayedTask(me.clearChangedCells, me.prototype);
                                me.clearChangedTask.delay(me.unhighlightDelay);
                            }
                            me.changedCells.push({
                                cell: cell,
                                cls: me.highlightClass,
                                expires: Ext.Date.now() + 1000
                            });
                        }
                    }
                }
                if (clearDirty && markDirty && !record.dirty) {
                    Ext.fly(oldItemDom, '_internal').select('.' + dirtyCls).
                        removeCls(dirtyCls).
                        set({
                            'aria-describedby': undefined
                        });
                }
                if (hasVariableRowHeight) {
                    Ext.suspendLayouts();
                }
                me.fireEvent('itemupdate', record, recordIndex, oldItemDom, me);
                if (hasVariableRowHeight) {
                    me.ownerGrid.updateLayout();
                    Ext.resumeLayouts(true);
                }
            }
            me.updatingRows = false;
        }
    },
    updateColumns: function (oldRow, newRow, columnsToUpdate, record) {
        var me = this,
            newAttrs, attLen, attName, attrIndex,
            colCount = columnsToUpdate.length,
            colIndex,
            column,
            oldCell, newCell,
            cellSelector = me.getCellSelector(),
            elData;
        if (oldRow.mergeAttributes) {
            oldRow.mergeAttributes(newRow, true);
        } else {
            newAttrs = newRow.attributes;
            attLen = newAttrs.length;
            for (attrIndex = 0; attrIndex < attLen; attrIndex++) {
                attName = newAttrs[attrIndex].name;
                if (attName !== 'id') {
                    oldRow.setAttribute(attName, newAttrs[attrIndex].value);
                }
            }
        }
        elData = oldRow._extData;
        if (elData) {
            elData.isSynchronized = false;
        }
        oldRow = Ext.get(oldRow);
        newRow = Ext.get(newRow);
        for (colIndex = 0; colIndex < colCount; colIndex++) {
            column = columnsToUpdate[colIndex];
            cellSelector = me.getCellSelector(column);
            oldCell = oldRow.selectNode(cellSelector);
            newCell = newRow.selectNode(cellSelector);
            newAttrs = newCell.attributes;
            attLen = newAttrs.length;
            for (attrIndex = 0; attrIndex < attLen; attrIndex++) {
                attName = newAttrs[attrIndex].name;
                if (attName !== 'id') {
                    oldCell.setAttribute(attName, newAttrs[attrIndex].value);
                }
            }
            elData = oldCell._extData;
            if (elData) {
                elData.isSynchronized = false;
            }
            oldCell = Ext.fly(oldCell).selectNode(me.innerSelector);
            newCell = Ext.fly(newCell).selectNode(me.innerSelector);
            Ext.fly(oldCell).syncContent(newCell);
            if (record && column.onItemAdd) {
                column.onItemAdd([record]);
            }
        }
    }
});