Ext.define('CMDBuildUI.view.administration.content.dms.models.tabitems.properties.fieldsets.FormWidgetFieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-models-tabitems-properties-fieldsets-formwidgetfieldset',
    mixins: ['CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin'],

    onAddNewWidgetMenuBtn: function (grid, rowIndex, colIndex) {

        var vm = this.getViewModel();
        var widgets = vm.get('theModel.widgets');
        var newWidgetStore = vm.get('formWidgetsStoreNew');
        var newWidget = newWidgetStore.getData().first();

        var invalid = false;
        // label can't be blank
        if (!newWidget.get('_label')) {
            grid.down('#widgetLabel').markInvalid(CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.contextMenus.cantbeempty);
            invalid = true;
        }

        // type can't be blank
        if (!newWidget.get('_type')) {
            grid.down('#widgetType').markInvalid(CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.contextMenus.cantbeempty);
            invalid = true;
        }
        if (invalid) {
            return false;
        }

        Ext.suspendLayouts();
        var uuid = Math.random().toString(36).substring(2) + Math.random().toString(36).substring(2);
        newWidget.set('_id', uuid);
        newWidget.set('WidgetId', uuid);
        newWidget.set('_config', (newWidget.get('_config') + '\nWidgetId="' + uuid + '"').trim());
        var clonedRecord = CMDBuildUI.model.WidgetDefinition.create(newWidget.getData());
        widgets.add(clonedRecord);
        newWidgetStore.rejectChanges();
        grid.refresh();
        vm.getParent().set('formWidgetCount', widgets.data.length);
        this.lookupReference('formWidgetGrid').view.grid.getView().refresh();
        window.newFormWidgetScriptField.getSession().setValue('');
        Ext.resumeLayouts();

    },
    onEditBtn: function (view, rowIndex, colIndex) {
        var vm = this.getViewModel();
        var grid = this.lookupReference('formWidgetGrid');
        var theWidget = grid.getStore().getAt(rowIndex);
        vm.set('theWidget', CMDBuildUI.util.administration.helper.ModelHelper.setReadState(theWidget));
        var formFields = [{
            xtype: 'container',
            //flex: 2,

            padding: '0 10 0 10',
            items: [{
                /********************* Triggers **********************/
                xtype: 'fieldcontainer',
                flex: 2,
                columns: 1,
                vertical: true,
                viewModel: {
                    data: {
                        widgetTypesStore: Ext.copy(vm.get('widgetTypesStore'))
                    }
                },
                fieldDefaults: {
                    labelAlign: 'top'
                },
                items: [{
                    xtype: 'textfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                    },
                    bind: {
                        value: '{theWidget._label}'
                    }
                }, {
                    xtype: 'combo',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.type,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
                    },
                    editable: false,
                    forceSelection: true,
                    allowBlank: false,
                    displayField: 'label',
                    valueField: 'value',
                    bind: {
                        store: '{widgetTypesStore}',
                        value: '{theWidget._type}'
                    }
                }, {
                    xtype: 'checkboxgroup',
                    userCls: 'hideCellCheboxes',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    flex: 1,
                    columns: 1,
                    vertical: false,
                    items: [{
                        xtype: 'checkbox',
                        bind: {
                            value: '{theWidget._active}'
                        },
                        value: view.grid.getStore().getAt(rowIndex).get('_active')
                    }]
                }]
            }]
        }];
        var popup = CMDBuildUI.util.administration.helper.AcePopupHelper.getPopup('theWidget', theWidget, '_config', formFields, 'popup-edit-fromwidget', CMDBuildUI.locales.Locales.administration.classes.strings.editformwidget);
    }
});