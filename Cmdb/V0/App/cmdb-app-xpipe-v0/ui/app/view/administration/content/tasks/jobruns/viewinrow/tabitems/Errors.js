Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.viewinrow.tabitems.Errors', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.administration-content-tasks-jobruns-viewinrow-tabitems-errors',
    bind: {
        store: '{errorsStore}'
    },

    headerBorders: false,
    border: false,
    bodyBorder: false,
    rowLines: false,
    sealedColumns: false,
    sortableColumns: false,
    enableColumnHide: false,
    enableColumnMove: false,
    enableColumnResize: false,
    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    viewConfig: {
        markDirty: false
    },

    columnWidth: 1,
    autoEl: {
        'data-testid': 'administration-content-tasks-descriptors-params-grid'
    },

    forceFit: true,
    loadMask: true,

    labelWidth: "auto",

    columns: [{

        text: CMDBuildUI.locales.Locales.administration.jobruns.level,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.jobruns.level'
        },
        dataIndex: 'level',
        align: 'left',
        renderer: function (value) {
            var output = '';
            var levels = CMDBuildUI.model.administration.JobRunner.getErrorLevels();
            Ext.Array.findBy(levels, function (level) {
                if (level.value.toLowerCase() === value) {
                    output = level.label;
                }
            });
            return output;
        }
    }, {
        flex: 1,
        text: CMDBuildUI.locales.Locales.administration.jobruns.message,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.jobruns.message'
        },
        dataIndex: 'message',
        align: 'left'
    }, {
        flex: 1,
        text: CMDBuildUI.locales.Locales.administration.jobruns.exception,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.jobruns.exception'
        },
        dataIndex: 'exception',
        align: 'left'
    }, {
        xtype: 'actioncolumn',
        minWidth: 54,
        align: 'center',
        items: [{
            iconCls: 'attachments-grid-action x-fa fa-eye',
            getTip: function () {
                return CMDBuildUI.locales.Locales.administration.jobruns.viewmessage;
            },
            handler: function (grid, rowIndex, colIndex, event, element, record) {
                var popup;
                var content = {
                    xtype: 'panel',
                    scrollable: true,
                    items: [{
                        xtype: 'textarea',
                        readOnly: true,
                        labelAlign: 'top',
                        columnWidth: 1,
                        height: '100%',
                        width: '100%',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.jobruns.exception,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.jobruns.exception'
                        },
                        value: record.get('exception')
                    }]
                };
                // custom panel listeners
                var listeners = {
                    /**
                     * @param {Ext.panel.Panel} panel
                     * @param {Object} eOpts
                     */
                    close: function (panel, eOpts) {
                        CMDBuildUI.util.Utilities.closePopup(popup.getId());
                    }
                };
                // create and open panel
                popup = CMDBuildUI.util.Utilities.openPopup(
                    null,
                    Ext.String.format('{0} {1}', CMDBuildUI.locales.Locales.administration.systemconfig[record.get('level').toLowerCase()], record.get('message')),
                    content,
                    listeners, {
                        ui: 'administration-actionpanel',
                        scrollable: true,
                        draggable: true
                    }
                );
            }
        }]
    }]
});