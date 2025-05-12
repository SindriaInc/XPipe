Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.domains.Classes', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.domains.tabitems.domains.ClassesController',
        'CMDBuildUI.view.administration.content.domains.tabitems.domains.DomainsModel'
    ],

    alias: 'widget.administration-content-domains-tabitems-domains-classes',
    controller: 'administration-content-domains-tabitems-domains-classes',
    viewModel: {
        type: 'administration-content-domains-tabitems-domains-classes'
    },
    heigth: '500',
    width: '250',
    layout: 'border',
    ui: 'administration-navigation-tree',
    items: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: {}
        }, 'domains-classes', 'theDomain')
    }, {
        xtype: 'treepanel',
        reference: 'originTree',
        split: true, // enable resizing
        region: 'west',
        layout: 'fit',
        width: '50%',
        viewConfig: {
            markDirty: false
        },    
        bind: {
            store: '{originStore}'
        },

        columns: [{
            xtype: 'treecolumn',
            text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin'
            },
            dataIndex: 'text',
            ui: 'administration',
            flex: 1,
            align: 'left',
            renderer: function (val, meta, rec) {
                if (rec.get('isLayover')) {
                    meta.tdStyle = 'color: gray; font-style: italic;';
                }
                return val;
            }
        }, {
            xtype: 'checkcolumn',
            width: 100,
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            bind: {
                disabled: '{originTreeDisabled}'
            },
            dataIndex: 'enabled',            
            renderer: function (value, cell, record, rowIndex, colIndex, store, view) {
                var config = this.defaultRenderer(value, cell);
                if (record.get('children') && record.get('children').length) {
                    return '';
                }
                return config;
            },
            listeners: {
                checkchange: function (column, recordIndex, checked) {
                    var theDomain = this.getView()
                        .lookupViewModel()
                        .get('theDomain');
                    var sourceDescendant = theDomain.get('disabledSourceDescendants');
                    var storeRecordName = this.getView().getStore().getAt(recordIndex).get('name');
                    var sourceDescendantIndex = sourceDescendant.indexOf(storeRecordName);
                    if (!checked && sourceDescendantIndex === -1) {
                        sourceDescendant.push(storeRecordName);
                    } else if (checked && sourceDescendantIndex > -1) {
                        sourceDescendant.splice(sourceDescendantIndex, 1);
                    }
                    theDomain.set('disabledSourceDescendants', sourceDescendant);
                }
            }
        }]
    }, {
        region: 'center', // center region is required, no width/height specified
        xtype: 'treepanel',
        reference: 'destinationTree',
        layout: 'fit',
        viewConfig: {
            markDirty: false
        },
        ui: 'administration-navigation-tree',
        bind: {
            store: '{destinationStore}'
        },
        columns: [{
            xtype: 'treecolumn',
            text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
            },
            dataIndex: 'text',
            align: 'left',
            flex: 1
        }, {
            xtype: 'checkcolumn',
            width: 100,
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            dataIndex: 'enabled',
            bind: {
                disabled: '{destinationTreeDisabled}'
            },            
            renderer: function (value, cell, record, rowIndex, colIndex, store, view) {
                var config = this.defaultRenderer(value, cell);
                if (record.get('children') && record.get('children').length) {
                    return;
                }
                return config;
            },
            listeners: {
                checkchange: function (column, recordIndex, checked) {
                    var theDomain = this.getView()
                        .lookupViewModel()
                        .get('theDomain');
                    var destinationDescendant = theDomain.get('disabledDestinationDescendants');
                    var storeRecordName = this.getView().getStore().getAt(recordIndex).get('name');
                    var destinationDescendantIndex = destinationDescendant.indexOf(storeRecordName);
                    if (!checked && destinationDescendantIndex === -1) {
                        destinationDescendant.push(storeRecordName);
                    } else if (checked && destinationDescendantIndex > -1) {
                        destinationDescendant.splice(destinationDescendantIndex, 1);
                    }
                    theDomain.set('disabledDestinationDescendants', destinationDescendant);
                }
            }
        }]
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
    }]
});