Ext.define('CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoattributesGridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-tab-cards-geoattributesgrid-geoattributesgrid',
    data: {
        buttonsSaveCancel: {
            hidden: true,
            saveDisabled: true
        }
    },

    formulas: {
        storeCalculation: {
            bind: {
                theObject: '{map-geoattributes-grid.theObject}'
            },
            get: function (data) {
                if (data.theObject) {

                    var objectTypeName = data.theObject.get('_type'),
                        objectTypeInstance = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName),
                        promises = [
                            objectTypeInstance.getGeoAttributes(),
                            data.theObject.getGeoValues(),
                            data.theObject.getGeoLayers()
                        ];

                    Ext.Deferred.all(promises).then(function (resolved) {
                        if (this.getView() && !this.getView().destroyed) {
                            var geovalues = resolved[1],
                                geolayers = resolved[2],
                                geoattributes = Ext.create('Ext.data.ChainedStore', {
                                    source: resolved[0],
                                    filters: [{
                                        property: 'owner_type',
                                        operator: 'in',
                                        value: objectTypeInstance.getHierarchy()
                                    }]
                                });

                            var d = Ext.Array.map(geoattributes.getRange(), function (geoattribute, index, array) {
                                var i = geovalues.find('ollayername', geoattribute.get('ollayername')),
                                    l = geolayers.find('ollayername', geoattribute.get('ollayername')),
                                    description = geoattribute.get('_description_translation'),
                                    geovalue;
                                if (i != -1) {
                                    geovalue = geovalues.getAt(i);
                                } else if (l != -1) {
                                    geovalue = geolayers.getAt(l);
                                }
                                if (geovalue) {
                                    geovalue.set('_attr_description', description);
                                    geovalue.set('_can_write', geoattribute.get('writable'));
                                    //the geovalue exists for the given geoAttribute
                                    return geovalue;
                                } else {
                                    //the geovalue doesn't exists for the given geoAttribute
                                    return geoattribute.createEmptyGeoValue(data.theObject.getId());
                                }
                            }, this);

                            var store = Ext.create('Ext.data.Store', {
                                model: 'CMDBuildUI.model.gis.GeoValue',
                                data: d
                            });
                            this.getView().setStore(store);
                        }

                    }, Ext.emptyFn, Ext.emptyFn, this);
                }
            }
        }
    }
});
