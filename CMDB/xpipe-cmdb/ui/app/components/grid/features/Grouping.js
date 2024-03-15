Ext.define("CMDBuildUI.components.grid.features.Grouping", {
    extend: 'Ext.grid.feature.Grouping',

    alias: 'feature.customgrouping',

    /**
     * @cfg {Boolean|Numeric} [startCollapsed=false]
     * True to start all groups collapsed. 
     * If a number, that number is used as max lenght for 
     * expanded groups at start.
     */
    startCollapsed: false,

    init: function (grid) {
        var me = this,
            view = me.view,
            store = me.getGridStore(),
            lockPartner, dataSource;
 
        view.isGrouping = store.isGrouped();
 
        me.mixins.summary.init.call(me);
 
        // me.callParent([grid]);
 
        view.headerCt.on({
            columnhide: me.onColumnHideShow,
            columnshow: me.onColumnHideShow,
            columnmove: me.onColumnMove,
            scope: me
        });
 
        // Add a table level processor 
        view.addTpl(Ext.XTemplate.getTpl(me, 'outerTpl')).groupingFeature = me;
 
        // Add a row level processor 
        view.addRowTpl(Ext.XTemplate.getTpl(me, 'groupRowTpl')).groupingFeature = me;
 
        view.preserveScrollOnRefresh = true;
 
        // Sparse store - we can never collapse groups 
        if (store.isBufferedStore) {
            me.collapsible = false;
        }
        // If it's a local store we can build a grouped store for use as the view's dataSource 
        else {
 
            // Share the GroupStore between both sides of a locked grid 
            lockPartner = me.lockingPartner;
            if (lockPartner && lockPartner.dataSource) {
                me.dataSource = view.dataSource = dataSource = lockPartner.dataSource;
            } else {
                me.dataSource = view.dataSource = dataSource = new CMDBuildUI.components.grid.features.GroupStore(me, store);
            }
        }
 
        grid = grid.ownerLockable || grid;
 
        // Before the reconfigure, rebind our GroupStore dataSource to the new store 
        grid.on('beforereconfigure', me.beforeReconfigure, me);
        
        if (!view.isLockedView) {
            me.gridEventRelayers = grid.relayEvents(view, me.relayedEvents);
        }
 
        view.on({
            afterrender: me.afterViewRender,
            scope: me,
            single: true
        });
 
        me.groupRenderInfo = {};
 
        if (dataSource) {
            // Listen to dataSource groupchange so it has a chance to do any processing 
            // before we react to it 
            dataSource.on('groupchange', me.onGroupChange, me);
        } else {
            me.setupStoreListeners(store);
        }
        
        me.mixins.summary.bindStore.call(me, grid, grid.getStore());
    }
});