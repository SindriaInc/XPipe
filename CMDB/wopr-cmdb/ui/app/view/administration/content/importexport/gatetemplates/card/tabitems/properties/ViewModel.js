Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-importexport-gatetemplates-card-tabitems-properties-view',
    data: {
        formModeCls: 'formmode-view',
        toolbarHiddenButtons: {
            edit: true, // action !== view
            print: true, // action !== view
            disable: true,
            enable: true
        },
        filterByTypeGate: [],
        toolAction: {
            _canClone: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        },
        classGeolayerStoreData: null
    },
    formulas: {
        geoserverDisabledMessage: {
            get: function () {
                return CMDBuildUI.locales.Locales.administration.gates.geoserverdisabledmessage;
            }
        },
        geoserverEnabled: {
            get: function () {
                return CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.geoserverEnabled);
            }
        },
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_etl_modify}'
            },
            get: function (data) {
                this.set('toolAction._canClone', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },

        getToolbarButtons: {
            bind: '{theGate.active}',
            get: function (get) {
                this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.print', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.disable', true);
                this.set('toolbarHiddenButtons.enable', false);
            }
        },
        updateToolbarButtons: {
            bind: '{theGate.active}',
            get: function (data) {
                if (data) {
                    this.set('toolbarHiddenButtons.disable', false);
                    this.set('toolbarHiddenButtons.enable', true);
                } else {
                    this.set('toolbarHiddenButtons.disable', true);
                    this.set('toolbarHiddenButtons.enable', false);
                }
            }
        },
        action: {
            bind: {
                isView: '{actions.view}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}'
            },
            get: function (data) {
                this.set('activeTab', this.getView().up('administration-content').getViewModel().get('activeTabs.gatetemplate') || 0);
                if (data.isView) {
                    this.set('formModeCls', 'formmode-view');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit) {
                    this.set('formModeCls', 'formmode-add');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    this.set('formModeCls', 'formmode-add');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                }
            },
            set: function (value) {
                this.getParent().set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.getParent().set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.getParent().set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },

        theHandler: {
            bind: '{theGate}',
            get: function (theGate) {
                if (theGate) {
                    return this.get('theGate').handlers().first();
                }
            }
        },

        enableShapeManager: {
            bind: {
                shapeEnabled: '{theHandler.shape_import_enabled}',
                type: '{gateType}'
            },
            get: function (data) {

                if ((data.type === 'cad' || data.type === 'gis') && data.shapeEnabled === false) {
                    this.set('theHandler.shape_import_target_class', null);
                    this.set('theHandler.shape_import_target_attr', null);
                    this.set('theHandler.shape_import_key_source', null);
                    this.set('theHandler.shape_import_key_attr', null);
                }
            }
        },

        shapeImportOrExcludeHiddenManager: {
            bind: {
                type: '{gateType}',
                includeOrExclude: '{theHandler._shape_import_include_or_exclude}'
            },
            get: function (data) {
                if (data.type === 'cad' || data.type === 'gis') {
                    if (data.includeOrExclude) {
                        this.set('shapeImportExcludeHidden', data.includeOrExclude !== CMDBuildUI.model.importexports.GateGisHandler.includeOrExclude.exclude);
                        this.set('shapeImportIncludeHidden', data.includeOrExclude !== CMDBuildUI.model.importexports.GateGisHandler.includeOrExclude.include);
                    }
                }

            }
        },

        shapeImportIncludeOrExludeData: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getShapeIncludeOrExcludeData();
        },


        classGeolayerStoreDataManager: {
            bind: {
                className: '{theHandler.shape_import_target_class}',
                type: '{gateType}'
            },
            get: function (data) {
                if (data.type === 'cad' || data.type === 'gis') {
                    var me = this;
                    if (!data.className.length) {
                        me.set('classGeolayerStoreData', undefined);
                        me.set('classAttributesStoreData', undefined);
                    } else {
                        var klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.className);
                        klass.getGeoAttributes(true).then(function (geoattributesStore) {
                            if (!me.destroyed) {
                                var geoattributes = [];
                                geoattributesStore.each(function (item) {
                                    if (['shape', 'geotiff'].indexOf(item.get('type')) > -1) {
                                        if (item.get('name') === me.get('theHandler.shape_import_target_attr')) {
                                            me.set('theHandler._shape_import_target_attr_description', item.get('description'));
                                        }
                                        geoattributes.push({
                                            label: item.get('description'),
                                            value: item.get('name')
                                        });
                                    }
                                });
                                me.set('classGeolayerStoreData', geoattributes);
                            }
                        });

                        klass.getAttributes(true).then(function (attributesStore) {
                            if (!me.destroyed) {
                                var attributes = [];
                                var allowedAttributes = ['Notes'];
                                var allowTenantAttribute = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled);
                                if (allowTenantAttribute) {
                                    allowedAttributes.push('IdTenant');
                                }
                                attributesStore.each(function (item) {
                                    if (item.canAdminShow(allowedAttributes)) {
                                        attributes.push({
                                            label: item.get('description'),
                                            value: item.get('_id')
                                        });
                                    }
                                });
                                me.set('classAttributesStoreData', attributes);
                            }
                        });
                    }

                }

            }
        },

        sourceTypes: function (get) {
            return CMDBuildUI.util.administration.helper.ModelHelper.getSourceTypes();
        },
        jdbcClasses: function (get) {
            return CMDBuildUI.util.administration.helper.ModelHelper.getJdbcDrivers();
        },
        jdbcPasswordEmptyText: {
            bind: '{theGate.config.jdbcPassword}',
            get: function (jdbcPassword) {
                if (jdbcPassword) {
                    return jdbcPassword.replace(/./g, "•");
                }
            }
        }
    },

    stores: {
        shapeImportIncludeOrExludeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{shapeImportIncludeOrExludeData}'
        },
        classGeolayerStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{classGeolayerStoreData}'
        },
        classAttributesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{classAttributesStoreData}'
        },
        sourceTypeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            data: '{sourceTypes}',
            autoDestroy: true
        },
        jdbcDriverClassNameStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            data: '{jdbcClasses}',
            autoDestroy: true
        },
        allEmailTemplates: {
            type: 'chained',
            source: 'emails.Templates',
            filters: [function (template) {
                return template.get('provider') === 'email' && template.get('active') === true;
            }],
            autoLoad: true,
            autoDestroy: true
        }

    }
});