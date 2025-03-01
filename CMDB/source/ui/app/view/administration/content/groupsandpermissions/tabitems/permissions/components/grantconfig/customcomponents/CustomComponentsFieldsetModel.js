Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.customcomponents.CustomComponentsFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-customcomponents-customcomponentsfieldset',
    data: {
        fieldsetTitle: null,
        fieldsetHidden: true,
        // componentType: null,
        // grant: null,
        gridData: null
    },

    formulas: {
        configManager: {
            bind: {
                componentType: '{componentType}',
                grant: '{grant}'
            },
            get: function (data) {
                var me = this;
                var theObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.grant.get('objectTypeName'));

                this.set('theObject', theObject);
                var gridData;
                switch (data.componentType) {
                    case 'widget':
                        // if the object does not contain widgets, the fieldset must be hidden
                        gridData = me.get('gridData') || [];
                        theObject.widgets().each(function (widget) {
                            var show = data.grant.get(Ext.String.format('_widget_{0}_access', widget.get('WidgetId')));                            
                            var dataForGrid = {
                                id: widget.get('WidgetId'),
                                name: widget.get('_label'),
                                description: widget.get('_label'),
                                // _widget_ < codicewidget > _access: true / false
                                show: (Ext.isEmpty(show)) ? true : show
                            };
                            gridData.push(dataForGrid);
                        });
                        me.set('fieldsetHidden', !gridData.length);
                        me.set('gridData', gridData);
                        me.set('descriptionLabel', CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.widget);
                        me.set('fieldsetTitle', 'Widgets');
                        break;
                    case 'contextmenu':
                        // if the object does not contain contextmenus, the fieldset must be hidden
                        gridData = me.get('gridData') || [];
                        theObject.contextMenuItems().each(function (contextMenuItem) {
                            if (!contextMenuItem.get('separator')) {
                                
                                // _contextmenu_ < codicemenu > _access: true / false
                                var key = Ext.String.format('_contextmenu_{0}_access', contextMenuItem.get('componentId'));
                                var show = data.grant.get(key);                                
                                
                                //data.grant.set(key, (Ext.isEmpty(show)) ? true : show);
                                var dataForGrid = {
                                    id: contextMenuItem.get('componentId'),
                                    name: contextMenuItem.get('label'),
                                    description: contextMenuItem.get('label'),
                                    show: (Ext.isEmpty(show)) ? true : show
                                };
                                gridData.push(dataForGrid);
                            }
                        });
                        me.set('fieldsetHidden', !gridData.length);
                        me.set('gridData', gridData);
                        me.set('descriptionLabel', CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.contextmenuitem);
                        me.set('fieldsetTitle', CMDBuildUI.locales.Locales.administration.common.labels.contextmenu);
                        break;
                    default:
                        break;
                }
            }
        }
    },

    stores: {
        gridDataStore: {
            fields: ['name', 'description', 'show'],
            proxy: {
                type: 'memory'
            },
            data: '{gridData}'
        }
    }

});