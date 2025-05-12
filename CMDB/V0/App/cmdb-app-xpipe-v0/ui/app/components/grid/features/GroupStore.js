/**
 * Private record store class which takes the place of the view's data store to provide a grouped
 * view of the data when the Grouping feature is used.
 *
 * Relays granular mutation events from the underlying store as refresh events to the view.
 *
 * On mutation events from the underlying store, updates the summary rows by firing update events on the corresponding
 * summary records.
 * @private
 */
Ext.define('CMDBuildUI.components.grid.features.GroupStore', {
    extend: 'Ext.grid.feature.GroupStore',
 
    processStore: function (store) {
        var me = this,
            groupingFeature = me.groupingFeature,
            collapseAll = Ext.isBoolean(groupingFeature.startCollapsed) ? groupingFeature.startCollapsed : false,
            collapseLimit = Ext.isNumber(groupingFeature.startCollapsed) ? groupingFeature.startCollapsed : null,
            data = me.data,
            groups = store.getGroups(),
            groupCount = groups ? groups.length : 0,
            groupField = store.getGroupField(),
            // We need to know all of the possible unique group names. The only way to know this is to check itemGroupKeys, which will keep a 
            // list of all potential group names. It's not enough to get the key of the existing groups since the collection may be filtered. 
            groupNames = groups && Ext.Array.unique(Ext.Object.getValues(groups.itemGroupKeys)),
            isCollapsed = false,
            oldMetaGroupCache = groupingFeature.getCache(),
            oldItem, metaGroup, metaGroupCache, i, len, featureGrouper, 
            group, groupName, groupPlaceholder, key, modelData, Model;
 
        groupingFeature.invalidateCache();
        // Get a new cache since we invalidated the old one. 
        metaGroupCache = groupingFeature.getCache();
 
        if (data) {
            data.clear();
        } else {
            data = me.data = new Ext.util.Collection({
                rootProperty: 'data',
                extraKeys: {
                    byInternalId: {
                        property: 'internalId',
                        rootProperty: ''
                    }
                }
            });
        }
 
        if (store.getCount()) {
            // Upon first process of a loaded store, clear the "always" collapse" flag 
            groupingFeature.startCollapsed = false;
 
            if (groupCount > 0) {
                Model = store.getModel();
 
                for (i = 0; i < groupCount; i++) {
                    group = groups.getAt(i);
 
                    // Cache group information by group name. 
                    key = group.getGroupKey();
 
                    // If there is no store grouper and the groupField looks up a complex data type, the store will stringify it and 
                    // the group name will be '[object Object]'. To fix this, groupers can be defined in the feature config, so we'll 
                    // simply do a lookup here and re-group the store. 
                    // 
                    // Note that if a grouper wasn't defined on the feature that we'll just default to the old behavior and still try 
                    // to group. 
                    if (me.badGrouperKey === key && (featureGrouper = groupingFeature.getGrouper(groupField))) {
                        // We must reset the value b/c store.group() will call into this method again! 
                        groupingFeature.startCollapsed = collapseAll;
                        store.group(featureGrouper);
                        return;
                    }
 
                    oldItem = oldMetaGroupCache[key];
                    metaGroup = metaGroupCache[key] = groupingFeature.getMetaGroup(key);
                    if (oldItem) {
                        metaGroup.isCollapsed = oldItem.isCollapsed;
                    }
 
                    // Remove the group name from the list of all possible group names. This is how we'll know if any remaining groups 
                    // in the old cache should be retained. 
                    Ext.Array.splice(groupNames, Ext.Array.indexOf(groupNames, key), 1);
 
                    isCollapsed = metaGroup.isCollapsed = (collapseLimit && group.items.length > collapseLimit) || collapseAll || metaGroup.isCollapsed;
 
                    // If group is collapsed, then represent it by one dummy row which is never visible, but which acts 
                    // as a start and end group trigger. 
                    if (isCollapsed) {
                        modelData = {};
                        modelData[groupField] = key;
                        metaGroup.placeholder = groupPlaceholder = new Model(modelData);
                        groupPlaceholder.isNonData = groupPlaceholder.isCollapsedPlaceholder = true;
                        groupPlaceholder.groupKey = key;
                        data.add(groupPlaceholder);
                    }
                    // Expanded group - add the group's child records. 
                    else {
                        data.insert(me.data.length, group.items);
                    }
                }
 
                if (groupNames.length) {
                    // The remainig group names in this list may refer to potential groups that have been filtered/sorted. If the group 
                    // name exists in the old cache, we must retain it b/c the groups could be recreated. See EXTJS-15755 for an example. 
                    // Anything left in the old cache can be discarded. 
                    for (i = 0, len = groupNames.length; i < len; i++) {
                        groupName = groupNames[i];
                        metaGroupCache[groupName] = oldMetaGroupCache[groupName];
                    }
                }
 
                oldMetaGroupCache = null;
            } else {
                data.add(store.getRange());
            }
        }
    }
});