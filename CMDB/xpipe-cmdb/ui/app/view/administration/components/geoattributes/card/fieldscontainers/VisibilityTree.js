Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.fieldscontainers.VisibilityTree', {
    extend: 'Ext.form.Panel',

    alias: 'widget.administration-components-geoattributes-card-fieldscontainers-visibilitytree',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,


    items: [{
        itemId: 'geoattributevisibilitytree',
        xtype: 'treepanel',
        rootVisible: false,
        bind: {
            store: '{gridStore}'
        },
        cls: 'nogridborder',
        ui: 'administration-navigation-tree',
        header: false,

        listeners: {
            beforecheckchange: {
                fn: function (node, value, element, eOpts) {
                    if (node.get('disabled')) {
                        return false;
                    }
                    return !this.lookupViewModel().get('actions.view');
                }
            },
            checkchange: {
                fn: function (node, value, element, eOpts) {
                    var vm = this.lookupViewModel();
                    var visibility = vm.get('theGeoAttribute.visibility');
                    if (value) {
                        visibility = Ext.Array.unique(Ext.Array.push(visibility, node.get('objecttype')));
                    } else {
                        visibility = Ext.Array.unique(Ext.Array.remove(visibility, node.get('objecttype')));
                    }
                    vm.get('theGeoAttribute').set('visibility', visibility);
                }
            }
        },
        columns: [{
            xtype: 'treecolumn',
            dataIndex: 'text',
            flex: 1,
            readOnly: true
        }]
    }]
});