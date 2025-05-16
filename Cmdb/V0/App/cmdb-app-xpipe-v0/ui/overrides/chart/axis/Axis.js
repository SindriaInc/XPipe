Ext.define("Override.chart.axis.Axis", {
    override: "Ext.chart.axis.Axis",

    /**
     * Get the range derived from all the bound series.
     * @return {Array}
     */
    getRange: function () {
        var me = this;

        if (me.range) {
            return me.range;
        } else if (me.masterAxis) {
            return me.masterAxis.range;
        }
        if ( Ext.isNumber(me.getMinimum()) && Ext.isNumber(me.getMaximum()) ) {
            return me.range = [me.getMinimum(), me.getMaximum()];
        }
        var min = 0,
            max = -Infinity,
            boundSeries = me.boundSeries,
            layout = me.getLayout(),
            segmenter = me.getSegmenter(),
            visibleRange = me.getVisibleRange(),
            getRangeMethod = 'get' + me.getDirection() + 'Range',
            context, attr, majorTicks,
            series, i, ln;

        // For each series bound to this axis, ask the series for its min/max values
        // and use them to find the overall min/max.
        for (i = 0, ln = boundSeries.length; i < ln; i++) {
            series = boundSeries[i];
            var minMax = series[getRangeMethod]();

            if (minMax) {
                if (minMax[0] < min) {
                    min = minMax[0];
                }
                if (minMax[1] > max) {
                    max = minMax[1];
                }
            }
        }
        if (!isFinite(max)) {
            max = me.prevMax;
        }

        if (!isFinite(min)) {
            min = me.prevMin;
        }

        if (me.getLabelInSpan() || min === max) {
            max += me.getIncrement();
            min -= me.getIncrement();
        }

        if (Ext.isNumber(me.getMinimum())) {
            min = me.getMinimum();
        } else {
            me.prevMin = min;
        }

        if (Ext.isNumber(me.getMaximum())) {
            max = me.getMaximum();
        } else {
            me.prevMax = max;
        }

        // When series `fullStack` config is used, the values may add up to
        // slightly more than the value of the `fullStackTotal` config
        // because of a precision error.
        me.range = [
            Ext.Number.correctFloat(min),
            Ext.Number.correctFloat(max)
        ];

        // It's important to call 'me.reconcileRange' after me.range
        // has been assigned to avoid circular calls.
        if (me.getReconcileRange()) {
            me.reconcileRange();
        }
        // TODO: Find a better way to do this.
        // TODO: The original design didn't take into account that the range of an axis
        // TODO: will depend not just on the range of the data of the bound series in the
        // TODO: direction of the axis, but also on the range of other axes with the
        // TODO: same direction and on the segmentation of the axis (interval between
        // TODO: major ticks).
        // TODO: While the fist omission was possible to retrofit rather gracefully
        // TODO: by adding the axis.reconcileRange method, the second one is harder to deal with.
        // TODO: The issue is that the resulting axis segmentation, which is a part of
        // TODO: the axis sprite layout has to be known before layout has begun.
        // TODO: Example for the logic below:
        // TODO: If we have a range of data of 0..34.5 the step will be 2 and we
        // TODO: will round up the max to 36 based on that step, but when the range is 0..36,
        // TODO: the step becomes 5, so we have to reconcile the range once again where max
        // TODO: becomes 40.
        if (me.getAdjustByMajorUnit() && segmenter.adjustByMajorUnit && !me.getMajorTickSteps()) {
            attr = Ext.Object.chain(me.getSprites()[0].attr);
            attr.min = me.range[0];
            attr.max = me.range[1];
            attr.visibleMin = visibleRange[0];
            attr.visibleMax = visibleRange[1];
            context = {
                attr: attr,
                segmenter: segmenter
            };
            layout.calculateLayout(context);
            majorTicks = context.majorTicks;
            if (majorTicks) {
                segmenter.adjustByMajorUnit(majorTicks.step, majorTicks.unit.scale, me.range);

                attr.min = me.range[0];
                attr.max = me.range[1];
                context.majorTicks = null;
                layout.calculateLayout(context);
                majorTicks = context.majorTicks;
                segmenter.adjustByMajorUnit(majorTicks.step, majorTicks.unit.scale, me.range);
            } else if (!me.hasClearRangePending) {
                // Axis hasn't been rendered yet.
                me.hasClearRangePending = true;
                me.getChart().on('layout', 'clearRange', me);
            }
        }

        if (!Ext.Array.equals(me.range, me.oldRange || []) ) {
            me.fireEvent('rangechange', me, me.range);
            me.oldRange = me.range;
        }
        return me.range;
    }
});