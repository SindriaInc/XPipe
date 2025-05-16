Ext.define('Override.data.BufferedStore', {
    override: 'Ext.data.BufferedStore',

    afterChange: function (item, modified, type) {
        var me = this;
        me.fireEvent('update', me, item, type, modified, {});
    },

    afterEdit: function (record, modifiedFieldNames) {
        this.needsSync = this.needsSync || record.dirty;
        this.afterChange(record, modifiedFieldNames, Ext.data.Model.EDIT);
    },

    /**
     * @override The original function will wait for the loading of 
     * the first two pages before return true.
     * 
     * @param {*} start 
     * @param {*} end 
     * @param {*} forRender 
     */
    rangeCached: function(start, end, forRender) {
        var requiredStart = start,
            requiredEnd = end;
        // If this is for getting data to render, we must wait for a slightly wider range to be cached.
        // This is to allow grouping features to peek at the two surrounding records
        // when rendering a *range* of records to see whether the start of the range
        // really is a group start and the end of the range really is a group end.
        return this.getData().hasRange(requiredStart, requiredEnd);
    }
});