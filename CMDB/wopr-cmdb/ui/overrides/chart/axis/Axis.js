Ext.define("Override.chart.axis.Axis", {
    override: "Ext.chart.axis.Axis",

    /**
    @override
     * @cfg {String} id
     * The **unique** id of this axis instance.
     */
    config: {
        /**
         * @cfg {Number} minimum
         * The minimum value drawn by the axis. If not set explicitly, the axis
         * minimum will be calculated automatically.
         */
        minimum: 0
    }

});