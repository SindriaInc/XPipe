Ext.define("Override.chart.legend.SpriteLegend", {
    override: "Ext.chart.legend.SpriteLegend",

    isXType: function (xtype, shallow) {
        return false;
    }

});