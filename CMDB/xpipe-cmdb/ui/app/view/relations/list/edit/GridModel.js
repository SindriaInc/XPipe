Ext.define('CMDBuildUI.view.relations.list.edit.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-list-edit-grid',

    data: {
        searchvalue: null,
        firstload: true
    },

    formulas: {
        /**
         * Update store info
         */
        updateStoreInfo: {
            bind: {
                objecttypename: '{objectTypeName}',
                theDomain: '{theDomain}',
                direction: '{relationDirection}',
                theRelation: '{theRelation}'
            },
            get: function (data) {
                if (data.objecttypename) {
                    // store type
                    var object = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.objecttypename);
                    this.set("storeinfo.proxyurl", CMDBuildUI.util.api.Classes.getCardsUrl(data.objecttypename));

                    // add ecql filter
                    var filters = {};
                    if (this.get("storeinfo.ecqlfilter") && !Ext.Object.isEmpty(this.get("storeinfo.ecqlfilter"))) {
                        filters.ecql = this.get("storeinfo.ecqlfilter");
                    }

                    // add filter for enabled subclasses
                    if (data.theDomain) {
                        if (data.theDomain.get('classReferenceFilters').sourceFilter_ecqlFilter && data.direction === 'inverse') {
                            filters.ecql = data.theDomain.get('classReferenceFilters').sourceFilter_ecqlFilter;
                        } else if (data.theDomain.get('classReferenceFilters').destinationFilter_ecqlFilter && data.direction === 'direct') {
                            filters.ecql = data.theDomain.get('classReferenceFilters').destinationFilter_ecqlFilter;
                        }
                        var disabledDescendants = data.direction === "direct" ?
                            data.theDomain.get("disabledDestinationDescendants") :
                            data.theDomain.get("disabledSourceDescendants");
                        if (!Ext.isEmpty(disabledDescendants)) {
                            filters.attributes = {
                                _type: [{
                                    operator: 'in',
                                    value: data.direction === "direct" ?
                                        data.theDomain.get("destinations") :
                                        data.theDomain.get("sources")
                                }]
                            };
                        }
                    }
                    this.set("storeinfo.advancedfilter", filters);

                    this.set("storeinfo.extraparams", {
                        forDomain_name: data.theDomain.get("name"),
                        forDomain_direction: data.direction,
                        forDomain_originId: this.get("originId"),
                        positionOf: data.theRelation.get("_destinationId"),
                        positionOf_goToPage: false
                    });

                    // sorters
                    var sorters = [];
                    if (object && object.defaultOrder().getCount()) {
                        object.defaultOrder().getRange().forEach(function (o) {
                            sorters.push({
                                property: o.get("attribute"),
                                direction: o.get("direction") === "descending" ? "DESC" : 'ASC'
                            });
                        });
                    } else {
                        sorters.push({
                            property: 'Description'
                        });
                    }
                    this.set("storeinfo.sorters", sorters);
                }
            }
        }
    },

    stores: {
        records: {
            type: 'classes-cards',
            model: '{storeinfo.modelname}',
            proxy: {
                type: 'baseproxy',
                url: '{storeinfo.proxyurl}',
                extraParams: '{storeinfo.extraparams}'
            },
            advancedFilter: '{storeinfo.advancedfilter}',
            sorters: '{storeinfo.sorters}',
            autoLoad: '{storeinfo.autoload}',
            listeners: {
                load: 'onRecordsStoreLoad'
            }
        }
    }

});
