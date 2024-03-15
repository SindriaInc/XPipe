Ext.define('CMDBuildUI.view.dms.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-grid',

    control: {
        '#': {
            afterrender: 'onAfterRender',
            rowdblclick: 'onRowDblClick',
            columnhide: 'onColumnVisibility',
            columnshow: 'onColumnVisibility'
        }
    },

    onAfterRender: function (view, eOpts) {
        view.getSelectionModel().excludeToggleOnColumn = [view.getVisibleColumns().length - 1];
    },

    /**
     * 
     * @param {Ext.grid.header.Container} header 
     * @param {Ext.grid.column.Column} column 
     * @param {Object} eOpts 
     */
    onColumnVisibility: function (header, column, eOpts) {
        var grid = header.ownerCt,
            excludeToggleOnColumn = [];

        Ext.Array.forEach(grid.getVisibleColumns(), function (item, index, allitems) {
            if (item.dataIndex === "_checkAttachment" || item.text === CMDBuildUI.locales.Locales.attachments.preview) {
                excludeToggleOnColumn.push(index);
            }
        });

        // update exclude toggle columns
        grid.getSelectionModel().excludeToggleOnColumn = excludeToggleOnColumn;
    },

    /**
     * @param {Ext.selection.RowModel} element
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {HTMLElement} rowIndex
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRowDblClick: function (element, record, rowIndex, e, eOpts) {
        var gridContainer = this.getView().up("dms-container"),
            title = CMDBuildUI.locales.Locales.attachments.editattachment + ' ' + record.get('_category_description_translation'),
            vm = this.getViewModel(),
            attachmentsStore = gridContainer.lookupViewModel().get('attachments'),
            attachmentName = record.get("name"),
            invalidFileNames = Ext.Array.remove(attachmentsStore.collect("name"), attachmentName),
            theObject;

        if (vm.get("dms-container.readOnly") || !vm.get("basepermissions.edit") || !record.get('_can_update')) {
            return false;
        }

        if (record.phantom || record.dirty) {
            theObject = record;
        }

        CMDBuildUI.util.Utilities.openPopup('popup-edit-attachment-form', title, {
            xtype: 'dms-attachment-edit',
            // DMSModelClass: model,
            objectType: gridContainer.getObjectType(),
            objectTypeName: gridContainer.getObjectTypeName(),
            objectId: gridContainer.getObjectId(),
            attachmentId: record.getId(),
            DMSCategoryTypeName: gridContainer.getDMSCategoryTypeName(),
            DMSCategoryValue: record.get('category'),
            ignoreSchedules: gridContainer.getIgnoreSchedules(),
            theObject: theObject,
            asyncStore: vm.get('dms-container.isAsyncSave') ? attachmentsStore : null,
            invalidFileNames: invalidFileNames,
            currentFileName: attachmentName
        }, {
            popupsave: {
                fn: function () {
                    gridContainer.getViewModel().getStore('attachments').load();
                },
                scope: this
            },
            popupcancel: function () { }
        });
    }
});