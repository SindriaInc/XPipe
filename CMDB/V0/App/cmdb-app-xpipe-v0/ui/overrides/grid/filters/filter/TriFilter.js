/**
 * Abstract base class for filter implementations.
 */
Ext.define('Overrides.grid.filters.filter.TriFilter', {
    override: 'Ext.grid.filters.filter.TriFilter',

    countActiveFilters: function () {
        var store = this.getGridStore();
        var advancedFilter = store.getAdvancedFilter();
        var i = 0;

        for (var attribute in advancedFilter.getAttributes()) {
            i++;
        }
        return i;
    }
});