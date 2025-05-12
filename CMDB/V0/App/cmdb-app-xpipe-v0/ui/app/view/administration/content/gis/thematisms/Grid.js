Ext.define('CMDBuildUI.view.administration.content.gis.thematisms.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.thematisms.GridController',
        'CMDBuildUI.view.administration.content.gis.thematisms.GridModel'
    ],

    alias: 'widget.administration-content-gis-thematisms-grid',
    controller: 'administration-content-gis-thematisms-grid',
    viewModel: {
        type: 'administration-content-gis-thematisms-grid'
    },

    forceFit: true,
    loadMask: true,
    bind: {
        store: '{thematismsStore}'
    },

    columns: [{
        text: CMDBuildUI.locales.Locales.administration.common.labels.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
        },
        dataIndex: 'description',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.gis.owneruser,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.gis.owneruser'
        },
        dataIndex: 'user',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.gis.ownerclass,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.gis.ownerclass'
        },
        dataIndex: 'owner',
        align: 'left'
    }, {
        xtype: 'checkcolumn',
        text: CMDBuildUI.locales.Locales.administration.gis.global,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.gis.global'
        },
        dataIndex: 'global',
        bind: {
            disabled: '{actions.view}'
        },
        align: 'center'
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',

        items: [{
            xtype: 'textfield',
            name: 'search',
            width: 250,
            emptyText: CMDBuildUI.locales.Locales.administration.gis.searchemptytext,
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.administration.gis.searchemptytext'
            },
            cls: 'administration-input',
            reference: 'searchtext',
            itemId: 'searchtext',
            bind: {
                hidden: '{!canFilter}'
            },
            listeners: {
                specialkey: 'onSearchSpecialKey'
            },
            triggers: {
                search: {
                    cls: Ext.baseCSSPrefix + 'form-search-trigger',
                    handler: 'onSearchSubmit',
                    autoEl: {
                        'data-testid': 'administration-gis-thematism-search-trigger'
                    }
                },
                clear: {
                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                    handler: 'onSearchClear',
                    autoEl: {
                        'data-testid': 'administration-gis-thematism-search-clear-trigger'
                    }
                }
            },
            autoEl: {
                'data-testid': 'administration-gis-thematism-search-form'
            }
        }]
    }, {
        dock: 'top',
        xtype: 'container',
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            items: [{
                xtype: 'button',
                itemId: 'spacer',
                style: {
                    visibility: 'hidden'
                }
            }, {
                xtype: "tbfill"
            }, {
                xtype: 'tool',
                iconCls: 'x-fa fa-pencil',
                tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
                },
                callback: 'onEditBtnClick',
                cls: 'administration-tool',
                bind: {
                    disabled: "{!toolAction._canUpdate}"

                },
                autoEl: {
                    'data-testid': 'administration-thematisms-editBtn'
                }
            }]
            // CMDBuildUI.util.administration.helper.FormHelper.getTools({
            //     edit: true // #editBtn set true for show the button
            // },
            //     'thematisms'
            // )
        }]
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
    }],
    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.gis.thematisms);
        this.callParent(arguments);
    }
});