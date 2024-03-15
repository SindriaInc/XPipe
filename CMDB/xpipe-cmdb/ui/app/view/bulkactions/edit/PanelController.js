Ext.define('CMDBuildUI.view.bulkactions.edit.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bulkactions-edit-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#attribtuescombo': {
            select: 'onAttributesComboSelect'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.bulkactions.edit.Panel} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        me._modifiedattributes = {};
        me._fieldsets = {};

        var vm = view.lookupViewModel();
        var objectitem = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
            view.getObjectTypeName(),
            view.getObjectType()
        );

        vm.linkTo('theObject', {
            type: CMDBuildUI.util.helper.ModelHelper.getModelName(view.getObjectType(), view.getObjectTypeName()),
            create: true
        });

        // update attributes store
        var attributeslist = [];
        objectitem.getAttributes().then(function (attributes) {
            attributes.getRange().forEach(function (attr) {
                if (attr.get("type") !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula) {
                    var _attr = attr.copy();
                    if (!_attr.get("_group_description_translation")) {
                        _attr.set("_group_description_translation", CMDBuildUI.locales.Locales.common.attributes.nogroup);
                    }
                    attributeslist.push(_attr);
                }
            });
            vm.set("attributeslist", attributeslist);
        });

        var form = view.lookupReference('bulkeditform');

        // add fieldsets for each group
        objectitem.attributeGroups().getRange().forEach(function (group) {
            me._fieldsets[group.get("name")] = form.add({
                xtype: 'formpaginationfieldset',
                title: group.get("_description_translation"),
                collapsible: true,
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                hidden: true
            });
        });

        // add nogroups fieldset
        me._fieldsets[CMDBuildUI.model.AttributeGrouping.nogroup] = form.add({
            xtype: 'formpaginationfieldset',
            title: CMDBuildUI.locales.Locales.common.attributes.nogroup,
            collapsible: true,
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            hidden: true
        });
    },

    /**
     * 
     * @param {Ext.form.field.ComboBox} combo 
     * @param {CMDBuildUI.model.Attribute} record 
     * @param {Object} eOpts 
     */
    onAttributesComboSelect: function (combo, record, eOpts) {
        if (record) {
            var me = this,
                view = this.getView();
            var model = CMDBuildUI.util.helper.ModelHelper.getModelFromName(
                CMDBuildUI.util.helper.ModelHelper.getModelName(view.getObjectType(), view.getObjectTypeName())
            );
            // get model field
            var field = model.getField(record.get("name"));
            // get fieldset
            var fieldset = field.attributeconf.group ?
                this._fieldsets[field.attributeconf.group] :
                this._fieldsets[CMDBuildUI.model.AttributeGrouping.nogroup];
            // add field to form            
            var row = fieldset.add({
                layout: {
                    type: 'hbox',
                    align: 'end'
                },

                items: [Ext.merge(CMDBuildUI.util.helper.FormHelper.getFormField(field, {
                    mode: CMDBuildUI.util.helper.FormHelper.formmodes.update,
                    ignoreUpdateVisibilityToField: true,
                    ignoreCustomValidator: true,
                    ignoreAutovalue: true
                }), {
                    flex: 1
                }), {
                    xtype: 'button',
                    iconCls: 'x-fa fa-trash',
                    margin: "auto auto 1 10",
                    ui: 'management-action',
                    tooltip: CMDBuildUI.locales.Locales.common.actions.remove,
                    handler: function () {
                        // make record writabe again and re-add to combo store
                        record.set("writable", true);
                        combo.getStore().add(record);

                        delete me._modifiedattributes[record.get("name")];
                        if (row.up().items.length === 1) {
                            row.up().setHidden(true);
                        }
                        row.destroy();
                    }
                }]
            });
            // show fieldset
            fieldset.setHidden(false);
            // remove attribute from combo
            record.set("writable", false);
            this._modifiedattributes[record.get("name")] = record.get("_description_translation");
            // clear combo
            combo.setValue();
        }
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Object} eOpts 
     */
    onSaveBtnClick: function () {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            grid = view.ownerGrid,
            objectData = vm.get("theObject").getData();

        // get request info
        var requestinfo = CMDBuildUI.view.bulkactions.Util.getRequestInfo(grid);

        function getMetaAttrs(attrname) {
            var re = new RegExp("^_" + attrname + "_([A-Z]\\w+)$");

            return Ext.Array.filter(Ext.Object.getAllKeys(objectData), function (item) {
                return CMDBuildUI.util.api.Client.testRegExp(re, item);
            });
        }

        // get changed data
        var changedData = {},
            changedAttributes = [];
        Ext.Object.each(this._modifiedattributes, function (attrname, attrdesc) {
            changedData[attrname] = objectData[attrname];

            getMetaAttrs(attrname).forEach(function (meta_attr) {
                changedData[meta_attr] = objectData[meta_attr];
            });

            changedAttributes.push(attrdesc);
        });

        if (Ext.Object.isEmpty(changedData)) {
            // show error message
            CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.bulkactions.noattributeselected);
        } else {
            // create confirm message
            var me = this,
                allPromises = [],
                message = Ext.String.format(
                    CMDBuildUI.locales.Locales.bulkactions.confirmedit,
                    '<em>' + changedAttributes.sort().join(", ") + '</em>',
                    requestinfo.count
                );

            CMDBuildUI.util.Msg.confirm(
                CMDBuildUI.locales.Locales.notifier.attention,
                message,
                function (btn) {
                    if (btn === "yes") {
                        CMDBuildUI.util.helper.FormHelper.startSavingForm();
                        var loadMask = CMDBuildUI.util.Utilities.addLoadMask(view);

                        if (Ext.isArray(requestinfo.advancedFitler)) {
                            Ext.Array.forEach(requestinfo.advancedFitler, function (item, index, allitems) {
                                allPromises.push(me.makePutRequest(requestinfo.url, changedData, item));
                            });
                        } else {
                            allPromises.push(me.makePutRequest(requestinfo.url, changedData, requestinfo.advancedFitler));
                        }

                        Ext.Promise.all(allPromises).then(function (responses, eOpts) {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                            // reload store
                            grid.getStore().load();
                            // close popup
                            view.closePopup();
                        }, function () {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
                        });
                    }
                }
            );
        }
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Object} eOpts 
     */
    onCancelBtnClick: function (btn, eOpts) {
        this.getView().closePopup();
    },

    privates: {
        /**
         * @property {Object} _fieldsets 
         * Reference to fieldsets added in the form. 
         */
        _fieldsets: {},

        /**
         * @property {Object} _modifiedattributes
         * The list of modified attributes
         */
        _modifiedattributes: {},

        /**
         * Make put request
         * @param {String} url 
         * @param {Object} changedData 
         * @param {String} filter 
         * @returns {Ext.promise.Promise}
         */
        makePutRequest: function (url, changedData, filter) {
            var deferred = new Ext.Deferred();

            // make ajax request
            Ext.Ajax.request({
                url: url,
                method: 'PUT',
                jsonData: changedData,
                params: {
                    filter: filter
                },
                callback: function (request, success, response) {
                    if (success) {
                        deferred.resolve();
                    } else {
                        deferred.reject();
                    }
                }
            });

            return deferred.promise;
        }
    }
});