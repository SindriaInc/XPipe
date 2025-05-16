Ext.define('CMDBuildUI.view.map.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-container',
    listen: {
        global: {
            objectidchanged: 'onObjectIdChanged',
            applythematism: 'onApplyThematism',
            cardcreated: 'refreshBtnClick',
            cardupdated: 'refreshBtnClick',
            carddeleted: 'onCardDeleted'
        },
        component: {
            'map-tab-tabpanel': {
                editgeovalue: 'onEditGeovalue',
                addgeovalue: 'onAddGeoValue',
                removegeovalue: 'onRemoveGeoValue',
                cleanmap: 'onCleanMap',
                loadfeatures: 'onLoadFeatures',
                animategeovalue: 'onAnimateGeovalue',
                hideshowlayer: 'onHideShowLayer'
            }
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            selectdeselectall: 'onSelectDeselectAllMap'
        }
    },

    onBeforeRender: function (view) {
        var me = this,
            container = view.getCardView(),
            vm = view.lookupViewModel();
        if (container) {
            var refreshBtn = container.lookupReference('refreshBtn');
            if (refreshBtn) {
                refreshBtn.addListener('click', function (refreshBtn, e, eOpts) {
                    this.refreshBtnClick()
                }, this);
            }

            view.lookupViewModel().bind({
                item: '{theObjectTypeCalculation}'
            }, function (data) {
                var addbutton = container.down("#addcardgis"),
                    item = data.item;
                if (item) {
                    addbutton.setText(CMDBuildUI.locales.Locales.classes.cards.addcard + ' ' + item.getTranslatedDescription());

                    var permission = CMDBuildUI.model.base.Base.permissions.add;
                    if (item.isProcess) {
                        permission = CMDBuildUI.model.base.Base.permissions.start;
                    }

                    function getGeoRecords(attrs, item) {
                        var georecords = []
                        Ext.Array.forEach(attrs, function (r, i, allitems) {
                            if ((r.get("subtype") !== "shape" && r.get("subtype") !== "geotiff") && item.getId() === r.get("owner_type")) {
                                georecords.push(r);
                            }
                        })
                        return georecords;
                    }

                    function createMenu(menu, records, child) {
                        Ext.Array.forEach(records, function (geoattribute, index, allitems) {
                            var menuitem = menu.add({
                                text: geoattribute.get("text"),
                                _itemdescription: geoattribute.get("text"),
                                _geoattribute: geoattribute,
                                iconCls: CMDBuildUI.model.menu.MenuItem.icons.klass,
                                disabled: geoattribute.get("writable") ? false : true,
                                listeners: {
                                    click: {
                                        fn: me.onAddGisButtonClick,
                                        scope: me
                                    }
                                }
                            });
                            if ((child && !child.get(permission)) || !Ext.Array.contains(geoattribute.get("visibility"), item.getId())) {
                                menuitem.disable();
                            }
                        });
                    }

                    item.getGeoAttributes().then(function (attributes) {
                        // set geoAttributes store
                        vm.set("geoAttributesStore", attributes);

                        if (item.get("prototype")) {

                            addbutton.setMenu([]);
                            var menu = addbutton.getMenu();

                            item.getChildren(true).forEach(function (child) {
                                child.getGeoAttributes().then(function (attributes) {
                                    var records = getGeoRecords(attributes.getRange(), child);
                                    if (!Ext.isEmpty(records)) {
                                        createMenu(menu, records, child);
                                        addbutton.enable(true);
                                    }
                                });
                            });

                        } else if (item.get(permission)) {

                            var records = getGeoRecords(attributes.getRange(), item);
                            if (records.length === 1) {
                                geoRecord = records[0];
                                geoRecord.get("writable") ? addbutton.enable() : addbutton.disable();
                                addbutton.setText(CMDBuildUI.locales.Locales.classes.cards.addcard + " " + geoRecord.get("text"));
                                addbutton._itemdescription = geoRecord.get("text");
                                addbutton._geoattribute = geoRecord;
                                addbutton.addListener("click", me.onAddGisButtonClick, me);
                            } else if (records.length > 1) {
                                addbutton.setMenu([]);
                                addbutton.enable();

                                var menu = addbutton.getMenu();
                                createMenu(menu, records);
                            }

                        }
                    });
                }
            })
        }
    },

    onEditGeovalue: function (config) {
        this.getView().lookupReference('map').modify(config);
    },

    onAddGeoValue: function (config) {
        this.getView().lookupReference('map').draw(config);
    },

    onRemoveGeoValue: function (config) {
        this.getView().lookupReference('map').clear(config);
    },

    onHideShowLayer: function (config) {
        this.getView().lookupReference('map').hideshow(config);
    },

    onCleanMap: function (record) {
        this.getView().lookupReference('map').clean(record);
    },
    onLoadFeatures: function (replace) {
        this.getView().lookupReference('map').getController().delayloadfeatures(replace || true);
    },

    onObjectIdChanged: function (newId) {
        this.getView().setObjectId(Ext.num(newId));
    },

    onApplyThematism: function (id) {
        var currentThematismId = this.getView().getThematismId();
        if (!Ext.isEmpty(currentThematismId) && currentThematismId == id) {
            //if the applied thematism is modifyed by the panel

            this.refreshThematism();
        } else {
            this.getView().setThematismId(id);
        }
    },

    /**
     * 
     * @param {CMDBuildUI.model.gis.GeoValue} config.geovalue
     */
    onAnimateGeovalue: function (config) {
        this.getView().lookupReference('map').getController().animateMap(config.geovalue);
    },


    onAddGisButtonClick: function (btn, eOpts) {
        var me = this,
            georecord = btn._geoattribute.createEmptyGeoValue(),
            gisButton = btn.getItemId() == "addcardgis" ? btn : btn.up("#addcardgis");

        gisButton.setPressed(true);
        if (gisButton.addGeoAttribute) {
            return;
        }
        gisButton.addGeoAttribute = true;

        this.onAddGeoValue({
            record: georecord,
            listeners: {
                drawend: {
                    fn: function (point) {
                        var popup = CMDBuildUI.util.Navigation.getManagementDetailsWindow() || CMDBuildUI.util.Navigation.getManagementDetailsWindow(true);
                        popup.removeAll();
                        popup.add({
                            xtype: 'classes-cards-card-create',
                            padding: 10,
                            viewModel: {
                                data: {
                                    objectTypeName: btn._geoattribute.get("owner_type"),
                                    action: 'new',
                                    detailsWindowTitle: CMDBuildUI.locales.Locales.classes.cards.addcard + " " + btn._itemdescription
                                }
                            },
                            defaultValuesForCreate: {
                                _point: point
                            },
                            buttons: [{
                                text: CMDBuildUI.locales.Locales.common.actions.save,
                                formBind: true, //only enabled once the form is valid
                                disabled: true,
                                ui: 'management-action-small',
                                autoEl: {
                                    'data-testid': 'map-card-create-save'
                                },
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                                },
                                handler: function (button, e) {
                                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                                    var cancelBtn = this.up().down("#cancelbtn");
                                    button.showSpinner = true;
                                    CMDBuildUI.util.Utilities.disableFormButtons([button, cancelBtn]);
                                    popup.down("classes-cards-card-create").getController().saveForm().then(function (record) {
                                        if (georecord.get("_type") === "point") {
                                            georecord.set('x', point.x);
                                            georecord.set('y', point.y);
                                        } else {
                                            georecord.set("points", point);
                                        }

                                        Ext.Ajax.request({
                                            url: CMDBuildUI.util.api.Classes.getGeoValuesUrl(
                                                record.get('_type'),
                                                record.get('_id')) + Ext.String.format('/{0}', georecord.get('_attr')),
                                            method: 'PUT',
                                            jsonData: georecord.getJsonData(),
                                            callback: function () {
                                                me.onCleanMap(georecord);
                                                me.redirectTo(CMDBuildUI.util.Navigation.getClassBaseUrl(
                                                    record.get("_type"),
                                                    record.getId()
                                                ));
                                                popup.close();
                                                gisButton.setPressed(false);
                                                gisButton.addGeoAttribute = false;
                                                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                            }
                                        });
                                    }).otherwise(function () {
                                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                        CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);
                                    });
                                }
                            }, {
                                text: CMDBuildUI.locales.Locales.common.actions.cancel,
                                itemId: 'cancelbtn',
                                ui: 'secondary-action-small',
                                handler: function () {
                                    popup.close();
                                    gisButton.setPressed(false);
                                    gisButton.addGeoAttribute = false;
                                }
                            }]
                        });
                        popup.on('close', function () {
                            me.onCleanMap(georecord);
                            gisButton.setPressed(false);
                            gisButton.addGeoAttribute = false;
                        });
                    },
                    scope: this
                }
            },
            operation: 'add'
        });
    },

    /**
     * Select or deselect all items on map, list tab and navigation tree tab based on the value of select
     * @param {Boolean} select 
     */
    onSelectDeselectAllMap: function (select) {
        var view = this.getView(),
            viewMap = view.getViewMap(),
            interaction = viewMap.getOl_interaction_select(viewMap.getOlMap()),
            listTab = view.getListTab(),
            navTreeTab = view.getNavigationTreeTab();

        view.getViewModel().set("settingsMap.selectAll", select);

        if (select) {
            var listStore = listTab.getStore(),
                navTreeStore = navTreeTab.getStore(),
                features = viewMap.getFeatureStore().getRange(),
                objectTypeName = viewMap.getViewModel().get("map.objectTypeName"),
                selected = [];

            Ext.Array.forEach(features, function (item, index, allitems) {
                if (objectTypeName == item.get("_owner_type")) {
                    selected.push(item.getOlFeature());
                }
            });

            interaction.dispatchEvent(new ol.interaction.Select.Event('select', selected, [], {
                selectAll: true
            }));

            listTab.getSelectionModel().select(listStore.getRange(0, listStore.getTotalCount()), true, true);
            Ext.Array.forEach(navTreeStore.getRange(0, navTreeStore.getTotalCount()), function (item, index, allitems) {
                var type = item.get("type");
                if (type == objectTypeName && type != item.get("description")) {
                    navTreeTab.getSelectionModel().select(item, true, true);
                }
            });
        } else {
            interaction.dispatchEvent(new ol.interaction.Select.Event('select', [], [], {
                deselectAll: true
            }));
            interaction.getFeatures().clear();
            listTab.getSelectionModel().deselectAll(true);
            navTreeTab.getSelectionModel().deselectAll(true);
        }
    },

    privates: {

        /**
         * This function removes the current map-container.theThematism and after that sets it again. In this process the thematismId doesn't change
         */
        refreshThematism: function () {
            var view = this.getView();

            var theThematism = view.getTheThematism();
            view.setTheThematism(null);

            view.getViewModel().bind({
                theThematism: '{map-container.theThematism}'
            }, Ext.Function.bind(thematismHandler, this, [theThematism], 0), this, {
                single: true
            });

            function thematismHandler(theThematism, data) {
                if (!data.theThematism) {
                    //sets the configuration in the view
                    theThematism.calculateResults(function () {
                        view.setTheThematism(theThematism)
                    }, this);
                } else {
                    view.getViewModel().bind({
                        theThematism: '{map-container.theThematism}'
                    }, Ext.Function.bind(thematismHandler, this, [theThematism], 0), this, {
                        single: true
                    });
                }
            }
        },

        /**
         * this function removes the current map-container.advancedfilter
         */
        refreshAdvancedfilter: function () {
            var view = this.getView();

            var advancedfilter = view.getAdvancedfilter();
            view.setAdvancedfilter(null);

            view.getViewModel().bind({
                advancedfilter: '{map-container.advancedfilter}'
            }, Ext.Function.bind(advancedfilterHandler, this, [advancedfilter], 0), this, {
                single: true
            });

            function advancedfilterHandler(advancedfilter, data) {
                if (!data.advancedfilter) {
                    //sets the configuration in the view
                    view.setAdvancedfilter(advancedfilter);
                } else {
                    view.getViewModel().bind({
                        advancedfilter: '{map-container.theThematism}'
                    }, Ext.Function.bind(advancedfilterHandler, this, [advancedfilter], 0), this, {
                        single: true
                    });
                }
            }
        },

        refreshBtnClick: function () {
            var view = this.getView(),
                mapView = view.getViewMap();

            view.items.each(function (item, index, array) {
                if (Ext.isFunction(item.refreshBtnClick)) {
                    item.refreshBtnClick.call(item)
                }
            }, this);

            if (mapView.infoWindow._current) {
                mapView.ol_interaction_select_refresh(mapView.getViewModel().get('map').olMap, true);
            }
        },

        onCardDeleted: function () {
            var view = this.getView(),
                mapView = view.getViewMap(),
                tabpanel = view.getMapTabPanel();
            tabpanel.setTheObject(null);
            tabpanel.getViewModel().set("map-tab-tabpanel.objectId", null);
            mapView.infoWindow._current = null;
            mapView.infoWindow._ownerId = null;
            this.refreshBtnClick();
        }
    }
});