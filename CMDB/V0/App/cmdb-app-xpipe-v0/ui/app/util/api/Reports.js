/**
 * @file CMDBuildUI.util.api.Reports
 * @module CMDBuildUI.util.api.Reports
 * @author Tecnoteca srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Reports', {
    singleton: true,

    /**
     * Get report attributes by report id.
     *
     * @param {Number|String} reportId
     *
     * @returns {String}
     */
    getReportAttributesUrlByReportId: function (reportId) {
        return Ext.String.format(
            "/reports/{0}/attributes",
            reportId
        );
    },

    /**
     * Get download report url.
     *
     * @param {Number|String} reportId
     * @param {String} extension
     *
     * @returns {String}
     */
    getReportDownloadUrl: function (reportId, extension) {
        var filename = "report";
        if (reportId) {
            var report = Ext.getStore('reports.Reports').getById(reportId);
            filename = report ? report.get("description") : filename;
        }
        if (extension) {
            filename += "." + extension;
        }
        filename = filename.replace(/\//g, "-");
        return Ext.String.format(
            "/reports/{0}/{1}",
            reportId,
            filename
        );
    },

    /**
     * Get execution report url.
     *
     * @param {Number|String} reportId
     *
     * @returns {String}
     */
    getReportExecutionUrl: function (reportId) {
        return Ext.String.format(
            "/reports/{0}/execute",
            reportId
        );
    }
});