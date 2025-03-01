Ext.define('CMDBuildUI.view.administration.content.dashboards.card.FormHelper', {
    singleton: true,

    /**
     * Get general properties fieldset
     * 
     * @param {String} mode display|edit|both
     * @return {Ext.form.FieldSet} The url for api resourcess
     */
    getGeneralProperties: function (mode, view) {
        var me = this;
        var fieldset = {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",
            layout: 'column',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },

            items: [
                me.getRow([
                    me.getName(mode),
                    me.getDescription(mode)
                ]),
                me.getRow([
                    me.getActive(mode)
                ])
            ]
        };

        return fieldset;
    },

    /**
     * Get general properties fieldset
     * 
     * @param {String} mode display|edit|both
     * @return {Ext.form.FieldSet} The url for api resourcess
     */
    getLayout: function (mode, view) {
        if (!mode) {
            mode = 'both';
        }
        var me = this;
        var vm = view.lookupViewModel();
        vm.set('chartsData', vm.getStore('chartsStore').getRange());

        var charts = vm.getStore('chartsStore').getRange();
        Ext.Array.forEach(charts, function (chart) {
            chart.set('descriptionWithName', chart.get('label'));
        });

        var fieldset = {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",
            layout: 'fit',
            maxHeight: view.getHeight() -100,
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.dashboards.layout,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dashboards.layout'
            },

            items: [
                me.getRow([
                    me.getLayoutGrid(view),
                    me.getOrginChartContainer(charts)
                ], {
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    }
                })
            ]
        };
        var cont = {
            xtype: 'container',
            layout: 'fit',
            forceFit: true,
            items: [fieldset]
        };
        return cont;
    },

    privates: {
        getRow: function (items, config) {

            var fieldcontainer = Ext.merge({}, {
                xtype: 'container',
                layout: 'column',
                cls: 'row-container',
                columnWidth: 1
            }, config || {});
            if (items && items.length) {
                // fieldcontainer.columnWidth = items.length;
                fieldcontainer.items = items;
            }
            return fieldcontainer;

        },

        // General properties fieldset
        getDescription: function (mode) {
            // description
            var propertyName = 'description';
            var config = {};
            config[propertyName] = {
                fieldcontainer: {
                    userCls: 'with-tool',
                    allowBlank: false
                },
                allowBlank: false,
                bind: {
                    value: '{theDashboard.description}'
                }
            };


            config[propertyName].fieldcontainer.labelToolIconCls = CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid');
            config[propertyName].fieldcontainer.labelToolIconQtip = CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate;
            config[propertyName].fieldcontainer.labelToolIconClick = 'onTranslateClickDescription';

            return CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput(config);
        },

        getName: function (mode) {
            // content 
            var propertyName = 'name';
            var config = {};
            config[propertyName] = {
                fieldcontainer: {
                    userCls: 'with-tool',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.extendeddescription
                },
                allowBlank: false,
                bind: {
                    value: '{theDashboard.name}'
                }
            };

            return CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput(config, true, '[name="description"]');
        },

        // 
        getActive: function (mode) {
            // active
            var config = {};
            var propertyName = 'active';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dashboards.active'
                    }
                },
                bind: {
                    value: '{theDashboard.active}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput(config, propertyName, false);
        },

        getLayoutGrid: function (view) {
            return {
                xtype: 'container',
                itemId: 'layoutcontainer',
                flex: 0.75,                
                scrollable: 'y'
            };
        },

        getOrginChartContainer: function (data) {

            return {
                flex: 0.25,
                xtype: 'administration-content-dashboards-card-builder-column',
                style: {
                    height: 40 * data.length
                },
                bind: {
                    hidden: '{actions.view}'
                },
                isAllFreeItems: true,
                store: Ext.create('Ext.data.Store', {
                    fields: ['label'],
                    proxy: {
                        type: 'memory'
                    },
                    data: data,
                    sorters: ['label'],
                    autoDestroy: true,
                    autoLoad: false
                })
            };
        }

    }
});