Ext.define('CMDBuildUI.view.events.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.events.ContainerController',
        'CMDBuildUI.view.events.ContainerModel'
    ],
    alias: 'widget.events-container',
    controller: 'events-container',
    viewModel: {
        type: 'events-container'
    },

    mixins: [
        'CMDBuildUI.mixins.grids.ContextMenuMixin'
    ],

    statics: {
        grid: 'grid',
        calendar: 'calendar'
    },
    reference: 'events-container',
    config: {
        schedules: null,
        selectedId: null,
        maingrid: true
    },
    publishes: [
        'schedules',
        'selectedId',
        'maingrid'
    ],

    layout: 'card',

    title: {
        xtype: "management-title",
        bind: {
            text: '{title}',
            objectTypeName: '{objectTypeName}',
            menuType: '{menuType}'
        }
    },

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [
            {
                xtype: 'button',
                ui: 'management-action',
                text: CMDBuildUI.locales.Locales.calendar.add,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.calendar.add'
                },
                iconCls: 'x-fa fa-plus',
                itemId: 'addevent',
                autoEl: {
                    'data-testid': 'events-container-addevent'
                }
            }, {
                xtype: 'textfield',
                name: 'search',
                width: 250,
                emptyText: CMDBuildUI.locales.Locales.common.actions.searchtext,
                itemId: 'searchtext',
                cls: 'management-input',
                autoEl: {
                    'data-testid': 'events-container-searchtext'
                },
                bind: {
                    value: '{search.value}'
                },
                listeners: {
                    specialkey: 'onSearchSpecialKey'
                },
                triggers: {
                    search: {
                        cls: Ext.baseCSSPrefix + 'form-search-trigger',
                        handler: 'onSearchSubmit'
                    },
                    clear: {
                        cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                        handler: 'onSearchClear'
                    }
                },
                localized: {
                    emptyText: "CMDBuildUI.locales.Locales.common.actions.searchtext"
                }
            }, {
                xtype: 'filters-launcher',
                storeName: 'events-container.schedules',
                showRelationsPanel: false,
                itemId: 'filterslauncher', //TODO: controller handler
                reference: 'filterslauncher',
                bind: {
                    selected: '{defaultfilter}'
                },
                autoEl: {
                    'data-testid': 'events-container-filterslauncheri'
                }
            }, {
                xtype: 'button',
                itemId: 'refreshBtn', //TODO: controller handler
                iconCls: 'x-fa fa-refresh',
                ui: 'management-action',
                tooltip: CMDBuildUI.locales.Locales.common.actions.refresh,
                autoEl: {
                    'data-testid': 'events-container-refreshbtn'
                },
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.common.actions.refresh'
                },
                handler: 'onRefreshBtnClick'
            }, {
                xtype: 'button',
                itemId: 'contextMenuBtn',
                reference: 'contextMenuBtn',
                iconCls: 'x-fa fa-bars',
                ui: 'management-action',
                tooltip: CMDBuildUI.locales.Locales.common.grid.opencontextualmenu,
                arrowVisible: false,
                autoEl: {
                    'data-testid': 'events-container-contextmenubtn'
                },
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.common.grid.opencontextualmenu'
                }
            }, {
                xtype: 'button',
                ui: 'management-action',
                iconCls: 'x-fa fa-calendar',
                bind: {
                    text: '{buttonText}'
                },
                handler: function (button, eOpts) {
                    var vm = button.lookupViewModel();
                    switch (vm.get('activeview')) {
                        case CMDBuildUI.view.events.Container.grid:
                            vm.set('activeview', CMDBuildUI.view.events.Container.calendar);
                            break;
                        case CMDBuildUI.view.events.Container.calendar:
                            vm.set('activeview', CMDBuildUI.view.events.Container.grid);
                            break;
                    }
                }
            }, CMDBuildUI.util.helper.GridHelper.getPrintButtonConfig(),
            {
                xtype: 'tbfill'
            },
            CMDBuildUI.util.helper.GridHelper.getBufferedGridCounterConfig("events-container.schedules"),
            CMDBuildUI.util.helper.GridHelper.getSaveGridPreferencesTool()]
    }, {
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            xtype: 'combobox',
            displayField: 'text',
            valueField: 'code',
            itemId: 'statuscombo',
            cls: 'management-input',
            width: 200,
            emptyText: CMDBuildUI.locales.Locales.calendar.active_expired,
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.calendar.active_expired'
            },
            bind: {
                store: '{statuscombostore}',
                value: '{statuscombo.value}'
            },
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function (combo, trigger, eOpts) {
                        combo.setValue(null);
                    }
                }
            }
        }, {
            xtype: 'combobox',
            displayField: 'text',
            valueField: 'code',
            itemId: 'categorycombo',
            cls: 'management-input',
            emptyText: CMDBuildUI.locales.Locales.calendar.allcategories,
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.calendar.allcategories'
            },
            width: 200,
            bind: {
                store: '{categorycombostore}',
                value: '{categorycombo.value}'
            },
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function (combo, trigger, eOpts) {
                        combo.setValue(null);
                    }
                }
            }
        }, {
            xtype: 'combobox',
            displayField: 'label',
            valueField: 'value',
            itemId: 'datecombo',
            cls: 'management-input',
            width: 200,
            emptyText: CMDBuildUI.locales.Locales.calendar.alldates,
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.calendar.alldates'
            },
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function (combo, trigger, eOpts) {
                        combo.setValue(null);
                    }
                }
            },
            hidden: true,
            bind: {
                store: '{datecombostore}',
                value: '{datecombo.value}',
                hidden: '{datecomboHidden}'
            }
        }]
    }],
    bind: {
        activeItem: '{activeview}'
    },

    items: [{
        itemId: 'grid', //don't change  CMDBuildUI.view.events.Container.grid
        xtype: 'events-grid',
        bind: {
            eventsStore: '{events-container.schedules}',
            selectedId: '{events-container.selectedId}',
            maingrid: '{events-container.maingrid}'
        },
        selModel: {
            pruneRemoved: false, // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
            type: 'checkboxmodel',
            checkOnly: true,
            mode: 'SINGLE'
        },
        maingrid: true
    }, {
        itemId: 'calendar', //dont' change  CMDBuildUI.view.events.Container.calendar
        xtype: 'events-calendar'
    }],

    /**
     * Returns the grid on which apply context menu actions.
     * 
     * @override
     * @return {Ext.gid.Panel}
     */
    getContextMenuGrid: function () {
        return this.down(this.referenceGridId);
    },

    updateSelectedId: function (selectedId) {
    },

    privates: {
        /**
         * @property referenceGridId
         */
        referenceGridId: 'events-grid'
    }

});