Ext.define('CMDBuildUI.view.fields.schedulerdate.Util', {
    singleton: true,

    generateEventsfromSequence: function (sequence) { //TODO: move on sequence model
        var deferred = new Ext.Deferred();

        var data = sequence.getData();
        delete data.notifications;

        if (Ext.isEmpty(sequence.get('notifications___0___template'))) {
            delete data.notifications___0___template;
            delete data.notifications___0___delay;
            delete data.notifications___0___content;
        }

        Ext.Ajax.request({
            method: 'POST',
            jsonData: data,
            url: Ext.String.format('{0}/calendar/sequences/_ANY/generate-events', CMDBuildUI.util.Config.baseUrl),
            callback: function (options, success, response) {
                response = JSON.parse(response.responseText);
                if (response.success == true) {
                    var data = response.data;
                    data.forEach(function (item, index, array) {
                        delete item.notifications;
                    });
                    deferred.resolve(data);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * 
     * @param {*} sequence Ther record to update
     * @param {*} newDate The new date
     * @param {Object} config 
     * @param {Boolean} config.keepgap This config calculates the new lastEvent date starting from the newDate and keeping the old gap. If false the gap is 0;
     */
    updateSequenceDate: function (sequence, newDate, config) {
        var config = config || {}
        Ext.applyIf(config, {
            keepgap: false
        })

        var gap = 0
        if (config.keepgap) {
            var gap = Ext.Date.diff(
                sequence.get('firstEvent'), //min
                sequence.get('lastEvent'), //max
                Ext.Date.DAY //unit
            )
        }
        var firstEvent = newDate;
        var lastEvent = Ext.Date.add(newDate, Ext.Date.DAY, gap);

        sequence.set('firstEvent', firstEvent);
        sequence.set('lastEvent', lastEvent);
    },

    /**
     * 
     * @param {*} record 
     */
    isDirty: function (record) {
        var dirty = true;

        if (record._dirty_delete !== true && record._dirty_create !== true && record._dirty_recalculate !== true && record._dirty_edit !== true) {
            dirty = false;
        }
        return dirty;
    }
});