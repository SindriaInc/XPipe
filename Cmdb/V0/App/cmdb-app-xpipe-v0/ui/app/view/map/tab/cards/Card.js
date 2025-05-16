
Ext.define('CMDBuildUI.view.map.tab.cards.Card', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.CardController',
        'CMDBuildUI.view.map.tab.cards.CardModel'
    ],
    alias: 'widget.map-tab-cards-card',
    controller: 'map-tab-cards-card',
    viewModel: {
        type: 'map-tab-cards-card'
    },

    autoScroll: true,
    items: [{
        title: CMDBuildUI.locales.Locales.gis.geographicalAttributes,
        xtype: 'fieldset',
        ui: 'formpagination',
        localized: {
            text: 'CMDBuildUI.locales.Locales.gis.geographicalAttributes'
        },
        items: [{
            xtype: 'map-tab-cards-geoattributesgrid-geoattributesgrid',
            bind: {
                theObject: '{map-tab-tabpanel.theObject}'
            }
        }]
    }], //other items are added in the controller

    refreshTheObject: function (theObject) {
        var objectId = theObject.getId();
        var objectTypeName = theObject.get('_type');
        var objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectTypeName);

        switch (objectType) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                this.remove('classes-cards-card-view');
                this.add(this.getCardObject(objectType, objectTypeName, objectId));
                break;
            default:
                CMDBuildUI.util.Logger.log(
                    Ext.String.format('Object Type not implemented card tab: {0}', objectType),
                    CMDBuildUI.util.Logger.levels.debug);
                break;
        }
        // }
    },

    getCardObject: function (objectType, objectTypeName, objectId) {
        var me = this;
        return {
            xtype: 'classes-cards-card-view',
            itemId: 'classes-cards-card-view',
            shownInPopup: true,
            hideTools: false,
            viewModel: {
                data: {
                    objectTypeName: objectTypeName,
                    objectId: objectId,
                    objectType: 'class'
                },
                formulas: {
                    canUpdate: {
                        bind: {
                            _can_update: '{theObject._model.' + CMDBuildUI.model.base.Base.permissions.edit + '}'
                        }, get: function (data) {
                            return !data._can_update;
                        }
                    }
                }
            },
            hideInlineElements: false,
            tabpaneltools: [{
                xtype: 'tool',
                itemId: 'opentool',
                iconCls: 'x-fa fa-external-link',
                cls: 'management-tool',
                action: CMDBuildUI.mixins.DetailsTabPanel.actions.view,
                tooltip: CMDBuildUI.locales.Locales.classes.cards.opencard,
                autoEl: {
                    'data-testid': 'cards-card-view-opentool'
                },
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.classes.cards.opencard'
                },
                listeners: {
                    click: Ext.Function.bind(this.clickHandler, this, [objectTypeName, objectId], 0)
                }
            }, {
                xtype: 'tool',
                iconCls: 'x-fa fa-ellipsis-v',
                cls: 'management-tool',
                autoEl: {
                    'data-testid': 'cards-card-view-openTabs'
                },
                callback: Ext.Function.bind(function (owner, tool, event) {
                    var configAttachments = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled),
                        classDef = me.up().getTheObject().get("_model"),
                        items = [];

                    // edit action
                    items.push({
                        tooltip: CMDBuildUI.locales.Locales.classes.cards.modifycard,
                        iconCls: 'x-fa fa-pencil',
                        cls: 'management-tool',
                        action: CMDBuildUI.mixins.DetailsTabPanel.actions.edit,
                        disabled: true,
                        bind: {
                            disabled: '{canUpdate}'
                        }
                    });

                    // details action
                    if (classDef[CMDBuildUI.model.users.Grant.permissions.detail_read]) {
                        items.push({
                            tooltip: CMDBuildUI.locales.Locales.common.tabs.details,
                            iconCls: 'x-fa fa-th-list',
                            cls: 'management-tool',
                            action: CMDBuildUI.mixins.DetailsTabPanel.actions.masterdetail
                        });
                    }

                    // notes action
                    if (classDef[CMDBuildUI.model.users.Grant.permissions.note_read]) {
                        items.push({
                            tooltip: CMDBuildUI.locales.Locales.common.tabs.notes,
                            iconCls: 'x-fa fa-sticky-note',
                            action: CMDBuildUI.mixins.DetailsTabPanel.actions.notes
                        });
                    }

                    // relations action
                    if (classDef[CMDBuildUI.model.users.Grant.permissions.relation_read]) {
                        items.push({
                            tooltip: CMDBuildUI.locales.Locales.common.tabs.relations,
                            iconCls: 'x-fa fa-link',
                            action: CMDBuildUI.mixins.DetailsTabPanel.actions.relations
                        });
                    }

                    // history action
                    if (classDef[CMDBuildUI.model.users.Grant.permissions.history_read]) {
                        items.push({
                            tooltip: CMDBuildUI.locales.Locales.common.tabs.history,
                            iconCls: 'x-fa fa-history',
                            action: CMDBuildUI.mixins.DetailsTabPanel.actions.history
                        });
                    }

                    // email action
                    if (classDef[CMDBuildUI.model.users.Grant.permissions.email_read]) {
                        items.push({
                            tooltip: CMDBuildUI.locales.Locales.common.tabs.emails,
                            iconCls: 'x-fa fa-envelope',
                            action: CMDBuildUI.mixins.DetailsTabPanel.actions.emails
                        });
                    }

                    // attachments action
                    if (configAttachments && classDef[CMDBuildUI.model.users.Grant.permissions.attachment_read]) {
                        items.push({
                            tooltip: CMDBuildUI.locales.Locales.common.tabs.attachments,
                            iconCls: 'x-fa fa-paperclip',
                            action: CMDBuildUI.mixins.DetailsTabPanel.actions.attachments
                        });
                    }

                    if (items.length) {
                        var menu = Ext.create('Ext.menu.Menu', {
                            autoShow: true,
                            items: items,
                            ui: 'actionmenu',
                            defaults: {
                                height: 32,
                                listeners: {
                                    click: Ext.Function.bind(this.clickHandler, this, [objectTypeName, objectId], 0)
                                }
                            },
                            viewModel: {
                                parent: tool.lookupViewModel()
                            }
                        });
                        menu.setMinWidth(35);
                        menu.setWidth(35);
                        menu.alignTo(tool.el.id, 't-b?');
                    }
                }, this)
            }],
            hideWidgets: true
        };
    },

    /**
     * 
     * @param {*} objectTypeName 
     * @param {*} objectId 
     * @param {*} tool 
     * @param {*} e 
     */
    clickHandler: function (objectTypeName, objectId, tool, e) {
        this.getController().redirectTo(CMDBuildUI.util.Navigation.getClassBaseUrl(objectTypeName, objectId, tool.action));
    },

    initComponent: function () {
        var tabpanel = this.up('map-tab-tabpanel')
        if (tabpanel) {
            this.relayEvents(tabpanel, ['refreshbtnclick'])
        }
        return this.callParent(arguments)
    }
});