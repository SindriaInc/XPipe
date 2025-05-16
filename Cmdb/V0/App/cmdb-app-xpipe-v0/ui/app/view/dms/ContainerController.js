Ext.define('CMDBuildUI.view.dms.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-container',
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            changeselection: 'onSelectionChange'
        },
        '#dmssearchtext': {
            searchsubmit: 'onSearchSubmit',
            clearsearch: 'onClearSearch',
            specialkey: 'onSearchSpecialKey'
        },
        '#dmsrefreshbtn': {
            click: 'onRefreshBtnClick'
        },
        '#dmscontextmenumultiselection': {
            click: 'onDMSContextMenuMultiSelectionClick'
        },
        '#dmscontextmenudelete': {
            click: 'onDMSContextMenuDeleteClick'
        },
        '#dmscontextmenudownload': {
            click: 'onDMSContextMenuDownloadClick'
        },
        '#dmscontextmenudownloadall': {
            click: 'onDMSContextMenuDownloadAllClick'
        },
        '#showextendedfield': {
            change: 'onShowExtendedFieldChange'
        }
    },

    /**
     * This function populates the button menu
     *
     * @param {CMDBuildUI.view.dms.Container} view
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();

        this.setDmsGrid();

        vm.bind({
            DMSCategoryType: '{dms-container.DMSCategoryType}',
            hidden: '{attachmentsButton.hidden}'
        }, function (data) {
            if (data.DMSCategoryType && !data.hidden) {
                var model = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(view.getObjectTypeName(), view.getObjectType());
                var values = model.get('dmsCategories'),
                    button = this.getView().lookupReference('attachmentsButton'),
                    l = values.length;
                switch (l) {
                    case 1:
                        var item = data.DMSCategoryType.values().findRecord('code', values[0].category);
                        button.DMSModelName = item.get('modelClass') || data.DMSCategoryType.get('modelClass');
                        button.DMSCategoryValue = item.getId();
                        button.DMSCategoryDescription = item.get('text');
                        button.setDisabled(!values[0]._can_create);
                        break;
                    default:
                        var menu = {
                            items: []
                        };
                        values.forEach(function (value, index, array) {
                            var item = data.DMSCategoryType.values().findRecord('code', value.category);
                            menu.items.push({
                                text: item.get('text'),
                                iconCls: CMDBuildUI.model.menu.MenuItem.icons.klass,
                                disabled: !value._can_create,
                                handler: 'onAttachmentsButtonMenitemClick',
                                DMSModelName: item.get('modelClass') || data.DMSCategoryType.get('modelClass'),
                                DMSCategoryValue: item.getId()
                            });
                        }, this);
                        if (!menu.items.length) {
                            button.setDisabled(true);
                        } else {
                            button.setDisabled(false);
                            button.setMenu(menu);
                        }
                        break;
                }
            }
        }, this);
    },
    /**
     *
     * @param {Ext.data.Store} attachments
     * @param {CMDBuildUI.model.dms.DMSAttachment[]} records
     * @param {Boolean} successful
     * @param {Ext.data.operation.Read} operation
     * @param {Object} eOpts
     */
    onAttachmentsLoad: function (attachments, records, successful, operation, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            DMSCategoryType = view.getDMSCategoryType();

        if (successful) {
            vm.set('tabcounters.attachments', records.length);
            Ext.GlobalEvents.fireEvent("updateDataCountStore");
        }

        if (DMSCategoryType) {
            var advancedFilter = attachments.getAdvancedFilter(),
                messageContainer = view.down('#message-container');
            messageContainer.removeAll();
            // add messages only if there is no filters
            if (!advancedFilter || advancedFilter.isEmpty()) {
                var groups = attachments.getGroups(),
                    newItems = [];

                /**
                 * The DMSCategory values are filterd with class permissions
                 */
                var theKlass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(view.getObjectTypeName(), view.getObjectType());
                var DMSCategoryTypeFiltered = Ext.Array.filter(DMSCategoryType.values().getRange(), function (item, index, array) {
                    var code = item.get('code');

                    return Ext.Array.findBy(theKlass.get('dmsCategories'), function (item_2, index, array) {
                        return item_2._can_create && item_2.category == code;
                    }, this);

                }, this);

                DMSCategoryTypeFiltered.forEach(function (item, index, array) {
                    var checkCount = item.get('checkCount'),
                        checkCountNumber = item.get('checkCountNumber');

                    if (Ext.isEmpty(checkCount) || Ext.isEmpty(checkCountNumber)) {
                        var dmsModelName = item.get('modelClass'),
                            dmsClass = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(dmsModelName);

                        if (Ext.isEmpty(checkCount)) {
                            checkCount = dmsClass.get('checkCount');
                        }

                        if (Ext.isEmpty(checkCountNumber)) {
                            checkCountNumber = dmsClass.get('checkCountNumber');
                        }
                    }

                    if (checkCount == CMDBuildUI.model.dms.DMSModel.checkCount.no_check) {
                        return;
                    }

                    //CMDBuildUI.model.dms.DMSCategory must have a field with this name
                    var groupValue = item.get('_description_translation'),
                        group = groups.findBy(function (item, key) {
                            return item.getGroupKey() == groupValue;
                        }, this, 0),
                        groupLength = group ? group.length : 0,
                        warning = Ext.String.format("<b>{0}</b>", CMDBuildUI.locales.Locales.notifier.warning);

                    switch (checkCount) {
                        case CMDBuildUI.model.dms.DMSModel.checkCount.at_least_number:
                            if (groupLength < checkCountNumber) {
                                newItems.push({
                                    margin: "5 8",
                                    html: Ext.String.format(CMDBuildUI.locales.Locales.attachments.warningmessages.atleast, warning, groupLength, groupValue, checkCountNumber)
                                });
                            }
                            break;
                        case CMDBuildUI.model.dms.DMSModel.checkCount.exactly_number:
                            if (groupLength != checkCountNumber) {
                                newItems.push({
                                    margin: '5 8',
                                    html: Ext.String.format(CMDBuildUI.locales.Locales.attachments.warningmessages.exactlynumber, warning, groupLength, groupValue, checkCountNumber)
                                });
                            }
                            break;
                        case CMDBuildUI.model.dms.DMSModel.checkCount.max_number:
                            if (groupLength > checkCountNumber) {
                                newItems.push({
                                    margin: '5 8',
                                    html: Ext.String.format(CMDBuildUI.locales.Locales.attachments.warningmessages.maxnumber, warning, groupLength, groupValue, checkCountNumber)
                                });
                            }
                            break;
                    }
                }, this);

                vm.set("textAlert", newItems);
                messageContainer.add(newItems);
            }
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     */
    onAttachmentsButtonClick: function (button, e) {
        var view = this.getView(),
            object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(view.getObjectTypeName(), view.getObjectType()),
            item = view.getDMSCategoryType().values().findRecord('code', object.get('dmsCategories')[0].category);
        if (!button.getMenu()) {
            this.openCreatePopup(button.DMSModelName, button.DMSCategoryValue, item.get('text'));
        }
    },

    /**
     *
     * @param {Ext.menu.Item} menuitem
     * @param {Ext.event.Event} e
     */
    onAttachmentsButtonMenitemClick: function (item, e) {
        this.openCreatePopup(item.DMSModelName, item.DMSCategoryValue, item.text);
    },

    /**
     *
     * @param {Ext.form.field.Text} field
     * @param {String} value
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, value, eOpts) {
        if (value) {
            var vm = field.lookupViewModel(),
                store = vm.get('attachments');
            if (store) {
                store.getAdvancedFilter().addQueryFilter(value);
                store.load();
            }
        } else {
            this.onClearSearch(field, eOpts);
        }
    },

    /**
     *
     * @param {Ext.form.field.Text} field
     * @param {Object} eOpts
     */
    onClearSearch: function (field, eOpts) {
        var vm = field.lookupViewModel(),
            store = vm.get('attachments');
        if (store) {
            store.getAdvancedFilter().clearQueryFilter();
            store.load();
            field.setValue(null);
        }
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field, field.getValue(), event);
        }
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onRefreshBtnClick: function (btn, eOpts) {
        var vm = this.getView().lookupViewModel(),
            store = vm.get('attachments');
        store.load();
    },

    /**
     *
     * @param {String} modelName
     * @param {String} DMSCategoryValue
     * @param {String} DMSCategoryDescription
     */
    openCreatePopup: function (modelName, DMSCategoryValue, DMSCategoryDescription) {
        CMDBuildUI.util.helper.ModelHelper.getModel(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
            modelName
        ).then(function (model) {
            var view = this.getView(),
                title = CMDBuildUI.locales.Locales.attachments.new + ' ' + DMSCategoryDescription,
                attachmentsStore = view.lookupViewModel().getStore('attachments'),
                invalidFileNames = attachmentsStore.collect("name");

            CMDBuildUI.util.Utilities.openPopup('popup-add-attachment-form', title, {
                xtype: 'dms-attachment-create',
                objectType: view.getObjectType(),
                objectTypeName: view.getObjectTypeName(),
                objectId: view.getObjectId(),
                attachmentId: null,
                DMSCategoryTypeName: view.getDMSCategoryTypeName(),
                DMSCategoryValue: DMSCategoryValue,
                DMSCategoryDescription: DMSCategoryDescription,
                ignoreSchedules: view.getIgnoreSchedules(),
                asyncStore: view.getIsAsyncSave() ? attachmentsStore : null,
                invalidFileNames: invalidFileNames
            }, {
                popupsave: {
                    fn: function () {
                        this.getViewModel().getStore('attachments').load();
                    },
                    scope: this
                },
                popupcancel: function () { }
            });

        }, Ext.emptyFn, Ext.emptyFn, this);
    },

    /**
     *
     * @param {Ext.grid.Panel} grid
     */
    onSelectionChange: function (grid) {
        var vm = this.getViewModel();
        if (grid.getStore().query("_checkAttachment", true).getCount() > 1) {
            vm.set('disabledbulkactions', false);
        } else {
            vm.set('disabledbulkactions', true);
        }
    },

    /**
     *
     * @param {Ext.menu.Item} menuitem
     * @param {Object} eOpts
     */
    onDMSContextMenuMultiSelectionClick: function (menuitem, eOpts) {
        var grid = this.getView().down("dms-grid"),
            vmGrid = grid.getViewModel();

        if (menuitem.multiselection) {
            menuitem.setText(CMDBuildUI.locales.Locales.common.grid.enamblemultiselection);
            menuitem.setIconCls('x-fa fa-square-o');
            vmGrid.set("hideCheckColumn", true);
            this.getViewModel().set('disabledbulkactions', true);
        } else {
            menuitem.setText(CMDBuildUI.locales.Locales.common.grid.disablemultiselection);
            menuitem.setIconCls('x-fa fa-check-square-o');
            vmGrid.set("hideCheckColumn", false);
            this.onSelectionChange(grid);
        }

        // update multiselection variable
        menuitem.multiselection = !menuitem.multiselection;
    },

    /**
     *
     * @param {Ext.menu.Item} menuitem
     * @param {Object} eOpts
     */
    onDMSContextMenuDeleteClick: function (menuitem, eOpts) {
        var grid = this.getView().down("dms-grid"),
            store = grid.getStore(),
            selection = store.query("_checkAttachment", true).getRange(),
            // create confirm message
            message = Ext.String.format(
                CMDBuildUI.locales.Locales.bulkactions.confirmdeleteattachements,
                selection.length
            ),
            isAsyncSave = this.getView().getIsAsyncSave(),
            resp = {
                url: store.getProxy().getUrl(),
                advancedFitler: new CMDBuildUI.util.AdvancedFilter()
            };

        var selectedids = [];
        selection.forEach(function (sel) {
            selectedids.push(sel.getId());
        });
        resp.advancedFitler.addAttributeFilter('_id', 'in', selectedids);

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            message,
            function (btn) {
                if (btn === 'yes') {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    var loadmask = CMDBuildUI.util.Utilities.addLoadMask(grid);
                    if (!isAsyncSave) {
                        Ext.Ajax.request({
                            url: resp.url,
                            method: 'DELETE',
                            jsonData: {},
                            params: {
                                filter: resp.advancedFitler.encode()
                            },
                            callback: function (request, success, response) {
                                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
                                if (success) {
                                    grid.setSelection();
                                    // reload store
                                    store.load();
                                }
                            }
                        });
                    } else {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
                    }
                }
            }, this);
    },

    /**
     *
     * @param {Ext.menu.Item} menuitem
     * @param {Object} eOpts
     */
    onDMSContextMenuDownloadClick: function (menuitem, eOpts) {
        var desc = menuitem.lookupViewModel().get('titledata.item') || '';

        // remove special characters for filename
        desc = desc.replace(/[^a-zA-Z0-9]/g, '');
        desc = desc ? desc.substring(0, 30) : 'attachments';

        this.downloadAttachments(
            desc + '.zip',
            this.getView().down("dms-grid").getStore().query("_checkAttachment", true).getRange(),
            CMDBuildUI.locales.Locales.bulkactions.download
        );
    },

    /**
     *
     * @param {Ext.menu.Item} menuitem
     * @param {Object} eOpts
     */
    onDMSContextMenuDownloadAllClick: function (menuitem, eOpts) {
        var desc = menuitem.lookupViewModel().get('titledata.item') || '';

        // remove special characters for filename
        desc = desc.replace(/[^a-zA-Z0-9]/g, '');
        desc = desc ? desc.substring(0, 30) : 'attachments';

        this.downloadAttachments(
            desc + '.zip',
            this.getView().down("dms-grid").getStore().getRange(),
            CMDBuildUI.locales.Locales.bulkactions.downloadall
        );
    },

    /**
     * 
     * @param {Ext.field.Checkbox} field 
     * @param {Boolean} newValue 
     * @param {Boolean} oldValue 
     * @param {Object} eOpts 
     */
    onShowExtendedFieldChange: function (field, newValue, oldValue, eOpts) {
        var view = this.getView(),
            menuitem = view.down("#dmscontextmenumultiselection");

        view.removeAll();
        this.getViewModel().set("enableExtendedGrid", newValue);
        this.setDmsGrid(newValue);

        if (newValue) {
            menuitem.multiselection = false;
            menuitem.setText(CMDBuildUI.locales.Locales.common.grid.enamblemultiselection);
            menuitem.setIconCls('x-fa fa-square-o');
        }
    },

    privates: {
        /**
         * Return `true` if record exist, is not phantom and the file was not modified
         *
         * @param {Ext.data.Model} record
         *
         * @returns {Boolean}
         */
        canDownloadRecord: function (record) {
            return record && !record.phantom && !record.isModified('_filedata');
        },

        /**
         * Download attachments and show alerts if necessary
         *
         * @param {String} filename Name of the archive file that will be downloaded
         * @param {Ext.data.Model[]} attachments List of attachments that will be added to the archive to be downloaded
         * @param {String} messageTitle Title for alert message
         */
        downloadAttachments: function (filename, attachments, messageTitle) {
            var me = this,
                attachmentsIds = [],
                notDownloadedCount = 0;

            // get attachments ids
            attachments.forEach(function (att) {
                if (me.canDownloadRecord(att)) {
                    attachmentsIds.push(att.getId());
                } else {
                    notDownloadedCount++;
                }
            });
            if (attachmentsIds.length && notDownloadedCount > 0) {
                // show alert if there are files that will not be downloaded
                CMDBuildUI.util.Msg.alert(messageTitle, CMDBuildUI.locales.Locales.bulkactions.alertdownloadattachments, function (action) {
                    if (action === 'ok') {
                        me.doDownloadAttachments(filename, attachmentsIds);
                    }
                });
            } else if (!attachmentsIds.length) {
                // show alert if no files will be downloaded
                CMDBuildUI.util.Msg.alert(messageTitle, CMDBuildUI.locales.Locales.bulkactions.alertnoattachmentsdownload);
            } else {
                this.doDownloadAttachments(filename, attachmentsIds);
            }
        },

        /**
         * Download attachments
         *
         * @param {String} filename Name of the archive file that will be downloaded
         * @param {Ext.data.Model[]} attachmentsIds List of attachments ids that will be added to the archive to be downloaded
         */
        doDownloadAttachments: function (filename, attachmentsIds) {
            // download file
            CMDBuildUI.util.File.download(Ext.String.format('{0}{1}/_MANY/{2}?attachmentId={3}',
                CMDBuildUI.util.Config.baseUrl,
                this.getViewModel().get('proxyUrl'), // specificated in CMDBuildUI.view.dms.GridModel
                filename,
                attachmentsIds.join(',')
            ), filename);
        },

        /**
         * Set the grid to view
         * @param {Boolean} checkboxSelected 
         */
        setDmsGrid: function (checkboxSelected) {
            var view = this.getView(),
                textAlert = view.lookupViewModel().get("textAlert"),
                items = [{
                    layout: {
                        type: 'vbox',
                        align: 'stretch' //stretch vertically to parent
                    },
                    xtype: 'panel',
                    itemId: 'message-container',
                    defaults: {
                        ui: 'messagewarning',
                        xtype: 'container'
                    },
                    items: textAlert
                }];

            if (!checkboxSelected) {
                items.push({
                    xtype: 'dms-grid',
                    flex: 1,
                    bind: {
                        store: '{attachments}'
                    }
                });
            } else {
                var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(view.getObjectTypeName(), view.getObjectType());

                if (object) {
                    var dmsCategories = object.get("dmsCategories");

                    Ext.Array.forEach(dmsCategories, function (item, index, allitems) {
                        items.push({
                            xtype: 'dms-expanded-fieldset',
                            viewModel: {
                                data: {
                                    dmsCategory: item.category
                                }
                            }
                        });
                    });
                }
            }

            view.add(items);
        }

    }
});