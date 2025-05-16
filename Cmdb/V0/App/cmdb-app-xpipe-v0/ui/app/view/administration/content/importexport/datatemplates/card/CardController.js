Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardController', {
    extend: 'Ext.app.ViewController',
    mixins: [
        'CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardMixin'
    ],
    alias: 'controller.view-administration-content-importexport-datatemplates-card',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editFilterBtn': {
            click: 'onEditFilterBtn'
        },
        '#removeFilterBtn': {
            click: 'onRemoveFilterBtn'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        },

        '#targetName_input': {
            change: 'onTargetNameInputChange'
        }

    },

    /**
     * @param {CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        CMDBuildUI.util.Stores.loadImportExportTemplatesStore();
        CMDBuildUI.util.Stores.loadEmailAccountsStore();
        CMDBuildUI.util.Stores.loadEmailTemplatesStore();
        var me = this;
        var vm = this.getViewModel();

        vm.bind({
            bindTo: '{allSelectedAttributesStore}'
        }, function (store) {
            me.onAllSelectedAttributesStoreDatachanged(store);
        });
        var fiedlsetHelper = CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper;

        if (!vm.get("showInMainPanel")) {
            if (!vm.get('theGateTemplate')) {
                if (!vm.get('grid')) {
                    vm.set('grid', vm.getParent().getView());
                }
                vm.set('theGateTemplate', vm.get('grid').getSelection());
            }

            if (vm.get('theGateTemplate').phantom) {
                Ext.Array.forEach(vm.get('theGateTemplate').get('columns'), function (item, index) {
                    vm.get('theGateTemplate').columns().add(item);
                });
            } else {
                vm.linkTo("theGateTemplate", {
                    type: 'CMDBuildUI.model.importexports.Template',
                    id: vm.get('grid').getSelection()[0].get('_id')
                });
            }
        }

        vm.bind({
            bindTo: {
                theGateTemplate: '{theGateTemplate}'
            }
        }, function (data) {
            if (data.theGateTemplate) {

                if (vm.get('actions.clone') && !data.theGateTemplate.isClone) {
                    Ext.asap(function () {
                        vm.set('theGateTemplate', vm.get('theGateTemplate').copyForClone());
                    });
                    return;
                }
                view.add(fiedlsetHelper.getGeneralPropertiesFieldset());
                view.add(fiedlsetHelper.getAttributesFieldset());
                view.add(fiedlsetHelper.getImportCriteriaFieldset());
                view.add(fiedlsetHelper.getExportCriteriaFieldset());
                if (data.theGateTemplate.get('fileFormat') !== 'ifc' && data.theGateTemplate.get('fileFormat') !== 'database') {
                    view.add(fiedlsetHelper.getErrorsManagementFieldset());
                }
                vm.set('fieldsadded', true);
                switch (vm.get('theGateTemplate.type')) {
                    case CMDBuildUI.model.importexports.Template.types['import']:
                        view.down('#importfieldset').setHidden(false);
                        view.down('#exportfilterfieldset').setHidden(true);
                        break;
                    case CMDBuildUI.model.importexports.Template.types['export']:
                        view.down('#importfieldset').setHidden(true);
                        view.down('#exportfilterfieldset').setHidden(vm.get('isClass') && false);
                        break;
                    case CMDBuildUI.model.importexports.Template.types.importexport:
                        view.down('#importfieldset') && view.down('#importfieldset').setHidden(false);
                        view.down('#exportfilterfieldset') && view.down('#exportfilterfieldset').setHidden(vm.get('isClass') && false);
                        break;
                    default:
                        view.down('#importfieldset').setHidden(true);
                        view.down('#exportfilterfieldset').setHidden(true);
                        break;
                }
                if (vm.get('actions.view')) {
                    Ext.Array.forEach(view.down('#importExportAttributeGrid').getColumns(), function (column) {
                        if (column.xtype === 'actioncolumn') {
                            column.destroy();
                        }
                    });
                }
                Ext.asap(function () {
                    try {
                        view.setHidden(false);
                        view.up().unmask();
                    } catch (error) {

                    }
                }, this);

            }
        });



        var topbar = {
            xtype: 'components-administration-toolbars-formtoolbar',
            dock: 'top',
            hidden: true,
            bind: {
                hidden: '{!actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true, // #editBtn set true for show the button
                view: false, // #viewBtn set true for show the button
                clone: true, // #cloneBtn set true for show the button
                'delete': true, // #deleteBtn set true for show the button
                activeToggle: true // #enableBtn and #disableBtn set true for show the buttons       
            },

                /* testId */
                'importexporttemplates',

                /* viewModel object needed only for activeTogle */
                'theGateTemplate',

                /* add custom tools[] on the left of the bar */
                [],

                /* add custom tools[] before #editBtn*/
                [],

                /* add custom tools[] after at the end of the bar*/
                []
            )
        };
        view.addDocked(topbar);

        // } else {
        var formButtons = {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true, {}, {
                listeners: {
                    mouseover: function () {
                        this.up('form').form.checkValidity();
                        var invalidFields = CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(this.up('form').form);
                        Ext.Array.forEach(invalidFields, function (field) {
                            CMDBuildUI.util.Logger.log(Ext.String.format('{0} is invalid', field.itemId), CMDBuildUI.util.Logger.levels.error);
                        });
                    }
                }
            })
        };
        view.addDocked(formButtons);


    },

    onAfterRender: function (view) {
        var me = this;
        var vm = this.getViewModel();
        vm.bind({
            bindTo: {
                type: '{theGateTemplate.type}',
                fieldsadded: '{fieldsadded}'
            }
        }, function (data) {

            if (data.fieldsadded && data.type && view.down('#importfieldset')) {
                switch (data.type) {
                    case CMDBuildUI.model.importexports.Template.types['import']:
                        view.down('#importfieldset').setHidden(false);
                        view.down('#exportfilterfieldset').setHidden(true);
                        break;
                    case CMDBuildUI.model.importexports.Template.types['export']:
                        view.down('#importfieldset').setHidden(true);
                        if (vm.get('isDomain')) {
                            view.down('#exportfilterfieldset').setHidden(true);
                        } else {
                            view.down('#exportfilterfieldset').setHidden(false);
                        }
                        break;
                    case CMDBuildUI.model.importexports.Template.types.importexport:
                        view.down('#importfieldset').setHidden(false);
                        if (vm.get('isDomain')) {
                            view.down('#exportfilterfieldset').setHidden(true);
                        } else {
                            view.down('#exportfilterfieldset').setHidden(false);
                        }
                        break;
                    default:
                        break;
                }
            }
        });


        vm.bind({
            bindTo: {
                theGateTemplate: '{theGateTemplate}',
                type: '{theGateTemplate.type}',
                targetType: '{theGateTemplate.targetType}',
                fileFormat: '{theGateTemplate.fileFormat}',
                mergeMode: '{theGateTemplate.mergeMode}',
                errorTemplate: '{theGateTemplate.errorTemplate}',
                errorAccount: '{theGateTemplate.errorAccount}',
                fieldsadded: '{fieldsadded}'
            }
        }, function (data) {
            if (data.fieldsadded && data.theGateTemplate) {
                var form = view;
                var importKeyAttr = form.down('#importKeyAttribute_input');
                var mergeMode = form.down('#mergeMode_input');
                var missingRecordAttr = form.down('#mergeMode_when_missing_update_attr_input');
                var missingRecordValue = form.down('#mergeMode_when_missing_update_value_input');
                var dataRowField = form.down('#dataRow_input');
                var headerRowField = form.down('#headerRow_input');

                if (data.type) {

                    var isDomain = data.targetType === CMDBuildUI.model.administration.MenuItem.types.domain;
                    var isModifyCard = data.mergeMode === CMDBuildUI.model.importexports.Template.missingRecords.modifycard;
                    var isNoMerge = data.mergeMode === CMDBuildUI.model.importexports.Template.missingRecords.nomerge;
                    switch (data.type) {
                        case CMDBuildUI.model.importexports.Template.types['export']:
                            me.setAllowBlank(importKeyAttr, true);
                            me.setAllowBlank(mergeMode, true);
                            break;

                        default:
                            me.setAllowBlank(importKeyAttr, isDomain || isNoMerge);
                            me.setAllowBlank(mergeMode, false);
                            break;

                    }
                    me.setAllowBlank(missingRecordValue, !isModifyCard);
                    me.setAllowBlank(missingRecordAttr, !isModifyCard);
                }

                if (data.fileFormat) {
                    switch (data.fileFormat) {
                        case 'csv':
                            me.setAllowBlank(dataRowField, true);
                            me.setAllowBlank(headerRowField, true);

                            break;

                        default:
                            me.setAllowBlank(dataRowField, false);
                            me.setAllowBlank(headerRowField, false);
                            break;
                    }
                }
                form.form.checkValidity();
            }
        });

    },

    onAddAttributeBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        var attributeInput = button.up().down('combo');
        var selectedAttribute = attributeInput.getValue();
        if (selectedAttribute) {
            var attribute = attributeInput.getStore().findRecord('name', selectedAttribute, false, false, true, true);
            var attributeDescription = attribute.get('description');
            var newAttribute = CMDBuildUI.model.importexports.Attribute.create({
                attribute: selectedAttribute,
                columnName: attributeDescription,
                mode: ''
            });
            vm.get('allSelectedAttributesStore').add(newAttribute);
            attributeInput.setValue(null);
            var attributesStore = attributeInput.getStore();
            attributesStore.remove(attributesStore.findRecord('name', selectedAttribute, false, false, true, true));
        } else {
            attributeInput.focus();
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditFilterBtn: function (button, e, eOpts) {
        var me = this;

        var vm = me.getViewModel();
        var record = vm.get('theGateTemplate');
        var actions = vm.get('actions');
        var recordFilter = record.get('exportFilter').length ? JSON.parse(record.get('exportFilter')) : {};

        var popuTitle = CMDBuildUI.locales.Locales.administration.importexport.texts.filterfortemplate;
        var type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(record.get("targetName")) || vm.get('theGateTemplate.targetType');

        popuTitle = Ext.String.format(
            popuTitle,
            record.get('description'));

        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            ownerType: record.get("targetType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.view ? CMDBuildUI.util.helper.ModelHelper.objecttypes.view : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            target: record.get("targetName"),
            configuration: recordFilter

        });

        var viewmodel = {
            data: {
                objectType: type,
                objectTypeName: record.get("targetName"),
                theFilter: filter,
                actions: Ext.copy(actions)
            }
        };

        var attrbutePanel = this.getAttributesFilterTab(viewmodel, record);
        var relationsPanel = this.getRelationsFilterTab(viewmodel, record);
        var functionPanel = this.getFunctionFilterTab(viewmodel, record);
        var fulltextPanel = this.getFulltextFilterTab(viewmodel, record);

        var listeners = {
            /**
             * 
             * @param {CMDBuildUI.view.filters.Panel} panel 
             * @param {CMDBuildUI.model.base.Filter} filter 
             * @param {Object} eOpts 
             */
            applyfilter: function (panel, _filter, _eOpts) {
                me.onApplyFilter(_filter);
                me.popup.close();
            },
            /**
             * 
             * @param {CMDBuildUI.view.filters.Panel} panel 
             * @param {CMDBuildUI.model.base.Filter} filter 
             * @param {Object} eOpts 
             */
            saveandapplyfilter: function (panel, _filter, _eOpts) {
                me.onSaveAndApplyFilter(_filter);
                me.popup.close();
            },
            /**
             * Custom event to close popup directly from popup
             * @param {Object} eOpts 
             */
            popupclose: function (_eOpts) {
                me.popup.close();
            }
        };

        var dockedItems = [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: me.getViewModel().get('actions.view'),
            items: CMDBuildUI.util.administration.helper.FormHelper.getOkCloseButtons({
                handler: function (_button) {
                    CMDBuildUI.util.administration.helper.FilterHelper.setRecordFilterFromPanel(me.popup, record, 'exportFilter');
                }
            }, {
                handler: function () {
                    me.popup.close();
                }
            })
        }];
        var content = {
            xtype: 'tabpanel',
            cls: 'administration',
            ui: 'administration-tabandtools',
            items: [attrbutePanel, relationsPanel, functionPanel, fulltextPanel],
            dockedItems: dockedItems,
            listeners: listeners
        };
        if (type === CMDBuildUI.model.administration.MenuItem.types.view) {
            Ext.Array.remove(content.items, relationsPanel);
        }

        me.popup = CMDBuildUI.util.Utilities.openPopup(
            'filterpopup',
            popuTitle,
            content, {}, {
            ui: 'administration-actionpanel',
            viewModel: {
                data: {
                    index: '0',
                    grid: {},
                    record: record,
                    canedit: true
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRemoveFilterBtn: function (button, e, eOpts) {
        this.getViewModel().set('theGateTemplate.exportFilter', '');
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = me.getView();
        var vm = this.getViewModel();
        var theGateTemplate = vm.get('theGateTemplate');
        CMDBuildUI.util.Utilities.showLoader(true, button.up('panel'));
        me.setColumnsData();
        if (!theGateTemplate.get('columns').length) {
            CMDBuildUI.util.Notifier.showWarningMessage(
                CMDBuildUI.locales.Locales.administration.importexport.texts.emptyattributegridmessage
            );

            CMDBuildUI.util.Utilities.showLoader(false, button.up('panel'));
        } else if (theGateTemplate.isValid()) {
            if (theGateTemplate.get('type') === CMDBuildUI.model.importexports.Template.types['export']) {

                Ext.Array.forEach(theGateTemplate.get('columns'), function (item) {
                    item['default'] = '';
                });
            }
            var mergeValueInput = form.down("#mergeMode_when_missing_update_value_input");
            if (mergeValueInput && mergeValueInput.getValueAsString) {
                theGateTemplate.set('mergeMode_when_missing_update_value', mergeValueInput.getValueAsString());
            }

            theGateTemplate.save({
                success: function (record, operation) {
                    if (!vm.get('showInMainPanel')) {
                        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                            function () {
                                var nextUrl = Ext.util.History.getToken();
                                CMDBuildUI.util.administration.MenuStoreBuilder.selectNode('href', nextUrl, me);
                                var eventToCall = vm.get('actions.edit') ? 'itemupdated' : 'itemcreated';
                                var grid = vm.get('grid');
                                grid.getPlugin('administration-forminrowwidget').view.fireEventArgs(eventToCall, [grid, record, me]);
                                form.fireEventArgs('savesuccess', [record, operation]);
                                form.up().fireEvent("closed");
                            });
                    } else {
                        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                            function () {
                                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl(record.get('_id'));
                                CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                            });
                    }
                    vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);

                },
                failure: function () {
                    CMDBuildUI.util.Utilities.showLoader(false, button.up('panel'));
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        vm.get("theGateTemplate").reject(); // discard changes        
        if (vm.get('showInMainPanel')) {
            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl(vm.get("theGateTemplate._id"));
            if (vm.get('actions.add')) {
                nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl();
            }
            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
        }
        if (this.getView() && !this.getView().destroyed) {
            this.getView().up().fireEvent("closed");
        }

    },

    // ok
    onTargetNameInputChange: function (combo, newValue, oldValue) {
        var form = combo.up('form');
        var vm = combo.lookupViewModel();
        if (oldValue) {
            vm.set('theGateTemplate.mergeMode_when_missing_update_attr', null);
        }
        form.form.checkValidity();
    },

    privates: {
        setAllowBlank: function (field, value, form) {

            if (field) {
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, value, form);
            }
        },
        /**
         * 
         * @param {CMDBuildUI.model.base.Filter} filter The filter to edit.
         */
        getAttributesFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-attributes-panel',
                title: CMDBuildUI.locales.Locales.administration.attributes.attributes,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.attributes.attributes'
                },
                viewModel: viewmodel
            };
            return filterPanel;
        },

        getRelationsFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-relationfilters-panel',
                title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations,
                localized: {
                    title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations
                },
                viewModel: viewmodel
            };
            return filterPanel;
        },

        getFunctionFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-functionfilters-panel',
                title: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.common.labels.funktion'
                },
                viewModel: viewmodel
            };
            return filterPanel;
        },

        getFulltextFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-fulltextfilter-panel',
                title: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fulltext,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.searchfilters.texts.fulltext'
                },
                viewModel: viewmodel
            };
            return filterPanel;
        }
    }
});