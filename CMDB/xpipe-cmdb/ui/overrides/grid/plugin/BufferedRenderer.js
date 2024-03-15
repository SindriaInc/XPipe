Ext.define("Overrides.grid.plugin.BufferedRenderer", {
    override: 'Ext.grid.plugin.BufferedRenderer',

    /**
     * @private
     */
    viewSize: 50,


    /**
     * Called directly from {@link Ext.view.Table#onResize}. Reacts to View changing height by
     * recalculating the size of the rendered block, and either trimming it or adding to it.
     * @param {Ext.view.Table} view The Table view.
     * @param {Number} width The new Width.
     * @param {Number} height The new height.
     * @param {Number} oldWidth The old width.
     * @param {Number} oldHeight The old height.
     * @private
     */
    onViewResize: function(view, width, height, oldWidth, oldHeight) {
        var me = this,
            newViewSize;

        me.refreshSize();

        // Only process first layout (the boxready event) or height resizes.
        if (!oldHeight || height !== oldHeight) {
            // Changing the content height may trigger multiple layouts for locked grids.
            // Ensure they are coalesced.
            Ext.suspendLayouts();

            // Recalculate the view size in rows now that the grid view has changed height
            me.viewClientHeight = height || view.el.dom.clientHeight;

            // Recalculate the view size in rows now that the grid view has changed height
            me.viewClientHeight = height || view.el.dom.clientHeight;
            newViewSize = Math.ceil(height / me.rowHeight) + me.trailingBufferZone + me.leadingBufferZone;
            me.viewSize = me.setViewSize(newViewSize);

            Ext.resumeLayouts(true);
        }
    },

    stretchView: function(view, scrollRange) {
        var me = this,
            newY;

        // Ensure that both the scroll range AND the positioned view body are in the viewable area.
        if (me.scrollTop > scrollRange) {
            newY = me.nextRefreshStartIndex == null ? me.bodyHeight : scrollRange - me.bodyHeight;
            me.position = me.scrollTop = Math.max(newY, 0);
            me.scroller.scrollTo(null, me.scrollTop);
        }

        if (me.bodyTop > scrollRange) {
            view.body.translate(null, me.bodyTop = me.position);
        }

        // Tell the scroller what the scroll size is.
        if (view.getScrollable()) {
            me.refreshScroller(view, scrollRange);
        }
    },

    refreshScroller: function(view, scrollRange) {
        var scroller = view.getScrollable();

        if (scroller) {
            // Ensure the scroller viewport element size is up to date if it needs to be told
            // (touch scroller)
            if (scroller.setElementSize) {
                scroller.setElementSize();
            }

            // Ensure the scroller knows about content size
            scroller.setSize({
                x: view.headerCt.getTableWidth(),

                // No Y range in the view's scroller if we're in a locking assembly.
                // The LockingScroller stretches the views.
                y: view.lockingPartner ? null : scrollRange
            });

            // In a locking assembly, stretch the yScroller
            if (view.lockingPartner) {
                this.scroller.setSize({
                    x: 0,
                    y: scrollRange
                });
            }
        }
    },

    setViewSize: function(viewSize, fromLockingPartner) {
        var me = this,
            store = me.store,
            view = me.view,
            ownerGrid,
            rows = view.all,
            elCount = rows.getCount(),
            storeCount = store.getCount(),
            start, end,
            lockingPartner = me.view.lockingPartner && me.view.lockingPartner.bufferedRenderer,
            diff = elCount - viewSize,
            oldTop = 0,
            maxIndex = Math.max(0, storeCount - 1),
            // This is which end is closer to being visible therefore must be the first
            // to have rows added or the opposite end from which rows get removed
            // if shrinking the view.
            pointyEnd = Ext.Number.sign(
                (me.getFirstVisibleRowIndex() - rows.startIndex) -
                (rows.endIndex - me.getLastVisibleRowIndex())
            );

        // Synchronize view sizes
        if (lockingPartner && !fromLockingPartner) {
            lockingPartner.setViewSize(viewSize, true);
        }

        diff = elCount - viewSize;

        if (diff) {

            // Must be set for getFirstVisibleRowIndex to work
            me.scrollTop = me.scroller ? me.scroller.getPosition().y : 0;

            me.viewSize = viewSize;

            if (store.isBufferedStore) {
                store.setViewSize(viewSize);
            }

            // If a store loads before we have calculated a viewSize, it loads me.defaultViewSize
            // records. This may be larger or smaller than the final viewSize so the store needs
            // adjusting when the view size is calculated.
            if (elCount) {
                // New start index should be current start index unless that's now too close
                // to the end of the store to yield a full view, in which case work back
                // from the end of the store. Ensure we don't go negative.
                start = Math.max(0, Math.min(rows.startIndex, storeCount - viewSize));

                // New end index works forward from the new start index ensuring
                // we don't walk off the end
                end = Math.min(start + viewSize - 1, maxIndex);

                // Only do expensive adding or removal if range is not already correct
                if (start === rows.startIndex && end === rows.endIndex) {
                    // Needs rows adding to or bottom depending on which end is closest
                    // to being visible (The pointy end)
                    if (diff < 0) {
                        me.handleViewScroll(pointyEnd);
                    }
                }
                else {
                    // While changing our visible range, the locking partner must not sync
                    if (lockingPartner) {
                        lockingPartner.disable();
                    }

                    // View must expand
                    if (diff < 0) {

                        // If it's *possible* to add rows...
                        if (storeCount > viewSize && storeCount > elCount) {

                            // Grab the render range with a view to appending and prepending
                            // nodes to the top and bottom as necessary.
                            // Store's getRange API always has been inclusive of endIndex.
                            store.getRange(start, end, {
                                callback: function(newRecords, start, end) {
                                    ownerGrid = view.ownerGrid;

                                    // Append if necessary
                                    if (end > rows.endIndex) {
                                        // eslint-disable-next-line max-len
                                        rows.scroll(Ext.Array.slice(newRecords, rows.endIndex + 1, Infinity), 1, 0);
                                    }

                                    // Prepend if necessary
                                    if (start < rows.startIndex) {
                                        oldTop = rows.first(true);
                                        // eslint-disable-next-line max-len
                                        rows.scroll(Ext.Array.slice(newRecords, 0, rows.startIndex - start), -1, 0);

                                        // We just added some rows to the top of the rendered block
                                        // We have to bump it up to keep the view stable.
                                        me.bodyTop -= oldTop.offsetTop;
                                    }

                                    me.setBodyTop(me.bodyTop);

                                    // The newly added rows must sync the row heights
                                    // eslint-disable-next-line max-len
                                    if (lockingPartner && !fromLockingPartner && (ownerGrid.syncRowHeight || ownerGrid.syncRowHeightOnNextLayout)) {
                                        lockingPartner.setViewSize(viewSize, true);
                                        ownerGrid.syncRowHeights();
                                    }
                                }
                            });
                        }
                        // If not possible just refresh
                        else {
                            me.refreshView(0);
                        }
                    }
                    // View size is contracting
                    else {
                        // If removing from top, we have to bump the rendered block downwards
                        // by the height of the removed rows.
                        if (pointyEnd === 1) {
                            oldTop = rows.item(rows.startIndex + diff, true).offsetTop;
                        }

                        // Clip the rows off the required end
                        rows.clip(pointyEnd, diff);
                        me.setBodyTop(me.bodyTop + oldTop);
                    }

                    if (lockingPartner) {
                        lockingPartner.enable();
                    }
                }
            }

            // Update scroll range
            me.refreshSize();
        }

        return viewSize;
    },


    doVerticalScroll: function(scroller, pos, supressEvents) {
        var me = this;

        if (!scroller) {
            return;
        }

        if (supressEvents) {
            scroller.suspendEvent('scroll');
        }

        scroller.scrollTo(null, me.position = pos);

        if (supressEvents) {
            scroller.resumeEvent('scroll');
        }
    },

    /**
     * @private
     * Scrolls to and optionally selects the specified row index **in the total dataset**.
     *
     * This is a private method for internal usage by the framework.
     *
     * Use the grid's {@link Ext.panel.Table#ensureVisible ensureVisible} method to scroll
     * a particular record or record index into view.
     *
     * @param {Number/Ext.data.Model} recordIdx The record, or the zero-based position
     * in the dataset to scroll to.
     * @param {Object} [options] An object containing options to modify the operation.
     * @param {Boolean} [options.animate] Pass `true` to animate the row into view.
     * @param {Boolean} [options.highlight] Pass `true` to highlight the row with a glow animation
     * when it is in view.
     * @param {Boolean} [options.select] Pass as `true` to select the specified row.
     * @param {Boolean} [options.focus] Pass as `true` to focus the specified row.
     * @param {Function} [options.callback] A function to call when the row has been scrolled to.
     * @param {Number} options.callback.recordIdx The resulting record index (may have changed
     * if the passed index was outside the valid range).
     * @param {Ext.data.Model} options.callback.record The resulting record from the store.
     * @param {HTMLElement} options.callback.node The resulting view row element.
     * @param {Object} [options.scope] The scope (`this` reference) in which to execute
     * the callback. Defaults to this BufferedRenderer.
     * @param {Ext.grid.column.Column/Number} [options.column] The column, or column index
     * to scroll into view.
     *
     */
    scrollTo: function(recordIdx, options) {
        var args = arguments,
            me = this,
            view = me.view,
            lockingPartner = view.lockingPartner && view.lockingPartner.grid.isVisible() &&
                             view.lockingPartner.bufferedRenderer,
            store = me.store,
            total = store.getCount(),
            startIdx, endIdx, targetRow, tableTop, groupingFeature, metaGroup, record, direction;

        // New option object API
        if (options !== undefined && !(options instanceof Object)) {
            options = {
                select: args[1],
                callback: args[2],
                scope: args[3]
            };
        }

        // If we have a grouping summary feature rendering the view in groups,
        // first, ensure that the record's group is expanded,
        // then work out which record in the groupStore the record is at.
        if ((groupingFeature = view.dataSource.groupingFeature) && (groupingFeature.collapsible)) {
            if (recordIdx.isEntity) {
                record = recordIdx;
            }
            else {
                record = view.store.getAt(
                    Math.min(Math.max(recordIdx, 0), view.store.getCount() - 1)
                );
            }

            metaGroup = groupingFeature.getMetaGroup(record);

            if (metaGroup && metaGroup.isCollapsed) {
                if (!groupingFeature.isExpandingOrCollapsing && record !== metaGroup.placeholder) {
                    groupingFeature.expand(groupingFeature.getGroup(record).getGroupKey());
                    total = store.getCount();
                    recordIdx = groupingFeature.indexOf(record);
                }
                else {
                    // If we've just been collapsed, then the only record we have is
                    // the wrapped placeholder
                    record = metaGroup.placeholder;
                    recordIdx = groupingFeature.indexOfPlaceholder(record);
                }
            }
            else {
                recordIdx = groupingFeature.indexOf(record);
            }

        }
        else {

            if (recordIdx.isEntity) {
                record = recordIdx;
                recordIdx = store.indexOf(record);

                // Currently loaded pages do not contain the passed record, we cannot proceed.
                if (recordIdx === -1) {
                    //<debug>
                    Ext.raise('Unknown record passed to BufferedRenderer#scrollTo');

                    //</debug>
                    return;
                }
            }
            else {
                // Sanitize the requested record index
                recordIdx = Math.min(Math.max(recordIdx, 0), total - 1);
                record = store.getAt(recordIdx);
            }
        }

        // See if the required row for that record happens to be within the rendered range.
        if (record && (targetRow = view.getNode(record))) {
            view.grid.ensureVisible(record, options);

            // Keep the view immediately replenished when we scroll an existing element into view.
            // DOM scroll events fire asynchronously, and we must not leave subsequent code
            // without a valid buffered row block.
            me.onViewScroll();

            return;
        }

        // Calculate view start index.
        // If the required record is above the fold...
        if (recordIdx < view.all.startIndex) {
            // The startIndex of the new rendered range is a little less
            // than the target record index.
            direction = -1;

            // eslint-disable-next-line max-len
            startIdx = Math.max(Math.min(recordIdx - (Math.floor((me.leadingBufferZone + me.trailingBufferZone) / 2)), total - me.viewSize + 1), 0);
            endIdx = Math.min(startIdx + me.viewSize - 1, total - 1);
        }
        // If the required record is below the fold...
        else {
            // The endIndex of the new rendered range is a little greater
            // than the target record index.
            direction = 1;

            // eslint-disable-next-line max-len
            endIdx = Math.min(recordIdx + (Math.floor((me.leadingBufferZone + me.trailingBufferZone) / 2)), total - 1);
            startIdx = Math.max(endIdx - (me.viewSize - 1), 0);
        }

        tableTop = Math.max(startIdx * me.rowHeight, 0);

        store.getRange(startIdx, endIdx, {
            callback: function(range, start, end) {
                // Render the range.
                // Pass synchronous flag so that it does it inline, not on a timer.
                // Pass fromLockingPartner flag so that it does not inform the lockingPartner.
                me.renderRange(start, end, true);
                record = store.data.getRange(recordIdx, recordIdx + 1)[0];
                targetRow = view.getNode(record);

                // bodyTop property must track the translated position of the body
                view.body.translate(null, me.bodyTop = tableTop);

                // Ensure the scroller knows about the range if we're going down
                if (direction === 1 && view.hasVariableRowHeight()) {
                    me.refreshSize();
                }

                // Locking partner must render the same range
                if (lockingPartner) {
                    lockingPartner.renderRange(start, end, true);

                    // Sync all row heights
                    me.syncRowHeights();

                    // bodyTop property must track the translated position of the body
                    lockingPartner.view.body.translate(null, lockingPartner.bodyTop = tableTop);

                    // Ensure the scroller knows about the range if we're going down
                    if (direction === 1) {
                        lockingPartner.refreshSize();
                    }
                }

                // The target does not map to a view node.
                // Cannot scroll to it.
                if (!targetRow) {
                    return;
                }

                view.grid.ensureVisible(record, options);

                me.scrollTop = me.position = me.scroller.getPosition().y;

                if (lockingPartner) {
                    lockingPartner.position = lockingPartner.scrollTop = me.scrollTop;
                }
            }
        });
    },

    onViewScroll: function(scroller, x, scrollTop) {
        var me = this,
            bodyDom = me.view.body.dom,
            store = me.store,
            totalCount = (store.getCount()),
            vscrollDistance,
            scrollDirection;

        // May be directly called with no args, as well as from the Scroller's scroll event
        me.scrollTop = scrollTop == null ? (scrollTop = me.scroller.getPosition().y) : scrollTop;

        // Because lockable assemblies now only have one Y scroller,
        // initially hidden grids (one side may begin with all the columns)
        // still get the scroll notification, but may not have any DOM
        // to scroll.
        if (bodyDom) {
            // Only check for nearing the edge if we are enabled, and if there is overflow
            // beyond our view bounds. If there is no paging to be done
            // (Store's dataset is all in memory) we will be disabled.
            if (!(me.disabled || totalCount < me.viewSize)) {

                vscrollDistance = scrollTop - me.position;
                scrollDirection = vscrollDistance > 0 ? 1 : -1;

                // Moved at least 20 pixels, or changed direction, so test whether the numFromEdge
                // is triggered
                if (Math.abs(vscrollDistance) >= 20 ||
                    (scrollDirection !== me.lastScrollDirection)) {
                    me.lastScrollDirection = scrollDirection;
                    me.handleViewScroll(me.lastScrollDirection, vscrollDistance);
                }
            }
        }
    },

    handleViewScroll: function(direction, vscrollDistance) {
        var me = this,
            rows = me.view.all,
            store = me.store,
            storeCount = store.getCount(),
            viewSize = me.viewSize,
            lastItemIndex = storeCount - 1,
            maxRequestStart = Math.max(0, storeCount - viewSize),
            requestStart,
            requestEnd;

        // We're scrolling up
        if (direction === -1) {
            // If table starts at record zero, we have nothing to do
            if (rows.startIndex) {
                if (me.topOfViewCloseToEdge()) {
                    requestStart = Math.max(0, me.getLastVisibleRowIndex() + me.trailingBufferZone -
                                            viewSize);

                    // If, having scrolled up, a variableRowHeight calculation based
                    // upon scrolTop/rowHeight yields an obviously wrong value,
                    // then constrain it to a calculated value.
                    // We CANNOT just Math.min it with maxRequestStart, because we may already
                    // be at maxRequestStart, and asking to render the same block
                    // will have no effect.
                    // We calculate a start value a few rows above the current startIndex.
                    if (requestStart > rows.startIndex) {
                        requestStart = Math.max(
                            0, rows.startIndex + Math.floor(vscrollDistance / me.rowHeight)
                        );
                    }
                }
            }
        }
        // We're scrolling down
        else {

            // If table ends at last record, we have nothing to do
            if (rows.endIndex < lastItemIndex) {
                if (me.bottomOfViewCloseToEdge()) {
                    // eslint-disable-next-line max-len
                    requestStart = Math.max(0, Math.min(me.getFirstVisibleRowIndex() - me.trailingBufferZone, maxRequestStart));
                }
            }
        }

        // View is OK at this scroll. Advance loadId so that any load requests in flight do not
        // result in rendering upon their return.
        if (requestStart == null) {
            // View is still valid at this scroll position.
            // Do not trigger a handleViewScroll call until *ANOTHER* 20 pixels have scrolled by.
            me.position = me.scrollTop;
            me.loadId++;
        }
        // We scrolled close to the edge and the Store needs reloading
        else {
            requestEnd = Math.min(requestStart + viewSize - 1, lastItemIndex);

            // viewSize was calculated too small due to small sample row count with some skewed
            // item height in there such as a tall group header item. Bump range
            // down by one in this case.
            if (me.variableRowHeight && requestEnd === rows.endIndex &&
                requestEnd < lastItemIndex) {
                requestEnd++;
                requestStart++;
            }

            // If calculated view range has moved, then render it and return the fact
            // that the scroll was handled.
            if (requestStart !== rows.startIndex || requestEnd !== rows.endIndex) {
                me.scroller.trackingScrollTop = me.scrollTop;
                me.renderRange(requestStart, requestEnd);

                return true;
            }
        }
    },

    /**
     * @private
     * Refreshes the current rendered range if possible.
     * Optionally refreshes starting at the specified index.
     */
    refreshView: function(startIndex, scrollIncrement) {
        var me = this,
            viewSize = me.viewSize,
            view = me.view,
            rows = view.all,
            store = me.store,
            storeCount = store.getCount(),
            maxIndex = Math.max(0, storeCount - 1),
            lockingPartnerRows = view.lockingPartner && view.lockingPartner.all,
            preserveScroll = me.bodyTop && view.preserveScrollOnRefresh || scrollIncrement,
            endIndex;

        // Empty Store is simple, don't even ask the store
        if (!storeCount) {
            return me.doRefreshView([], 0, 0);
        }
        // Store doesn't fill the required view size. Simple start/end calcs.
        else if (storeCount < viewSize) {
            startIndex = 0;
            endIndex = maxIndex;
            me.nextRefreshStartIndex = preserveScroll ? null : 0;
        }
        // We're starting from nothing, but there's a locking partner with the range info,
        // so match that
        else if (startIndex == null && !rows.getCount() && lockingPartnerRows &&
                lockingPartnerRows.getCount()) {
            startIndex = lockingPartnerRows.startIndex;
            endIndex = Math.min(lockingPartnerRows.endIndex, startIndex + viewSize - 1, maxIndex);
        }
        // Work out range to refresh
        else {
            if (startIndex == null) {
                // Use a nextRefreshStartIndex as set by a load operation
                // in which we are maintaining scroll position
                if (me.nextRefreshStartIndex != null && !preserveScroll) {
                    startIndex = me.nextRefreshStartIndex;
                }
                else {
                    startIndex = rows.startIndex;
                }

                me.nextRefreshStartIndex = null;
            }

            // New start index should be current start index unless that's now too close
            // to the end of the store to yield a full view, in which case work back
            // from the end of the store. Ensure we don't go negative.
            startIndex = Math.max(0, Math.min(startIndex, maxIndex - viewSize + 1));

            // New end index works forward from the new start index ensuring
            // we don't walk off the end
            endIndex = Math.min(startIndex + viewSize - 1, maxIndex);

            if (endIndex - startIndex + 1 > viewSize) {
                startIndex = endIndex - viewSize + 1;
            }
        }

        if (startIndex === 0 && endIndex === -1) {
            me.doRefreshView([], 0, 0);
        }
        else {
            store.getRange(startIndex, endIndex, {
                callback: me.doRefreshView,
                scope: me
            });
        }
    },

    doRefreshView: function(range, startIndex, endIndex) {
        var me = this,
            view = me.view,
            scroller = me.scroller,
            rows = view.all,
            previousStartIndex = rows.startIndex,
            previousEndIndex = rows.endIndex,
            prevRowCount = rows.getCount(),
            viewMoved = startIndex !== rows.startIndex && !me.isStoreLoading,
            calculatedTop = -1,
            previousFirstItem, previousLastItem, scrollIncrement, restoreFocus;

        me.isStoreLoading = false;

        // So that listeners to the itemremove events know that its because of a refresh.
        // And so that this class's refresh listener knows to ignore it.
        view.refreshing = me.refreshing = true;

        if (view.refreshCounter) {

            // Give CellEditors or other transient in-cell items a chance to get out of the way.
            if (view.hasListeners.beforerefresh &&
                view.fireEvent('beforerefresh', view) === false) {
                return view.refreshNeeded = view.refreshing = me.refreshing = false;
            }

            // If focus was in any way in the view, whether actionable or navigable,
            // this will return a function which will restore that state.
            restoreFocus = view.saveFocusState();

            view.clearViewEl(true);
            view.refreshCounter++;

            if (range.length) {
                view.doAdd(range, startIndex);

                if (viewMoved) {
                    // Try to find overlap between newly rendered block and old block
                    previousFirstItem = rows.item(previousStartIndex, true);
                    previousLastItem = rows.item(previousEndIndex, true);

                    // Work out where to move the view top if there is overlap
                    if (previousFirstItem) {
                        scrollIncrement = -previousFirstItem.offsetTop;
                    }
                    else if (previousLastItem) {
                        scrollIncrement = rows.last(true).offsetTop - previousLastItem.offsetTop;
                    }

                    // If there was an overlap, we know exactly where to move the view
                    if (scrollIncrement) {
                        calculatedTop = Math.max(me.bodyTop + scrollIncrement, 0);
                        me.scrollTop = calculatedTop ? me.scrollTop + scrollIncrement : 0;
                    }
                    // No overlap: calculate the a new body top and scrollTop.
                    else {
                        calculatedTop = startIndex * me.rowHeight;

                        // eslint-disable-next-line max-len
                        me.scrollTop = Math.max(calculatedTop + me.rowHeight * (calculatedTop < me.bodyTop ? me.leadingBufferZone : me.trailingBufferZone), 0);
                    }
                }
            }

            // Clearing the view.
            // Ensure we jump to top.
            // Apply empty text.
            else {
                me.scrollTop = calculatedTop = me.position = 0;
                view.addEmptyText();
            }

            // Keep scroll and rendered block positions synched if there is scrolling.
            if (calculatedTop !== -1) {
                me.setBodyTop(calculatedTop);
                me.doVerticalScroll(scroller, me.scrollTop, true);
            }

            // Correct scroll range
            me.refreshSize();
            view.refreshSize(rows.getCount() !== prevRowCount);
            view.fireItemMutationEvent('refresh', view, range);

            // If focus was in any way in this view, this will restore it
            restoreFocus();

            if (view.preserveScrollOnRefresh && restoreFocus !== Ext.emptyFn) {
                me.doVerticalScroll(scroller, me.scrollTop, true);
            }

            view.headerCt.setSortState();
        }
        else {
            view.refresh();
        }

        //<debug>
        // If there are columns to trigger rendering, and the rendered block is not
        // either the view size or, if store count less than view size, the store count,
        // then there's a bug.
        if (view.getVisibleColumnManager().getColumns().length &&
            rows.getCount() !== Math.min(me.store.getCount(), me.viewSize)) {
            Ext.raise('rendered block refreshed at ' + rows.getCount() +
                      ' rows while BufferedRenderer view size is ' + me.viewSize);
        }
        //</debug>

        view.refreshNeeded = view.refreshing = me.refreshing = false;
    },

    renderRange: function(start, end, forceSynchronous) {
        var me = this,
            rows = me.view.all,
            store = me.store;

        // We're being told to render what we already have rendered.
        if (rows.startIndex === start && rows.endIndex === end) {
            return;
        }

        // Skip if we are being asked to render exactly the rows that we already have.
        // This can happen if the viewSize has to be recalculated
        // (due to either a data refresh or a view resize event) but the calculated size
        // ends up the same.
        if (!(start === rows.startIndex && end === rows.endIndex)) {

            // If range is available synchronously, process it now.
            if (store.rangeCached(start, end)) {
                me.cancelLoad();

                if (me.synchronousRender || forceSynchronous) {
                    me.onRangeFetched(null, start, end);
                }
                else {
                    if (!me.renderTask) {
                        me.renderTask = new Ext.util.DelayedTask(me.onRangeFetched, me);
                    }

                    // Render the new range very soon after this scroll event handler exits.
                    // If scrolling very quickly, a few more scroll events may fire before
                    // the render takes place. Each one will just *update* the arguments with which
                    // the pending invocation is called.
                    me.renderTask.delay(-1, null, null, [null, start, end]);
                }
            }

            // Required range is not in the prefetch buffer. Ask the store to prefetch it.
            else {
                me.attemptLoad(start, end, me.scrollTop);
            }
        }
    },

    onRangeFetched: function(range, start, end) {
        var me = this,
            view = me.view,
            scroller = me.scroller,
            viewEl = view.el,
            rows = view.all,
            increment = 0,
            calculatedTop,
            partnerView = !me.doNotMirror && view.lockingPartner,
            lockingPartner = partnerView && partnerView.bufferedRenderer,
            partnerRows = partnerView && partnerView.all,
            variableRowHeight = me.variableRowHeight,

            // eslint-disable-next-line max-len
            doSyncRowHeight = partnerView && partnerView.ownerCt.isVisible() && (view.ownerGrid.syncRowHeight || view.ownerGrid.syncRowHeightOnNextLayout || (lockingPartner.variableRowHeight !== variableRowHeight)),
            activeEl, focusedView, i, newRows, newTop, noOverlap,
            oldStart, partnerNewRows, pos, removeCount, topAdditionSize, topBufferZone, records;

        // View may have been destroyed since the DelayedTask was kicked off.
        if (view.destroyed) {
            return;
        }

        // If called as a callback from the Store, the range will be passed,
        // if called from renderRange, it won't
        if (range) {
            // Re-cache the scrollTop if there has been an asynchronous call to the server.
            me.scrollTop = scroller.getPosition().y;
        }
        else {
            range = me.store.getRange(start, end);

            // Store may have been cleared since the DelayedTask was kicked off.
            if (!range) {
                return;
            }
        }

        // If we contain focus now, but do not when we have rendered the new rows,
        // we must focus the view el.
        activeEl = Ext.fly(Ext.Element.getActiveElement());

        if (viewEl.contains(activeEl)) {
            focusedView = view;
        }
        else if (partnerView && partnerView.el.contains(activeEl)) {
            focusedView = partnerView;
        }

        // In case the browser does fire synchronous focus events when a focused element
        // is derendered...
        if (focusedView) {
            activeEl.suspendFocusEvents();
        }

        // Best guess rendered block position is start row index * row height.
        // We can use this as bodyTop if the row heights are all standard.
        // We MUST use this as bodyTop if the scroll is a teleporting scroll.
        // If we are incrementally scrolling, we add the rows to the bottom, and
        // remove a block of rows from the top.
        // The bodyTop is then incremented by the height of the removed block to keep
        // the visuals the same.
        //
        // We cannot always use the calculated top, and compensate by adjusting the scroll position
        // because that would break momentum scrolling on DOM scrolling platforms, and would be
        // immediately undone in the next frame update of a momentum scroll on touch scroll
        // platforms.
        calculatedTop = start * me.rowHeight;

        // The new range encompasses the current range. Refresh and keep the scroll position stable
        if (start < rows.startIndex && end > rows.endIndex) {
            // How many rows will be added at top. So that we can reposition the table
            // to maintain scroll position
            topAdditionSize = rows.startIndex - start;

            // MUST use View method so that itemremove events are fired so widgets can be recycled.
            view.clearViewEl(true);
            newRows = view.doAdd(range, start);
            view.fireItemMutationEvent('itemadd', range, start, newRows, view);

            // Keep other side's rendered block the same
            if (lockingPartner) {
                partnerView.clearViewEl(true);
                partnerNewRows = partnerView.doAdd(range, start);
                partnerView.fireItemMutationEvent('itemadd', range, start, partnerNewRows,
                                                  partnerView);

                // We're going to be doing measurement of newRows
                // Ensure heights are synced first
                if (doSyncRowHeight) {
                    me.syncRowHeights(newRows, partnerNewRows);
                    doSyncRowHeight = false;
                }
            }

            for (i = 0; i < topAdditionSize; i++) {
                increment -= me.grid.getElementHeight(newRows[i]);
            }

            // We've just added a bunch of rows to the top of our range,
            // so move upwards to keep the row appearance stable
            newTop = me.bodyTop + increment;
        }
        else {
            // No overlapping nodes; we'll need to render the whole range.
            // teleported flag is set in getFirstVisibleRowIndex/getLastVisibleRowIndex if
            // the table body has moved outside the viewport bounds
            noOverlap = me.teleported || start > rows.endIndex || end < rows.startIndex;

            if (noOverlap) {
                view.clearViewEl(true);
                me.teleported = false;
            }

            if (!rows.getCount()) {
                newRows = view.doAdd(range, start);
                view.fireItemMutationEvent('itemadd', range, start, newRows, view);

                // Keep other side's rendered block the same
                if (lockingPartner) {
                    partnerView.clearViewEl(true);
                    partnerNewRows = lockingPartner.view.doAdd(range, start);
                    partnerView.fireItemMutationEvent('itemadd', range, start, partnerNewRows,
                                                      partnerView);
                }

                newTop = calculatedTop;

                // Adjust the bodyTop to place the data correctly around the scroll vieport
                if (noOverlap && variableRowHeight) {
                    topBufferZone = me.scrollTop < me.position
                        ? me.leadingBufferZone
                        : me.trailingBufferZone;

                    // Can't calculate a new top if there are fewer than topBufferZone rows above us
                    if (start > topBufferZone) {
                        // eslint-disable-next-line max-len
                        newTop = Math.max(me.scrollTop - rows.item(rows.startIndex + topBufferZone - 1, true).offsetTop, 0);
                    }
                }
            }
            // Moved down the dataset (content moved up): remove rows from top, add to end
            else if (end > rows.endIndex) {
                removeCount = Math.max(start - rows.startIndex, 0);

                // We only have to bump the table down by the height of removed rows
                // if rows are not a standard size
                if (variableRowHeight) {
                    increment = rows.item(rows.startIndex + removeCount, true).offsetTop;
                }

                records = Ext.Array.slice(range, rows.endIndex + 1 - start);
                newRows = rows.scroll(records, 1, removeCount);

                if (lockingPartner) {
                    partnerNewRows = partnerRows.scroll(records, 1, removeCount);
                }

                // We only have to bump the table down by the height of removed rows
                // if rows are not a standard size
                if (variableRowHeight) {
                    // Bump the table downwards by the height scraped off the top
                    newTop = me.bodyTop + increment;
                }
                // If the rows are standard size, then the calculated top will be correct
                else {
                    newTop = calculatedTop;
                }
            }
            // Moved up the dataset: remove rows from end, add to top
            else {
                removeCount = Math.max(rows.endIndex - end, 0);
                oldStart = rows.startIndex;
                records = Ext.Array.slice(range, 0, rows.startIndex - start);
                newRows = rows.scroll(records, -1, removeCount);

                if (lockingPartner) {
                    partnerNewRows = partnerRows.scroll(records, -1, removeCount);
                }

                // We only have to bump the table up by the height of top-added rows if
                // rows are not a standard size. If they are standard, calculatedTop is correct.
                // Sync the row heights *before* calculating the newTop and increment
                if (doSyncRowHeight) {
                    me.syncRowHeights(newRows, partnerNewRows);
                    doSyncRowHeight = false;

                    // Bump the table upwards by the height added to the top
                    newTop = me.bodyTop - rows.item(oldStart, true).offsetTop;

                    // We've arrived at row zero...
                    if (!rows.startIndex) {
                        // But the calculated top position is out. It must be zero at this point
                        // We adjust the scroll position to keep visual position of table the same.
                        if (newTop) {
                            me.doVerticalScroll(scroller, me.scrollTop -= newTop);
                            newTop = 0;
                        }
                    }
                    // Not at zero yet, but the position has moved into negative range
                    else if (newTop < 0) {
                        increment = rows.startIndex * me.rowHeight;
                        me.doVerticalScroll(scroller, me.scrollTop += increment);
                        newTop = me.bodyTop + increment;
                    }
                }
                // If the rows are standard size, then the calculated top will be correct
                else {
                    newTop = calculatedTop;
                }
            }

            // The position property is the scrollTop value *at which the table was last correct*
            // MUST be set at table render/adjustment time
            me.position = me.scrollTop;
        }

        // A view contained focus at the start, check whether activeEl has been derendered.
        // Focus the cell's column header if so.
        if (focusedView) {
            // Restore active element's focus processing.
            activeEl.resumeFocusEvents();

            if (!focusedView.el.contains(activeEl)) {
                pos = focusedView.actionableMode
                    ? focusedView.actionPosition
                    : focusedView.lastFocused;

                if (pos && pos.column) {
                    // we set the rendering rows to true here so the actionables know
                    // that view is forcing the onFocusLeave method here
                    focusedView.renderingRows = true;
                    focusedView.onFocusLeave({});
                    focusedView.renderingRows = false;

                    me.getNewFocusTarget(pos).focus();
                }
            }
        }

        // Calculate position of item container.
        newTop = Math.max(Math.floor(newTop), 0);

        if (view.positionBody) {
            me.setBodyTop(newTop, true);
        }

        // Sync the other side to exactly the same range from the dataset.
        // Then ensure that we are still at exactly the same scroll position.
        if (lockingPartner) {
            // Locking partner BufferedRenderer must not react to the scroll.
            lockingPartner.scrollTop = me.scrollTop;

            if (lockingPartner.bodyTop !== newTop) {
                lockingPartner.setBodyTop(newTop, true);
            }

            if (doSyncRowHeight) {
                me.syncRowHeights(newRows, partnerNewRows);
            }
        }
        else if (variableRowHeight) {
            delete me.rowHeight;
            me.refreshSize();
        }

        //<debug>
        // If there are columns to trigger rendering, and the rendered block
        // is not either the view size or, if store count less than view size,
        // the store count, then there's a bug.
        if (view.getVisibleColumnManager().getColumns().length &&
            rows.getCount() !== Math.min(me.store.getCount(), me.viewSize)) {
            Ext.raise('rendered block refreshed at ' + rows.getCount() +
                      ' rows while BufferedRenderer view size is ' + me.viewSize);
        }
        //</debug>

        return newRows;
    },

    /**
     * Gets the next focus target based on the position
     * @param {Ext.grid.CellContext} pos
     * @returns {Ext.Component}
     * @since 6.2.2
     */
    getNewFocusTarget: function(pos) {
        var view = pos.view,
            grid = view.grid,
            column = pos.column,
            hiddenHeaders = column.isHidden() || grid.hideHeaders,
            tabbableItems;

        // Focus MUST NOT silently die due to DOM removal. Focus will be moved
        // in the following order as available:
        // Try focusing the contextual column header
        if (column.focusable && !hiddenHeaders) {
            return column;
        }

        tabbableItems = column.el.findTabbableElements();

        // Failing that, look inside it for a tabbable element
        if (tabbableItems && tabbableItems.length) {
            return tabbableItems[0];
        }

        // Failing that, find the available focus target of the grid or focus the view
        return grid.findFocusTarget() || view.el;
    },


    syncRowHeightsFinish: function() {
        var me = this,
            view = me.view,
            lockingPartner = view.lockingPartner.bufferedRenderer,
            ownerGrid = view.ownerGrid,
            scrollable = view.getScrollable();

        ownerGrid.syncRowHeightOnNextLayout = false;

        // Now that row heights have potentially changed, both BufferedRenderers
        // have to re-evaluate what they think the average rowHeight is
        // based on the synchronized-height rows.
        //
        // If the view has not been layed out, then the upcoming first resize event
        // will trigger the needed refreshSize call; See onViewRefresh -
        // If control arrives there and the componentLayoutCounter is zero and
        // there is variableRowHeight, it schedules itself to be run on boxready
        // so refreshSize will be called there for the first time.
        if (view.componentLayoutCounter) {
            delete me.rowHeight;
            me.refreshSize();
            delete lockingPartner.rowHeight;
            lockingPartner.refreshSize();
        }

        // Component layout only restores the scroller's state for managed layouts
        // here we need to make sure the scroller is restores after the rows sync
        if (scrollable) {
            scrollable.restoreState();
        }
    }
});