Ext.define('CMDBuildUI.util.administration.helper.NavTreeHelper', {
    singleton: true,

    onItemExpand: function (node) {

        Ext.suspendLayouts();
        var me = this;
        var vm = me.view.getViewModel();
        var theNavigationTreeNodes = vm.get('theNavigationtree.nodes');
        var targetClass = node.get('targetClass') || vm.get('theNavigationtree.targetClass');
        if (targetClass) {
            var targetType = node.get("targetIsProcess") ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
            var targetObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(targetClass, targetType);
            var targetHierarchy = targetObject.getHierarchy();
            targetObject.getDomains().then(function (domains) {
                if (!me.view.destroyed) {
                    domains.getRange().forEach(function (d) {
                        if (d.get("active")) {
                            var tnt = theNavigationTreeNodes;
                            var found = CMDBuildUI.util.administration.helper.NavTreeHelper.findCheckedNodes(node, tnt);

                            var isCheckedNode = found && found.domain === d.get('name');

                            if (Ext.Array.contains(targetHierarchy, d.get("source"))) {
                                var destinationType = d.get("destinationProcess") ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                                var destinationObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(d.get("destination"), destinationType);

                                node.appendChild({
                                    _id: isCheckedNode ? found._id : Math.random().toString(98).replace(/[^a-z]+/g, '').substr(0, 25),
                                    text: d.get("description") + ' [' + d.getTranslatedDescriptionDirect() + ' ' + destinationObject.get("description") + ']',
                                    targetClass: d.get("destination"),
                                    targetIsProcess: d.get("destinationProcess"),
                                    domain: d.get("name"),
                                    checked: isCheckedNode ? true : false,
                                    direction: '_1',
                                    filter: isCheckedNode ? found.filter : '',
                                    parent: node.get('_id'),
                                    showOnlyOne: isCheckedNode ? found.showOnlyOne : false,
                                    recursionEnabled: isCheckedNode ? found.recursionEnabled : false,
                                    expanded: isCheckedNode ? true : false,
                                    leaf: false
                                });
                            }
                            if (Ext.Array.contains(targetHierarchy, d.get("destination"))) {
                                var sourceType = d.get("sourceProcess") ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                                var sourceObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(d.get("source"), sourceType);
                                if (!node.findChild('domain', d.get('name'))) {
                                    node.appendChild({
                                        _id: isCheckedNode ? found._id : Math.random().toString(98).replace(/[^a-z]+/g, '').substr(0, 25),
                                        text: d.get("description") + ' [' + d.getTranslatedDescriptionInverse() + ' ' + sourceObject.get("description") + ']',
                                        targetClass: d.get("source"),
                                        targetIsProcess: d.get("sourceProcess"),
                                        domain: d.get("name"),
                                        checked: isCheckedNode ? true : false,
                                        filter: isCheckedNode ? found.filter : '',
                                        direction: '_2',
                                        parent: node.get('_id'),
                                        showOnlyOne: isCheckedNode ? found.showOnlyOne : false,
                                        recursionEnabled: isCheckedNode ? found.recursionEnabled : false,
                                        expanded: isCheckedNode ? true : false,
                                        leaf: false
                                    });
                                }
                            }
                        }
                    });
                }
            });
        }

        Ext.resumeLayouts();
    },

    getViewPlugins: function () {
        return {
            pluginId: 'cellediting',
            ptype: 'cellediting',
            clicksToEdit: 1,
            listeners: {
                beforeedit: function (editor, context) {
                    if (editor.view.lookupViewModel().get('actions.view') || (!context.record.get('checked') && !context.record.get('filter'))) {
                        return false;
                    }
                }

            }
        };
    },

    getTreeListeners: function () {
        return {
            beforecheckchange: function () {
                return !this.getView().lookupViewModel().get('actions.view');
            },
            checkchange: function (node, checked) {

                function checkParent(node, checked) {
                    var parent = node.parentNode;
                    if (parent) {
                        parent.set('checked', checked);
                        if (parent.parentNode) {
                            checkParent(parent, checked);
                        }
                    }
                }

                function uncheckChild(node, checked) {
                    var childrens = node.childNodes;
                    Ext.Array.forEach(childrens, function (childNode, i) {
                        childNode.set('checked', checked);
                        childNode.set('showOnlyOne', false);
                        childNode.set('recursionEnabled', false);
                        uncheckChild(childNode, checked);
                    });
                }

                if (checked) {
                    checkParent(node, checked);
                } else {
                    node.set('showOnlyOne', false);
                    node.set('recursionEnabled', false);
                    uncheckChild(node, checked);
                }
            }
        };
    },

    recursiveParentCheck: function (node, checked) {
        var me = this;
        if (checked) {
            node.set('checked', true);
            if (node.parentNode) {
                // up direction
                CMDBuildUI.util.administration.helper.NavTreeHelper.recursiveParentCheck(node.parentNode, checked);
            }
        } else {
            // down direction
            node.set('checked', false);
            if (node.childNodes && node.childNodes.length) {
                Ext.Array.forEach(node.childNodes, function (item) {
                    CMDBuildUI.util.administration.helper.NavTreeHelper.recursiveParentCheck(item, checked);
                });
            }
        }
    },

    findCheckedNodes: function (node, nodes) {
        var foundNode;
        Ext.Array.forEach(nodes, function (item) {
            if (!foundNode) {
                if (item.parent === node.get('_id')) {
                    foundNode = item;
                } else {
                    foundNode = CMDBuildUI.util.administration.helper.NavTreeHelper.findCheckedNodes(node, item.nodes);
                }
            }
        });
        return foundNode;
    }

});