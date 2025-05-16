Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.domains.grids.ViewDomains', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.classes.tabitems.domains.grids.ViewDomainsController'
    ],

    alias: 'widget.administration-content-classes-tabitems-domains-grids-viewdomains',
    controller: 'administration-content-classes-tabitems-domains-grids-viewdomains',
    viewModel: {},
    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true,
        selected: null
    },
    autoScroll: true,
    forceFit: true,
    loadMask: true,
    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',
        expandOnDblClick: true,
        selectRowOnExpand: true,
        widget: {
            xtype: 'administration-content-domains-tabitems-properties-properties',
            ui: 'administration-tabandtools',
            // layout: 'fit',
            // paddingBottom: 10,
            // removeWidgetOnCollapse: true,
            autoScroll: true,
            // heigth: '100%',
            viewModel: {
                type: 'administration-content-domains-view'
            },
            bind: {
                theDomain: '{record}'
            }
        }
    }],

    autoEl: {
        'data-testid': 'administration-content-classes-tabitems-domains-grid'
    },



    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    labelWidth: "auto",


    bind: {
        hidden: '{!actions.view}'
    },
    columns: [{
        text: CMDBuildUI.locales.Locales.administration.common.labels.name,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
        },
        dataIndex: 'name',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
        },
        dataIndex: 'description',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin'
        },
        dataIndex: 'source',
        align: 'left',
        renderer: function (value, cell, record) {
            if (value) {
                var storeId = record.get('sourceProcess') ? 'processes.Processes' : 'classes.Classes';
                var sourceRecord = Ext.getStore(storeId).getById(record.get('source'));
                return sourceRecord && sourceRecord.get('description');
            }
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
        },
        dataIndex: 'destination',
        align: 'left',
        renderer: function (value, cell, record) {
            if (value) {
                var storeId = record.get('destinationProcess') ? 'processes.Processes' : 'classes.Classes';
                var sourceRecord = Ext.getStore(storeId).getById(record.get('destination'));
                return sourceRecord && sourceRecord.get('description');
            }
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription'
        },
        dataIndex: 'descriptionDirect',
        align: 'left'
    }, {
        text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription',
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription'
        },
        dataIndex: 'descriptionInverse',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality'
        },
        dataIndex: 'cardinality',
        align: 'left'
    }, {
        xtype: 'checkcolumn',
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetailshort,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetailshort'
        },
        disabled: true,        
        dataIndex: 'isMasterDetail',
        align: 'center'
    }, {
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdetail,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdetail'
        },
        dataIndex: 'descriptionMasterDetail',
        align: 'left'
    }, {
        xtype: 'checkcolumn',
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origininline,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origininline'
        },
        hidden: true,
        disabled: true,
        dataIndex: 'sourceInline',
        align: 'center'
    }, {
        xtype: 'checkcolumn',
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destinationinline,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destinationinline'
        },
        hidden: true,
        disabled: true,
        dataIndex: 'destinationInline',
        align: 'center'
    }, {
        xtype: 'checkcolumn',
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.link,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.link'
        },
        dataIndex: 'link',
        align: 'center',
        bind: {
            disabled: '{actions.view}'
        }
    }]
});