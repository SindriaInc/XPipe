Ext.define('CMDBuildUI.view.administration.content.tasks.Grid', {
    extend: 'Ext.grid.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.tasks.GridController',
        'CMDBuildUI.view.administration.content.tasks.GridModel',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],

    alias: 'widget.administration-content-tasks-grid',
    controller: 'administration-content-tasks-grid',
    viewModel: {
        type: 'administration-content-tasks-grid'
    },

    bind: {
        store: '{gridDataStore}',
        selection: '{selected}'
    },
    itemId: 'taskGrid',
    reserveScrollbar: true,
    initComponent: function () {
        Ext.getStore('importexports.Gates');
        this.callParent(arguments);
    },
    columns: [{
        text: CMDBuildUI.locales.Locales.administration.common.labels.type, // Type
        dataIndex: 'type',
        align: 'left',
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.type' // Type
        },
        renderer: function (value, cell, record) {
            // workaround after patch "3.3.0-35"
            if (!record.get('config').tag && record.get('config').gate) {
                var gateStore = Ext.getStore('importexports.Gates');
                var gate = gateStore.findRecord('code', record.get('config').gate);
                if (gate) {
                    record.get('config').gateconfig_handlers_0_type = gate.get('_handler_type');
                    record.get('config').gateconfig_handlers_0_gate = gate.get('code');
                    record.get('config').tag = gate.get('_handler_type');
                }
            }
            var types = CMDBuildUI.model.tasks.Task.getTypes();
            var type = Ext.Array.findBy(types, function (item) {
                if (value === 'etl') {
                    return item.subType === record.get('config').tag;
                }
                return item.value === value;
            });
            return type && type.label || value;
        },
        bind: {
            hidden: '{taskType}'
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.code, // Code
        dataIndex: 'code',
        align: 'left',
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.code' // Code
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.description, // Description
        dataIndex: 'description',
        align: 'left',
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.description' // Description
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.active,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
        },
        xtype: 'actioncolumn',
        align: 'center',
        hideable: false,
        items: [{
            iconCls: 'tasks-grid-action x-fa fa-square',
            getTip: function (value, metadata, record, row, col, store) {
                if (record.get('enabled')) {
                    return CMDBuildUI.locales.Locales.administration.tasks.tooltips.execution;
                }
                return CMDBuildUI.locales.Locales.administration.tasks.tooltips.stopped;
            },
            handler: function (grid, rowIndex, colIndex) { },
            bind: {
                disabled: '{taskType.isSyncronous || !toolAction._canAdd}'
            },
            getClass: function (v, meta, record) {
                if (record.get('enabled')) {
                    return 'tasks-grid-action-play enabled x-fa fa-square';
                }
                return 'tasks-grid-action-disabled x-fa fa-square';
            }
        }]
    }, {
        xtype: 'actioncolumn',
        align: 'center',
        cellFocusable: false,
        minWidth: 100, // width property not works. Use minWidth.
        hideable: false,
        items: [{
            iconCls: 'tasks-grid-action x-fa fa-play-circle-0',
            getTip: function () {
                return CMDBuildUI.locales.Locales.administration.tasks.tooltips.singleexecution;
            },
            handler: 'onRunBtnClick',
            bind: {
                disabled: '{taskType.isSyncronous || theSession.rolePrivileges.admin_jobs_modify}'
            },
            getClass: function (v, meta, record, rowIndex, colIndex, e, view) {
                if (record.get('enabled') || !view.lookupViewModel().get('theSession.rolePrivileges.admin_jobs_modify')) {
                    return 'tasks-grid-action-play disabled x-fa fa-play-circle-o';
                }
                return 'tasks-grid-action-play enabled x-fa fa-play-circle-o';
            }
        }, '->', {
            getTip: function () {
                return CMDBuildUI.locales.Locales.administration.tasks.tooltips.start;
            },
            handler: 'onStartStopBtnClick',
            isDisabled: function (view, rowIndex, colIndex, button, record) {
                return record.get('enabled') || !view.lookupViewModel().get('theSession.rolePrivileges.admin_jobs_modify');
            },
            getClass: function (v, meta, record, rowIndex, colIndex, e, view) {
                if (record.get('enabled') || !view.lookupViewModel().get('theSession.rolePrivileges.admin_jobs_modify')) {
                    return 'tasks-grid-action-play disabled x-fa fa-play';
                }
                return 'tasks-grid-action-play enabled x-fa fa-play';
            }
        }, {
            getTip: function () {
                return CMDBuildUI.locales.Locales.administration.tasks.tooltips.stop;
            },
            handler: 'onStartStopBtnClick',
            itemId: 'stopBtn',
            isDisabled: function (view, rowIndex, colIndex, button, record) {
                return !record.get('enabled') || !view.lookupViewModel().get('theSession.rolePrivileges.admin_jobs_modify');
            },
            getClass: function (v, meta, record, rowIndex, colIndex, e, view) {
                if (!record.get('enabled') || !view.lookupViewModel().get('theSession.rolePrivileges.admin_jobs_modify')) {
                    return 'tasks-grid-action-stop disabled cmdbuildicon-stop-circle';
                }
                return 'tasks-grid-action-stop enabled cmdbuildicon-stop-circle';
            }
        }]
    }],

    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',

        expandOnDblClick: false,
        widget: {
            xtype: 'administration-content-tasks-card-viewinrow',
            ui: 'administration-tabandtools',
            viewModel: {
                data: {
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            },
            bind: {
                theTask: '{selected}',
                type: '{type}',
                subType: '{subType}'
            }

        }
    }],

    autoEl: {
        'data-testid': 'administration-content-tasks-grid'
    },

    forceFit: true,
    loadMask: true,

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    labelWidth: "auto"
});