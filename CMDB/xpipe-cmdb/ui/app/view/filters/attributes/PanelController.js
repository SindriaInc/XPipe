Ext.define('CMDBuildUI.view.filters.attributes.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.filters-attributes-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#attributecombo': {
            select: 'onAttributeComboSelect'
        },
        '#removebutton': {
            click: 'onRemoveButtonClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.filters.attributes.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view._fieldsetsreferences = [];
        var me = this;
        var vm = this.getViewModel();
        var fields = {};
        var store = vm.get("attributeslist");

        CMDBuildUI.util.helper.ModelHelper.getModel(vm.get("objectType"), vm.get("objectTypeName")).then(function (model) {
            CMDBuildUI.util.helper.FiltersHelper.addOperators(store);

            // multitenant field
            me._addTenantFieldIfNeeded(model, store, fields);

            // activity field (if object type is process)
            me._addActivityFieldIfNeeded(model, store, fields);

            // get model fields
            me._addModelFields(model, store, fields);

            vm.set("allfields", fields);

            var config = vm.get("theFilter").get("configuration");
            if (config && config.attributesCustom || config.attribute) {
                CMDBuildUI.util.helper.FiltersHelper.populateAttributeContainer(view, config.attributesCustom || config.attribute);
            }

            if (CMDBuildUI.util.helper.SessionHelper.getViewportVM().get('isAdministrationModule')) {
                var disable = vm.get('actions.view') ? true : false;
                vm.set("displayOnly", disable);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRemoveButtonClick: function (button, e, eOpts) {
        // destroy row
        var parent = button.up("filters-attributes-row") || button.up('filters-attributes-block');
        parent.destroy();
    },

    /**
     * @param {Ext.form.field.ComboBox} combo 
     * @param {CMDBuildUI.model.base.ComboItem} record 
     * @param {Object} eOpts 
     */
    onAttributeComboSelect: function (combo, record, eOpts) {
        if (record) {
            var block = combo.getParentBlock().down('#blockitems');
            if (record.get('operator')) {
                block.add(this.getBlockConfig(
                    record.get('value'),
                    block.up().getLevel() + 1
                ));
            } else {
                block.add(
                    this.getAttributeRowConfig({
                        attribute: record.get("value")
                    })
                );
            }
            combo.setValue();
        }
    },

    privates: {
        /**
         * Returns filter block configuration
         * @param {String} operator 
         * @param {Integer} level 
         * @returns {Object}
         */
        getBlockConfig: function (operator, level) {
            return {
                xtype: 'filters-attributes-block',
                operator: operator,
                level: level
            };
        },

        /**
         * Returns filter attribute configuration
         * @param {Object} filter
         * @param {String} filterRowPanel.attribute
         * @param {String} filterRowPanel.operator
         * @param {Boolean} filterRowPanel.typeinput
         * @param {*} filterRowPanel.value1
         * @param {*} filterRowPanel.value2
         * @returns {Object}
         */
        getAttributeRowConfig: function (filter) {
            var view = this.getView(),
                attribute = view.lookupViewModel().get("allfields")[filter.attribute],
                filterRowPanel;

            if (attribute) {
                var attrConf = attribute.attributeconf;
                filter.attribute_description = attrConf.description_localized || attribute._localized_description || attribute._attributeDescription || attribute.description || attrConf.description;
                if (attribute._attributeClassAlias) {
                    filter.attribute_description = Ext.String.format("{0} - {1}", attribute._attributeClassAlias, attribute._attributeDescription);
                }
            }

            filterRowPanel = {
                xtype: 'filters-attributes-row',
                allowInputParameter: view.getAllowInputParameter(),
                viewModel: {
                    data: {
                        values: Ext.clone(filter)
                    }
                }
            };

            if (CMDBuildUI.util.helper.SessionHelper.getViewportVM().get('isAdministrationModule')) {
                Ext.apply(filterRowPanel, {
                    allowCurrentUser: view.getAllowCurrentUser(),
                    allowCurrentGroup: view.getAllowCurrentGroup()
                });
            }

            return filterRowPanel;
        },

        _addTenantFieldIfNeeded: function (model, store, fields) {
            if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled)) {
                var objectdefinition = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(model.objectTypeName, model.objectType);
                var multitenantMode = objectdefinition ? objectdefinition.get("multitenantMode") : null;
                if (
                    multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.always ||
                    multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.mixed
                ) {
                    store.add({
                        value: '_tenant',
                        label: CMDBuildUI.util.Utilities.getTenantLabel(),
                        group: ''
                    });
                    fields['_tenant'] = {
                        attributeconf: {
                            description_localized: CMDBuildUI.util.Utilities.getTenantLabel()
                        },
                        cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant
                    };
                }
            }
        },

        _addActivityFieldIfNeeded: function (model, store, fields) {
            if (model.objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                store.add({
                    value: '_activity_definition',
                    label: CMDBuildUI.locales.Locales.common.tabs.activity,
                    group: ''
                });
                fields['_activity_definition'] = {
                    attributeconf: {
                        description_localized: CMDBuildUI.locales.Locales.common.tabs.activity
                    },
                    cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.activity
                };

            }
        },

        _addModelFields: function (model, store, fields) {
            model.getFields().forEach(function (field) {
                var attrconf = field.attributeconf;
                if (!Ext.String.startsWith(field.name, "_") && !field.hidden && !attrconf.password && !attrconf.hideInFilter) {
                    fields[field.name] = field;
                    store.addSorted(Ext.create("CMDBuildUI.model.base.ComboItem", {
                        value: field.name,
                        label: attrconf.description_localized || field._localized_description || field.description,
                        group: attrconf._group_description_translation || attrconf._group_description || CMDBuildUI.locales.Locales.common.attributes.nogroup
                    }));
                }
            });
        }
    }
});