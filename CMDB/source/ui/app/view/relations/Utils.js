Ext.define('CMDBuildUI.view.relations.Utils', {
    singleton: true,

    /**
     * Delete relation
     * 
     * @param {CMDBuildUI.model.domains.Relation} relation 
     * 
     * @return {Ext.promise.Promise}
     */
    deleteRelation: function (relation) {
        var deferred = new Ext.Deferred();

        // show confirm message
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.relations.deleterelationconfirm,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    // erase item
                    CMDBuildUI.util.Ajax.setActionId('relation.delete');
                    relation.erase({
                        url: Ext.String.format('/domains/{0}/relations', relation.get('_type')),
                        success: function (record, operation) {
                            deferred.resolve(record);
                        },
                        failure: function (record, operation) {
                            deferred.reject();
                        },
                        callback: function (record, operation, success) {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        }
                    });
                }
            }
        );

        return deferred.promise;
    },

    /**
     * Edit relation
     * 
     * @param {CMDBuildUI.model.domains.Relation} relation 
     * @param {Object} params
     * @param {String} params.proxyurl
     * @param {String} params.objecttypename
     * @param {String} params.objectid
     * 
     * @return {Ext.promise.Promise}
     */
    editRelation: function (relation, params) {
        var deferred = new Ext.Deferred(),
            domain = CMDBuildUI.util.helper.ModelHelper.getDomainFromName(relation.get("_type")),
            destinationdescription = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(relation.get("_destinationType")),
            popup,
            targettype,
            reldescription;

        // get relation description
        if (domain) {
            if (relation.get("_direction") === "direct") {
                reldescription = domain.get("_descriptionDirect_translation");
                targettype = domain.get("destination");
            } else {
                reldescription = domain.get("_descriptionInverse_translation");
                targettype = domain.get("source");
            }
        }

        // popup title
        var title = Ext.String.format(
            "{0} {1} ({2})",
            CMDBuildUI.locales.Locales.relations.editrelation,
            reldescription,
            destinationdescription
        );

        // update relatin proxy url
        relation.getProxy().setUrl(params.proxyurl);

        // popup config
        var popupconfig = {
            xtype: 'relations-list-edit-gridcontainer',
            originTypeName: params.objecttypename,
            originId: params.objectid,
            viewModel: {
                data: {
                    theObject: params.theObject,
                    objectTypeName: targettype,
                    relationDirection: relation.get("_direction"),
                    theRelation: relation,
                    theDomain: domain
                }
            },

            listeners: {
                /**
                 * Custom event to close popup directly from popup
                 */
                popupclose: function (eOpts) {
                    popup.close();
                }
            },
            onSaveSuccess: function () {
                deferred.resolve();
            }
        };

        // open popup
        popup = CMDBuildUI.util.Utilities.openPopup(null, title, popupconfig);

        return deferred.promise;
    }
});