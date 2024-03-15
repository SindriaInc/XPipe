Ext.define('CMDBuildUI.components.grid.plugin.FormInRowWidget', {
    extend: 'Ext.grid.plugin.RowWidget',
    alias: 'plugin.forminrowwidget',

    config: {
        ui: 'default',

        /**
         * @cfg {Boolean} removeWidgetOnCollapse
         * Default to `false`. When `true` the widget will be removed on
         * row collapse.
         */
        removeWidgetOnCollapse: false
    },

    rowBodyDivCls: 'forminrowwidget',

    bindView: function (view) {
        var me = this,
            listeners = {
                itemclick: me.onItemClick,
                togglerow: me.onToggleRow,
                itemupdated: me.onItemUpdated,
                itemremoved: me.onItemRemoved,
                itemcreated: me.onItemCreated,
                scope: me
            };
        view.on(listeners);

        this.callParent(arguments);
    },

    onToggleRow: function (view, record, rowIdx, e) {
        var me = this;
        me.toggleRow(rowIdx, record);
    },

    onItemClick: function (view, record, row, rowIdx, e) {
        var me = this;
        // not expand row if multi-selection is enabled and user click on selection checkbox
        setTimeout(function () {
            if (
                Ext.isEmpty(view.getSelectionModel().excludeToggleOnColumn) ||
                !Ext.Array.contains(Ext.Array.from(view.getSelectionModel().excludeToggleOnColumn), e.position.colIdx)
            ) {
                me.toggleRow(rowIdx, record);
            }
        }, 100);
    },

    getHeaderConfig: function () {
        var me = this,
            lockable = me.grid.lockable && me.grid;

        return {
            width: me.headerWidth,
            ignoreExport: true,
            lockable: false,
            autoLock: true,
            sortable: false,
            resizable: false,
            draggable: false,
            hideable: false,
            menuDisabled: true,
            selectable: true,
            tdCls: Ext.baseCSSPrefix + 'grid-cell-special',
            innerCls: Ext.baseCSSPrefix + 'grid-cell-inner-row-expander',
            renderer: function () {
                return '<div class="' + Ext.baseCSSPrefix + 'grid-row-expander" role="presentation" tabIndex="0"></div>';
            },

            // This column always migrates to the locked side if the locked side is visible. 
            // It has to report this correctly so that editors can position things correctly 
            isLocked: function () {
                return lockable && (lockable.lockedGrid.isVisible() || this.locked);
            },

            // In an editor, this shows nothing. 
            editRenderer: function () {
                return '&#160;';
            }
        };
    },
    /**
     * Event for create grid and form in row
     * @param {Ext.grid.Panel} grid
     * @param {Ext.data.Model} record
     */
    onItemCreated: function (grid, record, scope) {
        var me = this;
        var gridParams = {
            pageSize: grid.getStore().getConfig().pageSize,
            page: false,
            start: 0,
            positionOf: record.id
        };
        this.gridReload(grid, record, gridParams, scope, function () {
            var store = grid.getStore();
            var index = store.findExact("id", record.getId());
            var storeItem = store.getById(record.getId());
            Ext.asap(function (_grid, _storeItem, _index) {
                _grid.getView().refresh();
                this.view.fireEventArgs('togglerow', [null, _storeItem, _index]);
            }, me, [grid, storeItem, index]);
        });
    },

    /**
     * Event for remove grid and form in row
     * @param {Ext.grid.Panel} grid
     * @param {Ext.data.Model} record
     */
    onItemRemoved: function (grid, record, scope) {
        var me = this;
        var store = grid.getStore();
        var index = store.findExact("_id", record.getId());
        var storeItem = store.getById(record.getId());
        this.view.fireEventArgs('togglerow', [null, storeItem, index]);
        //me.view.destroy();
        Ext.asap(function () {
            store.load({
                callback: function (records, operation, success) {
                    grid.getView().refresh();
                    if (index) {
                        grid.getView().bufferedRenderer.scrollTo(index);
                    }
                },
                scope: scope
            });
        });

    },
    /**
     * Event for update grid and form in row
     * @param {Ext.grid.Panel} grid
     * @param {Ext.data.Model} record
     * @param {Object} scope
     * @param {Boolean} ignoreGridParams
     */
    onItemUpdated: function (grid, record, scope, ignoreGridParams) {
        var me = this;
        var store = grid.getStore();
        var index = store.findExact("_id", record.getId());
        var storeItem = store.getAt(index);

        if (index !== -1) {
            me.view.fireEventArgs('togglerow', [null, storeItem, index]);
        }
        var gridParams = (ignoreGridParams) ? {} : {
            limit: store.getConfig().pageSize,
            page: false,
            start: 0,
            positionOf: record.id
        };
        this.gridReload(grid, record, gridParams, scope, function () {
            Ext.asap(function (_grid, _store) {
                var index = _store.findExact("_id", record.getId());
                var storeItem = _store.getAt(index);
                me.view.fireEventArgs('togglerow', [_grid, storeItem, index]);

            }, me, [grid, store]);
        });
    },

    privates: {
        /**
         * @private
         * 
         */
        gridReload: function (view, record, params, scope, cb) {
            var store = view.getStore();
            store.load({
                params: params,
                callback: function (records, operation, success) {
                    view.getView().refresh();
                    if (store.getProxy().reader.metaData.positions && store.getProxy().reader.metaData.positions[record.id]) {
                        var position = store.getProxy().reader.metaData.positions[record.id];
                        view.getView().bufferedRenderer.scrollTo(position);

                        if (cb && typeof cb === 'function') {
                            cb();
                        }
                    } else if (cb && typeof cb === 'function') {
                        cb();
                    }
                },
                scope: scope
            });
        },
        /**
         * @private
         * @override
         * 
         */
        addWidget: function (view, record) {
            var me = this,
                target, width, widget,
                hasAttach = !!me.onWidgetAttach,
                isFixedSize = me.isFixedSize,
                el;
            // If the record is non data (placeholder), or not expanded, return
            if (record.isNonData || !me.recordsExpanded[record.internalId]) {
                return;
            }

            target = Ext.fly(view.getNode(record)).down(me.rowBodyFeature.innerSelector);
            width = target.getWidth(true) - target.getPadding('lr');
            widget = me.getWidget(view, record);
            // Might be no widget if we are handling a lockable grid
            // and only one side has a widget definition.
            if (widget) {
                // Bind widget to record unless it has declared a binding
                if (widget.defaultBindProperty && !widget.getBind()) {
                    widget.setConfig(widget.defaultBindProperty, record);
                }
                if (hasAttach) {
                    Ext.callback(me.onWidgetAttach, me.scope, [
                        me,
                        widget,
                        record
                    ], 0, me);
                }

                // double check if element dom exist 
                // sencha version: el = widget.el || widget.element.dom
                el = (widget.el && widget.el.dom) ? widget.el : (widget.element && widget.element.dom) ? widget.element : undefined;
                if (el) {
                    target.dom.appendChild(el.dom);
                    if (!isFixedSize && widget.width !== width) {
                        widget.setWidth(width);
                    } else {
                        widget.updateLayout();
                    }

                    widget.reattachToBody();
                } else {
                    if (!isFixedSize) {
                        widget.width = width;
                    }
                    widget.render(target);
                }
                view.down(me.widget.xtype).fireEventArgs('itemupdated', [view, record]);
                widget.setWidth('100%');

            }
            return widget;
        },
        /**
         * @private
         * @override
         * @param {Object} config
         */
        toggleRow: function (rowIdx, record, config) {
            config = config || {};
            // if server fail to get the data and record is udefined
            if (!record) {
                return;
            }
            rowIdx = (rowIdx === -1 && record.get('index')) ? record.get('index') : rowIdx;

            var me = this,
                // If we are handling a lockable assembly, 
                // handle the normal view first 
                view = me.normalView || me.view;

            var rowNode = view ? view.getNodeByRecord(record) : null;
            if (!rowNode) {
                me.recordsExpanded = {};
                return true;
            }
            var normalRow = Ext.fly(rowNode),
                lockedRow,
                nextBd = normalRow.down(me.rowBodyTrSelector, true),
                wasCollapsed = normalRow.hasCls(me.rowCollapsedCls),
                addOrRemoveCls = wasCollapsed ? 'removeCls' : 'addCls',
                ownerLockable = me.grid.lockable && me.grid,
                hasCheckbox = me.grid.getSelectionModel().column ? !me.grid.getSelectionModel().column.hidden : false,
                widget = me.grid.liveRowContexts[record.internalId].widgets[me.getId() + '-' + view.getId()]

            normalRow[addOrRemoveCls](me.rowCollapsedCls);
            Ext.fly(nextBd)[addOrRemoveCls](me.rowBodyHiddenCls);

            // All layouts must be coalesced. 
            // Particularly important for locking assemblies which need 
            // to sync row height on the next layout. 
            Ext.suspendLayouts();

            if (widget && me.getRemoveWidgetOnCollapse()) {
                delete me.grid.liveRowContexts[record.internalId].widgets[me.getId() + '-' + view.getId()];
                delete me.grid.liveRowContexts[record.internalId].viewModel
                widget.destroy();
            }

            // We're expanding 
            if (wasCollapsed && record.crudState !== 'D') {
                // close all expanded records
                Ext.Array.each(Object.keys(me.recordsExpanded), function (key, index) {
                    me.toggleRow(me.recordsExpanded[key].rowIdx, me.recordsExpanded[key]);
                });

                me.recordsExpanded[record.internalId] = record;
                widget = me.addWidget(view, record);

                // set selection in grid
                if (!hasCheckbox) {
                    view.selectionModel.select(record, false, config.suppressSelectEvent || false) //setSelection(record);
                }
            } else {
                delete me.recordsExpanded[record.internalId];

                // clear selection in grid
                if (!hasCheckbox) {
                    view.setSelection(null);
                }
            }

            // Sync the collapsed/hidden classes on the locked side 
            if (ownerLockable) {

                // Only attempt to toggle lockable side if it is visible. 
                if (ownerLockable.lockedGrid.isVisible()) {

                    view = me.lockedView;

                    // Process the locked side. 
                    lockedRow = Ext.fly(view.getNode(rowIdx));
                    // Just because the grid is locked, doesn't mean we'll necessarily have a locked row. 
                    if (lockedRow) {
                        lockedRow[addOrRemoveCls](me.rowCollapsedCls);

                        // If there is a template for expander content in the locked side, toggle that side too 
                        nextBd = lockedRow.down(me.rowBodyTrSelector, true);
                        Ext.fly(nextBd)[addOrRemoveCls](me.rowBodyHiddenCls);

                        // Pass an array if we're in a lockable assembly. 
                        if (wasCollapsed && me.lockedWidget) {
                            widget = [widget, me.addWidget(view, record)];
                        } else {
                            widget = [widget, me.getWidget(view, record)];
                        }

                    }

                    // We're going to need a layout run to synchronize row heights 
                    ownerLockable.syncRowHeightOnNextLayout = true;
                }
            }

            view.updateLayout();
            Ext.resumeLayouts(true);
            if (record.crudState !== 'D') {
                me.view.fireEvent(wasCollapsed ? 'expandbody' : 'collapsebody', rowNode, record, nextBd, widget);
                if (me.scrollIntoViewOnExpand && wasCollapsed) {
                    // We need to delay this activity to allow the user to edit the item when a doubleclick occurs 
                    new Ext.util.DelayedTask(function () {
                        if (me.grid) {
                            me.grid.ensureVisible(record);
                        }
                    }).delay(200);
                }
            }
        }
    }
});