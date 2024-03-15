Ext.define('CMDBuildUI.view.classes.cards.clonerelations.Container', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.classes.cards.clonerelations.ContainerController',
        'CMDBuildUI.view.classes.cards.clonerelations.ContainerModel'
    ],

    alias: 'widget.classes-cards-clonerelations-container',
    controller: 'classes-cards-clonerelations-container',
    viewModel: {
        type: 'classes-cards-clonerelations-container'
    },
    layout: 'fit',
    items: [{
        xtype: 'form',
        scrollable: 'y',
        items: [{
            xtype: 'classes-cards-card-create',
            itemId: 'classes-cards-card-create',
            padding: 10,
            cloneObject: true,
            tab: CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations,
            tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations,
            buttons: null
        },
        {
            ui: 'formpagination',
            xtype: "formpaginationfieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.common.tabs.clonerelationmode,
            localized: {
                title: 'CMDBuildUI.locales.Locales.common.tabs.clonerelationmode'
            },
            items: [{
                xtype: 'grid',
                itemId: 'classes-cards-clonerelations-panel',
                layout: 'fit',
                forceFit: true,
                columns: [{
                    text: CMDBuildUI.locales.Locales.filters.domain,
                    dataIndex: 'description',
                    align: 'left',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.filters.domain'
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.filters.actions,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.filters.actions'
                    },
                    columns: [{
                        xtype: 'checkcolumn',
                        text: CMDBuildUI.locales.Locales.filters.ignore,
                        dataIndex: 'ignore',

                        localized: {
                            text: 'CMDBuildUI.locales.Locales.filters.ignore'
                        },
                        listeners: {
                            checkchange: function (column, rowindex, checked, record) {
                                var ignoreValue = record.get('ignore');
                                var mode;
                                if (checked && !ignoreValue) {
                                    mode = CMDBuildUI.util.helper.FiltersHelper.cloneFilters.ignore;
                                    record.set('ignore', checked);
                                    record.set('migrates', !checked);
                                    record.set('clone', !checked);
                                    record.set("mode", mode);
                                }
                            }
                        }
                    }, {
                        xtype: 'checkcolumn',
                        text: CMDBuildUI.locales.Locales.filters.migrate,
                        dataIndex: 'migrates',
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.filters.migrate'
                        },
                        listeners: {
                            checkchange: function (column, rowindex, checked, record) {
                                var migratesValue = record.get('migrates');
                                var mode;
                                if (checked && !migratesValue) {
                                    mode = CMDBuildUI.util.helper.FiltersHelper.cloneFilters.migrates;
                                    record.set('ignore', !checked);
                                    record.set('migrates', checked);
                                    record.set('clone', !checked);
                                    record.set("mode", mode);
                                }
                            }
                        }
                    }, {
                        xtype: 'checkcolumn',
                        text: CMDBuildUI.locales.Locales.filters.clone,
                        dataIndex: 'clone',
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.filters.clone'
                        },
                        renderer: function (value, cell, record) {
                            if (record.get('isDisabled')) {
                                cell.tdCls = 'x-item-disabled';
                            }
                            if (Ext.isEmpty(value)) {
                                return Ext.String.format("<span class=\"x-grid-checkcolumn\"></span>");
                            }
                            var klass = '';
                            if (value) {
                                klass = 'x-grid-checkcolumn-checked';
                            }
                            return Ext.String.format("<span class=\"{0} x-grid-checkcolumn \"></span>", klass);
                        },
                        listeners: {
                            beforecheckchange: function (column, rowIndex, checked, record) {
                                if (record.get('isDisabled')) {
                                    return false;
                                }
                            },

                            checkchange: function (column, rowindex, checked, record) {

                                var cloneValue = record.get('clone');
                                var mode;
                                if (checked && !cloneValue) {
                                    mode = CMDBuildUI.util.helper.FiltersHelper.cloneFilters.clone;
                                    record.set('ignore', !checked);
                                    record.set('migrates', checked);
                                    record.set('clone', !checked);
                                    record.set("mode", mode);
                                }
                            }
                        }

                    }]
                }
                ],

                bind: {
                    store: '{relations}'
                }

            }]
        }
        ],

        fbar: [{
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.common.actions.save,
            disabled: true,
            bind: {
                disabled: '{saveButtonDisabled}'
            },
            itemId: 'savebtn',
            ui: 'management-action-small',
            autoEl: {
                'data-testid': 'card-create-save'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.save'
            }
        }, {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.common.actions.saveandclose,
            disabled: true,
            bind: {
                disabled: '{saveButtonDisabled}'
            },
            ui: 'management-action-small',
            itemId: 'saveandclosebtn',
            autoEl: {
                'data-testid': 'card-create-saveandclose'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.saveandclose'
            }
        }, {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.common.actions.cancel,
            itemId: 'cancelbtn',
            ui: 'secondary-action-small',
            autoEl: {
                'data-testid': 'card-create-cancel'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
            }
        }]
    }]
});