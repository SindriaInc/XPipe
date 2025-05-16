Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.domains.Domains', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.classes.tabitems.domains.DomainsController',
        'CMDBuildUI.view.administration.content.classes.tabitems.domains.DomainsModel'
    ],

    alias: 'widget.administration-content-classes-tabitems-domains-domains',
    controller: 'administration-content-classes-tabitems-domains-domains',
    viewModel: {
        type: 'administration-content-classes-tabitems-domains-domains'
    },
    itemId: 'classDomainsGrid',
    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true,
        selected: null
    },

    forceFit: true,
    loadMask: true,
    viewConfig: {
        plugins: [{
            ptype: 'gridviewdragdrop',
            dragText: CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop,
            localized: {
                dragText: 'CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop'
            },
            containerScroll: true,
            pluginId: 'gridviewdragdrop'
        }]
    },
    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',
        expandOnDblClick: true,
        selectRowOnExpand: true,
        widget: {
            xtype: 'administration-content-domains-tabitems-properties-properties',
            ui: 'administration-tabandtools',
            layout: 'fit',
            paddingBottom: 10,
            scrollable: false,
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
    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.domains.texts.adddomain,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.texts.adddomain'
        },
        ui: 'administration-action-small',
        reference: 'adddomain',
        itemId: 'adddomain',
        iconCls: 'x-fa fa-plus',
        autoEl: {
            'data-testid': 'administration-class-toolbar-addDomainBtn'
        },
        bind: {
            disabled: '{!toolAction._canAdd}'
        }
    }, {
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.domains.texts.addlink,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.texts.addlink'
        },
        ui: 'administration-action-small',
        reference: 'addlink',
        itemId: 'addlink',
        iconCls: 'x-fa fa-link',
        viewModel: {},
        // TODO: comment hidden for activate linkbutton
        hidden: true,
        bind: {

        },
        autoEl: {
            'data-testid': 'administration-class-toolbar-addLinkBtn'
        }
    }, {
        xtype: 'localsearchfield',
        gridItemId: '#classDomainsGrid'
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tool',
        align: 'right',
        itemId: 'editBtn',
        cls: 'administration-tool',
        iconCls: 'x-fa fa-pencil',
        viewModel: {},
        tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.editBtn.tooltip,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.editBtn.tooltip'
        },
        autoEl: {
            'data-testid': 'administration-class-domains-tool-editbtn'
        },
        // TODO: comment hidden for activate editbutton form link column in grid
        hidden: true
    }, {
        xtype: 'checkbox',
        fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.includeinherited,
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.includeinherited'
        },
        labelAlign: 'left',
        labelStyle: 'width:auto',
        labelWidth: false,
        value: true,
        bind: {
            value: '{includeInherited}'
        },
        listeners: {
            change: 'onIncludeInheritedChange'
        }
    }],


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
            hidden: true,
            disabled: true,
            dataIndex: 'isMasterDetail',
            align: 'center'
        }, {
            text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdetail,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdetail'
            },
            hidden: true,
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
        },
        //  {
        //     xtype: 'checkcolumn',
        //     text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.link,
        //     localized: {
        //         text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.link'
        //     },
        //     dataIndex: 'link',
        //     align: 'center',
        //     hidden: true,
        //     bind: {
        //         disabled: '{actions.view}'
        //     }
        // },
        {
            xtype: 'checkcolumn',
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            disabled: true,
            dataIndex: 'active',
            align: 'center',
            readOnly: true

        }
    ],


    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        // TODO: comment hidden for activate form buttons form link column in grid
        hidden: true,
        bind: {
            // hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
    }]
});