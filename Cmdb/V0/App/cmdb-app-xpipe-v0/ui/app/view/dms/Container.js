Ext.define('CMDBuildUI.view.dms.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.dms.ContainerController',
        'CMDBuildUI.view.dms.ContainerModel'
    ],
    alias: 'widget.dms-container',
    controller: 'dms-container',
    viewModel: {
        type: 'dms-container'
    },
    reference: 'dms-container',
    config: {

        //Needed configuration at initialization
        objectType: {
            evented: true,
            $value: ''
        },

        //Needed configuration at initialization
        objectTypeName: {
            evented: true,
            $value: ''
        },

        //Needed configuration at initialization
        objectId: {
            evented: true,
            $value: null
        },

        //calculated starting from objectType & objectTypeName
        DMSCategoryTypeName: {
            evented: true,
            $value: null
        },

        //calculated starting from DMSCategoryTypeName. Has the '.values()' store already loaded
        DMSCategoryType: {
            evented: true,
            $value: null
        },
        readOnly: undefined,
        ignoreSchedules: false,

        /**
         * @cfg {Boolean} isAsyncSave 
         * `true` if saving is managed by other components
         */
        isAsyncSave: false
    },
    publishes: [
        'objectType',
        'objectTypeName',
        'objectId',
        'DMSCategoryTypeName',
        'DMSCategoryType',
        'readOnly',
        'ignoreSchedules',
        'isAsyncSave'
    ],

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    scrollable: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.attachments.add,
        localized: {
            text: 'CMDBuildUI.locales.Locales.attachments.add'
        },
        iconCls: 'x-fa fa-plus',
        ui: 'management-action-small',
        DMSModelName: undefined,
        reference: 'attachmentsButton',
        publishes: [
            'hidden'
        ],
        menu: undefined,
        hidden: true,
        disabled: true,
        bind: {
            hidden: '{dms-container.readOnly}'
        },
        handler: 'onAttachmentsButtonClick',
        listeners: {
            show: function (button) {
                button.publishState();
            },
            hide: function (button) {
                button.publishState();
            }
        },
        autoEl: {
            'data-testid': 'dms-container-addbtn'
        }
    }, {
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText: CMDBuildUI.locales.Locales.common.actions.searchtext,
        itemId: 'dmssearchtext',
        cls: 'management-input',
        disabled: true,
        autoEl: {
            'data-testid': 'dms-container-searchtext'
        },
        bind: {
            disabled: '{disableRemoteActions}'
        },
        triggers: {
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                handler: function (field, trigger, eOpts) {
                    field.fireEvent('searchsubmit', field, field.getValue(), eOpts);
                }
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: function (field, trigger, eOpts) {
                    field.fireEvent('clearsearch', field, eOpts);
                }
            }
        },
        localized: {
            emptyText: "CMDBuildUI.locales.Locales.common.actions.searchtext"
        }
    }, {
        xtype: 'filters-launcher',
        storeName: 'attachments',
        reference: 'filterslauncher',
        disabled: true,
        showAttributesPanel: false,
        showRelationsPanel: false,
        isDms: true,
        bind: {
            disabled: '{disableRemoteActions}'
        }
    }, {
        xtype: 'button',
        itemId: 'dmsrefreshbtn',
        iconCls: 'x-fa fa-refresh',
        ui: 'management-action',
        tooltip: CMDBuildUI.locales.Locales.common.actions.refresh,
        disabled: true,
        autoEl: {
            'data-testid': 'dms-container-refreshbtn'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.common.actions.refresh'
        },
        bind: {
            disabled: '{disableRemoteActions}'
        }
    }, {
        xtype: 'button',
        itemId: 'dmscontextmenubtn',
        iconCls: 'x-fa fa-bars',
        ui: 'management-action',
        tooltip: CMDBuildUI.locales.Locales.common.grid.opencontextualmenu,
        arrowVisible: false,
        autoEl: {
            'data-testid': 'dms-container-contextmenubtn'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.common.grid.opencontextualmenu'
        },
        menu: [{
            iconCls: 'x-fa fa-square-o',
            itemId: 'dmscontextmenumultiselection',
            text: CMDBuildUI.locales.Locales.common.grid.enamblemultiselection,
            bind: {
                disabled: '{enableExtendedGrid}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.grid.enamblemultiselection'
            }
        }, {
            xtype: 'menuseparator'
        }, {
            iconCls: 'x-fa fa-trash',
            itemId: 'dmscontextmenudelete',
            text: CMDBuildUI.locales.Locales.bulkactions.delete,
            hidden: true,
            bind: {
                hidden: '{dms-container.readOnly}',
                disabled: '{disabledbulkactions || enableExtendedGrid}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.bulkactions.delete'
            }
        }, {
            iconCls: 'x-fa fa-download',
            itemId: 'dmscontextmenudownload',
            text: CMDBuildUI.locales.Locales.bulkactions.download,
            bind: {
                disabled: '{disabledbulkactions || enableExtendedGrid}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.bulkactions.download'
            }
        }, {
            iconCls: 'x-fa fa-download',
            itemId: 'dmscontextmenudownloadall',
            text: CMDBuildUI.locales.Locales.bulkactions.downloadall,
            bind: {
                disabled: '{enableExtendedGrid}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.bulkactions.downloadall'
            }
        }]
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'checkboxfield',
        fieldLabel: CMDBuildUI.locales.Locales.relations.extendeddata,
        itemId: 'showextendedfield',
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.relations.extendeddata'
        }
    }]
});