Ext.define('CMDBuildUI.view.classes.cards.clonerelations.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-clonerelations-container',

    data: {
        storeinfo: {
            data: []
        }
    },

    formulas: {
        updateStoreData: {
            bind: {
                id: '{objectId}',
                name: '{objectTypeName}'
            },
            get: function (data) {
                var domains = [];
                var me = this;
                var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.name);
                var removedDomains = [];
                var hierarchy = item.getHierarchy();

                Ext.Promise.all([
                    item.getAttributes(),
                    item.getDomains()
                ]).then(function (response) {
                    var itemattributes = response[0].getRange();
                    var itemdomains = response[1].getRange();

                    Ext.Array.each(itemattributes, function (attribute) {
                        if (attribute.get('type') === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase()) {
                            var element = {
                                domain: attribute.get('domain'),
                                direction: attribute.get('direction')
                            };
                            removedDomains.push(element);
                        }
                    });

                    Ext.Array.each(itemdomains, function (d) {
                        // direct domain
                        if (
                            Ext.Array.contains(hierarchy, d.get("source")) &&
                            !Ext.Array.contains(d.get("disabledSourceDescendants"), data.name)
                        ) {
                            addToDomainsList(
                                d,
                                d.get("destination"),
                                d.get("_descriptionDirect_translation") || d.get("descriptionDirect"),
                                d.get("destinationProcess"),
                                "direct"
                            );
                        }

                        // inverse domain
                        if (
                            Ext.Array.contains(hierarchy, d.get("destination")) &&
                            !Ext.Array.contains(d.get("disabledDestinationDescendants"), data.name)
                        ) {
                            addToDomainsList(
                                d,
                                d.get("source"),
                                d.get("_descriptionInverse_translation") || d.get("descriptionInverse"),
                                d.get("sourceProcess"),
                                "inverse"
                            );
                        }

                    });
                    me.set("storeinfo.data", domains);


                    function addToDomainsList(domain, destination, description, destIsProcess, direction) {
                        var matchingArrays = Ext.Array.filter(removedDomains, function (element) {
                            return element.domain === domain.getId() && element.direction === direction;
                        });

                        var cardinality = domain.get('cardinality');
                        var isDisabled = false;
                        if ((cardinality == CMDBuildUI.model.domains.Domain.cardinalities.onetomany && direction == 'direct') ||
                            (cardinality == CMDBuildUI.model.domains.Domain.cardinalities.onetoone) ||
                            (cardinality == CMDBuildUI.model.domains.Domain.cardinalities.manytoone && direction == 'inverse')) {
                            isDisabled = true;
                        }
                        if (destination && !matchingArrays.length) {
                            // get destination object
                            var destObj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                destination,
                                destIsProcess ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass
                            );

                            if (destObj) {
                                // add domain in list
                                domains.push({
                                    domain: domain.getId(),
                                    description: description,
                                    destination: destination,
                                    destinationDescription: destObj.getTranslatedDescription(),
                                    destinationIsProcess: destIsProcess,
                                    direction: direction,
                                    isDisabled: isDisabled,
                                    ignore: null,
                                    migrates: null,
                                    clone: null,
                                    mode: null
                                });
                            }

                        }
                    }

                });
            }
        }
    },

    stores: {
        relations: {
            model: 'CMDBuildUI.model.domains.Clone',
            storeId: 'relations-clone',
            autoDestroy: true,
            data: '{storeinfo.data}',
            groupField: '_type',
            proxy: {
                type: 'memory'
            }
        }
    }

});