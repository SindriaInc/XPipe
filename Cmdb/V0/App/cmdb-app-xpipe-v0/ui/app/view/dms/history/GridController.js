Ext.define('CMDBuildUI.view.dms.history.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-history-grid',

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionDownload: function (view, rowIndex, colIndex, item, e, record, row) {
        var url = Ext.String.format(
            "{0}{1}/{2}/{3}",
            CMDBuildUI.util.Config.baseUrl,
            this.getViewModel().get('proxyUrl'),
            record.getId(), // attachment id
            record.get("name") // file name
        );
        CMDBuildUI.util.File.download(url, record.get("name"));
    }

});
