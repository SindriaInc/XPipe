// localized: ok
Ext.define('CMDBuildUI.view.administration.components.attributes.grid.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget',
        'CMDBuildUI.view.administration.components.attributes.grid.GridModel',
        'CMDBuildUI.view.administration.components.attributes.grid.GridController'
    ],
    controller: 'administration-components-attributes-grid-grid',
    viewModel: {
        type: 'administration-components-attributes-grid-grid'
    },

    alias: 'widget.administration-components-attributes-grid-grid',
    bind: {
        store: '{allAttributes}',
        selection: '{selected}'
    },

    itemId: 'attributeGrid',
    gridColumns: [{
        text: CMDBuildUI.locales.Locales.administration.attributes.texts.name,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.name'
        },
        dataIndex: 'name',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.attributes.texts.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.description'
        },
        dataIndex: 'description',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.attributes.texts.type,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.type'
        },
        dataIndex: '_type_description',
        align: 'left'
    }, {
        dataIndex: 'showInGrid',
        align: 'center',
        xtype: 'checkcolumn',
        renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
        bind: {
            text: '{showInGridText}'
        },
        listeners: {
            beforecheckchange: function () {
                return false;
            }
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid'
        },
        dataIndex: 'showInReducedGrid',
        xtype: 'checkcolumn',
        renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
        listeners: {
            beforecheckchange: function () {
                return false;
            }
        },
        hidden: true
    }, {
        text: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.excludefromgrid,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.excludefromgrid'
        },
        dataIndex: 'hideInGrid',
        xtype: 'checkcolumn',
        renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
        listeners: {
            beforecheckchange: function () {
                return false;
            }
        },
        hidden: true
    }, {
        text: CMDBuildUI.locales.Locales.administration.attributes.texts.unique,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.unique'
        },
        dataIndex: 'unique',
        align: 'center',
        xtype: 'checkcolumn',
        renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
        listeners: {
            beforecheckchange: function () {
                return false;
            }
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.attributes.texts.mandatory,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.mandatory'
        },
        dataIndex: 'mandatory',
        align: 'center',
        xtype: 'checkcolumn',
        renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
        listeners: {
            beforecheckchange: function () {
                return false;
            }
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.attributes.texts.active,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.active'
        },
        dataIndex: 'active',
        align: 'center',
        xtype: 'checkcolumn',
        renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
        listeners: {
            beforecheckchange: function () {
                return false;
            }
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.attributes.texts.editingmode,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.editingmode'
        },
        dataIndex: 'mode',
        align: 'left',
        renderer: CMDBuildUI.util.administration.helper.RendererHelper.getAttributeMode

    }, {
        text: CMDBuildUI.locales.Locales.administration.attributes.texts.grouping,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.grouping'
        },
        dataIndex: '_group_description',
        align: 'left',
        bind: {
            hidden: '{isOtherPropertiesHidden}'
        }
    }],
    getGridColumns: function () {
        return this.gridColumns;
    },
    bufferedRenderer: false,
    reserveScrollbar: true,
    listeners: {
        beforeitemdblclick: function (row, record) {
            var formInRow = row.ownerGrid.getPlugin('administration-forminrowwidget');
            formInRow.removeAllExpanded(record);
            row.setSelection(record);
        },
        rowdblclick: function (row, record, element, rowIndex, e, eOpts) {
            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            var vm = row.grid.getViewModel();
            container.removeAll();
            var action = vm.get('toolAction._canAdd') ? CMDBuildUI.util.administration.helper.FormHelper.formActions.edit : CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
            container.add({
                xtype: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view ? 'administration-components-attributes-actionscontainers-view' : 'administration-components-attributes-actionscontainers-create',
                viewModel: {
                    data: {
                        theAttribute: record,
                        objectTypeName: vm.get('objectTypeName'),
                        objectType: vm.get('objectType'),
                        attributeName: record.get('name'),
                        attributes: row.grid.getStore().getRange(),
                        title: Ext.String.format(
                            '{0} - {1} {2}',
                            record.get('objectType'),
                            CMDBuildUI.locales.Locales.administration.attributes.attribute,
                            (record.get('name')) ? '- ' + record.get('name') : ''
                        ),
                        grid: Ext.copy(row.grid),
                        action: action,
                        actions: {
                            edit: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                            view: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                            add: false
                        }
                    }
                }
            });
        }
    },



    viewConfig: {
        plugins: [{
            ptype: 'gridviewdragdrop',
            dragText: CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop,
            // TODO: localized not work as expected
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
        widget: {
            xtype: 'administration-components-attributes-actionscontainers-viewinrow',
            ui: 'administration-tabandtools',
            viewModel: {},
            bind: {
                pluralObjectType: '{pluralObjectType}',
                theAttribute: '{selected}'
            },
            autoEl: {
                "data-testid": "administration-components-attributes-actionscontainers-viewinrow"
            }
        }
    }],

    autoEl: {
        'data-testid': 'administration-components-attributes-grid'
    },

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true,
        selected: null
    },

    forceFit: true,
    loadMask: true,

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    labelWidth: "auto",
    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.attributes.texts.addattribute,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.addattribute'
        },
        reference: 'addattribute',
        itemId: 'addattribute',
        iconCls: 'x-fa fa-plus',
        ui: 'administration-action',

        bind: {
            disabled: '{!toolAction._canAdd}',
            hidden: '{newButtonHidden}'
        }
    }, {
        xtype: 'localsearchfield',
        gridItemId: '#attributeGrid'
    },
    // {
    //     xtype: 'textfield',
    //     name: 'search',
    //     width: 250,

    //     emptyText: CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search,
    //     localized: {
    //         emptyText: 'CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search'
    //     },
    //     reference: 'searchtext',
    //     itemId: 'searchtext',
    //     cls: 'administration-input',
    //     bind: {
    //         hidden: '{!canFilter}'
    //     },
    //     listeners: {
    //         specialkey: 'onSearchSpecialKey',
    //         change: 'onSearchSubmit'
    //     },
    //     autoEl: {
    //         'data-testid': 'administration-attributes-search-input'
    //     },
    //     triggers: {
    //         search: {
    //             cls: Ext.baseCSSPrefix + 'form-search-trigger',
    //             handler: 'onSearchSubmit',
    //             autoEl: {
    //                 'data-testid': 'administration-attributes-search-input-submitBtn'
    //             }
    //         },
    //         clear: {
    //             cls: Ext.baseCSSPrefix + 'form-clear-trigger',
    //             handler: 'onSearchClear',
    //             autoEl: {
    //                 'data-testid': 'administration-attributes-search-input-clearBtn'
    //             }
    //         }
    //     }
    // },
    {
        xtype: 'tbfill'
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
        hidden: true,
        bind: {
            hidden: '{isSimpleClass}',
            value: '{includeInherited}'
        },
        listeners: {
            change: 'onIncludeInheritedChange'
        }
    }
    ]
});