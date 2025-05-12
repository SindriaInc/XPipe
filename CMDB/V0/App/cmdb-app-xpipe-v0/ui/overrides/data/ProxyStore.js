Ext.define('Override.data.ProxyStore', {
    override: 'Ext.data.ProxyStore',

    config: {
        /**
         * 
         */
        advancedFilter: null
    },

    constructor: function (config) {
        var me = this;

        // <debug> 
        var configModel = me.model;
        // </debug> 

        /**
         * @event beforeload
         * Fires before a request is made for a new data object. If the beforeload handler returns false the load
         * action will be canceled.
         *
         * **Note:** If you are using a buffered store, you should use
         * {@link Ext.data.Store#beforeprefetch beforeprefetch}.
         *
         * @param {Ext.data.Store} store This Store
         * @param {Ext.data.operation.Operation} operation The Ext.data.operation.Operation object that will be passed to the Proxy to
         * load the Store
         * @since 1.1.0
         */

        /**
         * @event load
         * Fires whenever the store reads data from a remote data source.
         *
         * **Note:** If you are using a buffered store, you should use
         * {@link Ext.data.Store#prefetch prefetch}.
         *
         * @param {Ext.data.Store} this
         * @param {Ext.data.Model[]} records An array of records
         * @param {Boolean} successful True if the operation was successful.
         * @param {Ext.data.operation.Read} operation The 
         * {@link Ext.data.operation.Read Operation} object that was used in the data 
         * load call
         * @since 1.1.0
         */

        /**
         * @event write
         * Fires whenever a successful write has been made via the configured {@link #proxy Proxy}
         * @param {Ext.data.Store} store This Store
         * @param {Ext.data.operation.Operation} operation The {@link Ext.data.operation.Operation Operation} object that was used in
         * the write
         * @since 3.4.0
         */

        /**
         * @event beforesync
         * Fired before a call to {@link #sync} is executed. Return false from any listener to cancel the sync
         * @param {Object} options Hash of all records to be synchronized, broken down into create, update and destroy
         */

        /**
         * @event metachange
         * Fires when this store's underlying reader (available via the proxy) provides new metadata.
         * Metadata usually consists of new field definitions, but can include any configuration data
         * required by an application, and can be processed as needed in the event handler.
         * This event is currently only fired for JsonReaders.
         * @param {Ext.data.Store} this
         * @param {Object} meta The JSON metadata
         * @since 1.1.0
         */


        /**
         * Temporary cache in which removed model instances are kept until successfully
         * synchronised with a Proxy, at which point this is cleared.
         *
         * This cache is maintained unless you set `trackRemoved` to `false`.
         *
         * @protected
         * @property {Ext.data.Model[]} removed
         */
        me.removed = [];

        me.callParent(arguments);

        if (me.getAsynchronousLoad() === false) {
            me.flushLoad();
        }

        // <debug> 
        if (!me.getModel() && me.useModelWarning !== false && me.getStoreId() !== 'ext-empty-store') {
            // There are a number of ways things could have gone wrong, try to give as much information as possible 
            var logMsg = [
                Ext.getClassName(me) || 'Store',
                ' created with no model.'
            ];

            if (typeof configModel === 'string') {
                logMsg.push(" The name '", configModel, "'", ' does not correspond to a valid model.');
            }

            Ext.log.warn(logMsg.join(''));
        }
        // </debug> 

        me.on("beforeload", me.applyAdvancedFilterBeforeLoad);
    },

    /**
     * 
     * @return {CMDBuildUI.util.AdvancedFilter}
     */
    getAdvancedFilter: function () {
        var advancedFilter = this.callParent();
        if (!advancedFilter) {
            this.setAdvancedFilter(new CMDBuildUI.util.AdvancedFilter());
            advancedFilter = this.callParent();
        }

        return advancedFilter
    },

    applyAdvancedFilter: function (newvalue) {
        if (newvalue && newvalue.isAdvancedFilter) {
            return newvalue;
        } else if (Ext.isObject(newvalue)) {
            var advancedFilter =  new CMDBuildUI.util.AdvancedFilter();
            advancedFilter.applyAdvancedFilter(newvalue);
            return advancedFilter;
        } else {
            return null;
        }
    },

    applyAdvancedFilterBeforeLoad: function () {
        if (this.getAdvancedFilter()) {
            var encoded = this.getAdvancedFilter().encode();

            if (this.getProxy().getExtraParams) {
                var extraparams = this.getProxy().getExtraParams();
                if (encoded) {
                    extraparams = Ext.apply(extraparams, {
                        filter: encoded
                    });
                } else if (extraparams && extraparams.filter) {
                    delete extraparams.filter;
                }
                this.getProxy().setExtraParams(extraparams);
            }
        }
    },

    /**
     * Finds the index of the first matching Record in this store by a specific field value.
     *
     * When store is filtered, finds records only within filter.
     *
     * **IMPORTANT
     *
     * If this store is {@link Ext.data.BufferedStore Buffered}, this can ONLY find records which happen to be cached in the page cache.
     * This will be parts of the dataset around the currently visible zone, or recently visited zones if the pages
     * have not yet been purged from the cache.**
     *
     * @param {String} property The name of the Record field to test.
     * @param {String/RegExp} value Either a string that the field value
     * should begin with, or a RegExp to test against the field.
     * @param {Number} [startIndex=0] The index to start searching at
     * @param {Boolean} [anyMatch=false] True to match any part of the string, not just the
     * beginning.
     * @param {Boolean} [caseSensitive=false] True for case sensitive comparison
     * @param {Boolean} [exactMatch=true] True to force exact match (^ and $ characters
     * added to the regex). Ignored if `anyMatch` is `true`.
     * @return {Number} The matched index or -1
     */
    find: function (property, value, startIndex, anyMatch, caseSensitive, exactMatch) {
        //             exactMatch 
        //  anyMatch    F       T 
        //      F       ^abc    ^abc$ 
        //      T       abc     abc 
        // 
        
        exactMatch = exactMatch || true;
        return this.callParent([property, value, startIndex, anyMatch, caseSensitive, exactMatch]);
    },

    onFilterEndUpdate: function() {
        var me = this,
            suppressNext = me.suppressNextFilter,
            filters = me.getFilters(false);
        // If the collection is not instantiated yet, it's because we are constructing.
        if (!filters) {
            return;
        }
        if (me.getRemoteFilter() && filters.length) {
            me.getFilters().each(function(filter) {
                if (filter.getInitialConfig().filterFn) {
                    Ext.raise('Unable to use a filtering function in conjunction with remote filtering.');
                }
            });
            me.currentPage = 1;
            if (!suppressNext) {
                me.load();
            }
        } else if (!suppressNext) {
            me.fireEvent('datachanged', me);
            me.fireEvent('refresh', me);
        }
        if (me.trackStateChanges) {
            // We just mutated the filter collection so let's save stateful filters from this point forward.
            me.saveStatefulFilters = true;
        }
        // This is not affected by suppressEvent.
        me.fireEvent('filterchange', me, me.getFilters().getRange());
    }
});
