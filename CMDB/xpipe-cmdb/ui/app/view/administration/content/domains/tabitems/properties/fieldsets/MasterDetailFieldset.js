Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.MasterDetailFieldset', {
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.MasterDetailFieldsetController',
        'CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.MasterDetailFieldsetModel'
    ],

    alias: 'widget.administration-content-domains-tabitems-properties-fieldsets-masterdetailfieldset',

    controller: 'administration-content-domains-tabitems-properties-fieldsets-masterdetailfieldset',
    viewModel: {
        type: 'administration-content-domains-tabitems-properties-fieldsets-masterdetailfieldset'
    },
    layout: 'column',
    columnWidth: 1,
    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        columnWidth: 1,
        title: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetail,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetail'
        },
        itemId: 'domain-masterdetailfieldset',
        ui: 'administration-formpagination',
        hidden: true,
        bind: {
            hidden: '{ theDomain.cardinality === "1:1" || theDomain.cardinality === "N:N"}'
        },
        items: [{
            layout: 'column',
            columnWidth: 1,
            listeners: {
                hide: function (view) {
                    CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldset.onHideInlineOptions(view);
                }
            },
            items: [{
                columnWidth: 0.5,
                /********************* Master detail **********************/
                items: [{
                    // create / edit / view
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetail,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetail'
                    },
                    name: 'masterDetail',
                    hidden: true,
                    bind: {
                        value: '{theDomain.isMasterDetail}',
                        readOnly: '{actions.view}',
                        hidden: '{!theDomain}',
                        disabled: '{!isN1or1N}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                /********************* Master detail label **********************/
                items: [{
                    xtype: 'fieldcontainer',
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClickMasterDetail',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdataillong,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdataillong'
                    },
                    hidden: true,
                    bind: {
                        hidden: '{descriptionMasterDetailInput.hidden && descriptionMasterDetailDisplay.hidden}'
                    },
                    items: [{
                        // add / edit
                        xtype: 'textfield',
                        name: 'descriptionMasterDetail',
                        hidden: true,
                        bind: {
                            value: '{theDomain.descriptionMasterDetail}',
                            hidden: '{descriptionMasterDetailInput.hidden}'
                        }
                    }, {
                        // view
                        xtype: 'displayfield',
                        hidden: true,
                        bind: {
                            value: '{theDomain.descriptionMasterDetail}',
                            hidden: '{descriptionMasterDetailDisplay.hidden}'
                        }
                    }]
                }]
            }]
        }, {
            xtype: 'fieldcontainer',
            columnWidth: 1,
            fieldLabel: CMDBuildUI.locales.Locales.administration.domains.texts.showsummaryfor,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.texts.showsummaryfor'
            },
            bind: {
                hidden: '{!theDomain.isMasterDetail}'
            },
            items: [{
                xtype: 'container',
                layout: 'column',
                items: [{
                    xtype: 'grid',
                    headerBorders: false,
                    border: false,
                    bodyBorder: false,
                    rowLines: false,
                    sealedColumns: false,
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    enableColumnResize: false,
                    cls: 'administration-reorder-grid',
                    itemId: 'sumattributesGrid',
                    selModel: {
                        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                    },
                    controller: {

                    },
                    columnWidth: 0.5,
                    autoEl: {
                        'data-testid': 'administration-content-domain-sumattributes-grid'
                    },

                    forceFit: true,
                    loadMask: true,
                    labelWidth: "auto",
                    bind: {
                        store: '{masterDetailAggregateAttrsStore}'
                    },
                    columns: [{
                        flex: 1,
                        text: CMDBuildUI.locales.Locales.administration.attributes.attribute,
                        dataIndex: 'description',
                        align: 'left'
                    }, {
                        xtype: 'actioncolumn',
                        itemId: 'actionColumnCancel',
                        bind: {
                            hidden: '{actions.view}'
                        },
                        width: 30,
                        minWidth: 30, // width property not works. Use minWidth.
                        maxWidth: 30,
                        align: 'center',
                        items: [{
                            iconCls: 'x-fa fa-times',
                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                            },
                            handler: function (grid, rowIndex, colIndex) {
                                var vm = grid.lookupViewModel();
                                var store = vm.get('masterDetailAggregateAttrsStore');
                                var record = store.getAt(rowIndex);
                                store.remove(record);
                                var aggregateAttributes = vm.get('theDomain.masterDetailAggregateAttrs');
                                Ext.Array.remove(aggregateAttributes, record.get('name'));
                                vm.getStore('freeAttributeForAggregateStore').add(record);
                                vm.set('theDomain.masterDetailAggregateAttrs', aggregateAttributes);

                            },
                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                metadata.value = Ext.String.insert(metadata.value, Ext.String.format(' data-testid="administration-importexport-attribute-removeBtn-{0}"', rowIndex), -7);
                                if (record.get('editing')) {
                                    return 'x-fa fa-ellipsis-h';
                                }
                                return 'x-fa  fa-times';
                            }
                        }]
                    }]
                }]
            }, {
                xtype: 'container',
                layout: 'column',
                columnWidth: 1,

                items: [{
                    margin: '20 0 20 0',
                    xtype: 'grid',
                    headerBorders: false,
                    border: false,
                    bodyBorder: false,
                    rowLines: false,
                    sealedColumns: false,
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    enableColumnResize: false,
                    cls: 'administration-reorder-grid',
                    itemId: 'importExportAttributeGridNew',
                    selModel: {
                        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                    },
                    layout: 'hbox',
                    autoEl: {
                        'data-testid': 'administration-content-importexport-datatemplates-grid-newrecord'
                    },
                    columnWidth: 0.5,
                    forceFit: true,
                    loadMask: true,

                    labelWidth: "auto",
                    bind: {
                        store: '{newSelectedAttributesStore}',
                        hidden: '{actions.view}'
                    },

                    columns: [{
                        xtype: 'widgetcolumn',
                        dataIndex: 'name',
                        align: 'left',
                        flex: 1,
                        widget: {
                            xtype: 'combo',
                            queryMode: 'local',
                            typeAhead: true,
                            editable: true,
                            forceSelection: true,
                            emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts.selectanattribute,
                            itemId: 'selectAttributeForGrid',
                            displayField: 'description',
                            valueField: 'name',
                            bind: {
                                store: '{freeAttributeForAggregateStore}'
                            },
                            autoEl: {
                                'data-testid': 'administration-importexport-attribute-name'
                            },

                            listeners: {
                                focus: function () {
                                    var me = this;
                                    var vm = me.lookupViewModel();
                                    me.getStore().removeAll();
                                    var supportedAttributes = [CMDBuildUI.model.Attribute.types.integer, CMDBuildUI.model.Attribute.types.decimal, CMDBuildUI.model.Attribute.types.double];
                                    vm.get('allDetailAttributesStore').each(function (attribute) {
                                        var record = vm.get('masterDetailAggregateAttrsStore').getById(attribute.getId());
                                        if (!record && attribute.get('showInGrid') && supportedAttributes.indexOf(attribute.get('type')) >= 0) {
                                            me.getStore().add(attribute);
                                        }
                                    });
                                    return true;
                                }
                            },
                            height: 19,
                            minHeight: 19,
                            maxHeight: 19,
                            padding: 0,
                            ui: 'reordergrid-editor-combo'
                        }
                    }, {
                        xtype: 'actioncolumn',
                        itemId: 'actionColumnAddNew',
                        width: 30,
                        minWidth: 30, // width property not works. Use minWidth.
                        maxWidth: 30,
                        align: 'center',
                        items: [{
                            iconCls: 'x-fa fa-plus',
                            autoEl: {
                                'data-testid': 'administration-importexport-attribute-addBtn'
                            },
                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                return CMDBuildUI.locales.Locales.administration.common.actions.add;
                            },
                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                metadata.value = Ext.String.insert(metadata.value, ' data-testid="administration-importexport-attribute-addBtn"', -7);
                                return 'x-fa fa-plus';
                            },

                            handler: function (button, rowIndex, colIndex) {

                                var attributeName = button.up('panel').down('#selectAttributeForGrid');
                                if (Ext.isEmpty(attributeName.getValue())) {
                                    attributeName.focus();
                                    attributeName.expand();
                                    return false;
                                }
                                var vm = button.lookupViewModel();
                                var attributeStore = vm.getStore('freeAttributeForAggregateStore');
                                var record = vm.get('allDetailAttributesStore').getById(attributeName.getValue());
                                if (record) {
                                    vm.getStore('masterDetailAggregateAttrsStore').add(record);
                                    vm.get('theDomain.masterDetailAggregateAttrs').push(record.get('name'));
                                    attributeStore.remove(record);
                                }
                                var mainGrid = button.up('form').down('#sumattributesGrid');
                                attributeName.reset();
                                mainGrid.getView().refresh();
                            }
                        }]
                    }]
                }]
            }]

        }, {
            xtype: 'fieldcontainer',
            fieldLabel: CMDBuildUI.locales.Locales.administration.domains.texts.disabledcardattributes,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.texts.disabledcardattributes'
            },
            bind: {
                hidden: '{!theDomain.isMasterDetail}'
            },
            layout: 'column',
            columnWidth: 1,
            items: [{
                xtype: 'container',
                layout: 'column',
                columnWidth: 1,
                items: [{
                    xtype: 'grid',
                    headerBorders: false,
                    border: false,
                    bodyBorder: false,
                    rowLines: false,
                    sealedColumns: false,
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    enableColumnResize: false,
                    cls: 'administration-reorder-grid',
                    itemId: 'diabledattributesGrid',
                    selModel: {
                        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                    },
                    controller: {

                    },
                    columnWidth: 0.5,
                    autoEl: {
                        'data-testid': 'administration-content-domain-disabledattributes-grid'
                    },
                    layout: 'fit',
                    forceFit: true,
                    loadMask: true,
                    labelWidth: "auto",
                    bind: {
                        store: '{disabledAttributesStore}'
                    },
                    columns: [{
                        flex: 1,
                        text: CMDBuildUI.locales.Locales.administration.attributes.attribute,
                        dataIndex: 'description',
                        align: 'left'
                    }, {
                        xtype: 'actioncolumn',
                        itemId: 'actionColumnCancel',
                        bind: {
                            hidden: '{actions.view}'
                        },
                        width: 30,
                        minWidth: 30, // width property not works. Use minWidth.
                        maxWidth: 30,
                        align: 'center',
                        items: [{
                            iconCls: 'x-fa fa-times',
                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                            },
                            handler: function (grid, rowIndex, colIndex) {
                                var vm = grid.lookupViewModel();
                                var store = vm.get('disabledAttributesStore');
                                var record = store.getAt(rowIndex);
                                store.remove(record);
                                var aggregateAttributes = vm.get('theDomain.masterDetailDisabledCreateAttrs');
                                Ext.Array.remove(aggregateAttributes, record.get('name'));
                                vm.getStore('freeDisabledAttributeStore').add(record);
                                vm.set('theDomain.masterDetailDisabledCreateAttrs', aggregateAttributes);

                            },
                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                metadata.value = Ext.String.insert(metadata.value, Ext.String.format(' data-testid="administration-content-domain-disabledattributes-grid-removeBtn-{0}"', rowIndex), -7);
                                if (record.get('editing')) {
                                    return 'x-fa fa-ellipsis-h';
                                }
                                return 'x-fa  fa-times';
                            }
                        }]
                    }]
                }]
            }, {
                xtype: 'container',
                layout: 'column',
                columnWidth: 1,
                items: [{
                    margin: '20 0 20 0',
                    xtype: 'grid',
                    columnWidth: 0.5,
                    headerBorders: false,
                    border: false,
                    bodyBorder: false,
                    rowLines: false,
                    sealedColumns: false,
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    enableColumnResize: false,
                    cls: 'administration-reorder-grid',
                    itemId: 'diabledAttributeGridNew',
                    selModel: {
                        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                    },
                    layout: 'hbox',
                    autoEl: {
                        'data-testid': 'administration-content-domain-disabledattributes-grid-newrecord'
                    },
                    forceFit: true,
                    loadMask: true,

                    labelWidth: "auto",
                    bind: {
                        store: '{newDisabledAttributesStore}',
                        hidden: '{actions.view}'
                    },

                    columns: [{
                        xtype: 'widgetcolumn',
                        dataIndex: 'name',
                        align: 'left',
                        flex: 1,
                        widget: {
                            xtype: 'combo',
                            queryMode: 'local',
                            typeAhead: true,
                            editable: true,
                            forceSelection: true,
                            emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts.selectanattribute,
                            itemId: 'selectDisabledAttribute',
                            displayField: 'description',
                            valueField: 'name',
                            bind: {
                                store: '{freeDisabledAttributeStore}'
                            },
                            autoEl: {
                                'data-testid': 'administration-content-domain-disabledattributes-grid-new-attribute'
                            },

                            listeners: {
                                focus: function () {
                                    var me = this;
                                    var vm = me.lookupViewModel();
                                    me.getStore().removeAll();
                                    vm.get('allDetailAttributesStore').each(function (attribute) {
                                        var record = vm.get('disabledAttributesStore').getById(attribute.getId());
                                        if (!record) {
                                            me.getStore().add(attribute);
                                        }
                                    });
                                    return true;
                                }
                            },
                            height: 19,
                            minHeight: 19,
                            maxHeight: 19,
                            padding: 0,
                            ui: 'reordergrid-editor-combo'
                        }
                    }, {
                        xtype: 'actioncolumn',
                        itemId: 'actionColumnAddNew',
                        width: 30,
                        minWidth: 30, // width property not works. Use minWidth.
                        maxWidth: 30,
                        align: 'center',
                        items: [{
                            iconCls: 'x-fa fa-plus',
                            autoEl: {
                                'data-testid': 'administration-content-domain-disabledattributes-grid-new-attribute-addBtn'
                            },
                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                return CMDBuildUI.locales.Locales.administration.common.actions.add;
                            },
                            getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                metadata.value = Ext.String.insert(metadata.value, ' data-testid="administration-content-domain-disabledattributes-grid-new-attribute-addBtn"', -7);
                                return 'x-fa fa-plus';
                            },

                            handler: function (button, rowIndex, colIndex) {

                                var attributeName = button.up('panel').down('#selectDisabledAttribute');
                                if (Ext.isEmpty(attributeName.getValue())) {
                                    attributeName.focus();
                                    attributeName.expand();
                                    return false;
                                }
                                var vm = button.lookupViewModel();
                                var attributeStore = vm.getStore('freeDisabledAttributeStore');
                                var record = vm.get('allDetailAttributesStore').getById(attributeName.getValue());
                                if (record) {
                                    vm.getStore('disabledAttributesStore').add(record);
                                    vm.get('theDomain.masterDetailDisabledCreateAttrs').push(record.get('name'));
                                    attributeStore.remove(record);
                                }
                                var mainGrid = button.up('form').down('#diabledattributesGrid');
                                attributeName.reset();
                                mainGrid.getView().refresh();
                            }
                        }]
                    }]
                }]
            }]

        }, {
            xtype: 'fieldcontainer',
            columnWidth: 1,
            layout: 'column',
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('readFilter', {
                readFilter: {
                    xtype: 'textarea',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetailviewcqlfilter,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetailviewcqlfilter'
                    },
                    resizable: {
                        handles: "s"
                    },
                    fieldcontainer: {
                        hidden: true,
                        bind: {
                            hidden: '{!theDomain.isMasterDetail}'
                        }
                    },
                    name: 'readFilter',
                    bind: {
                        readOnly: '{actions.view}',
                        value: '{theDomain.filterMasterDetail}',
                        hidden: '{!theDomain.isMasterDetail}'
                    }
                }
            })]
        }]
    }]
});