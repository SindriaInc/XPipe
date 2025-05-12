Ext.define('CMDBuildUI.view.fields.bufferedcombo.SelectionPopupModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.fields-bufferedcombo-selectionpopup',

    data: {
        searchvalue: null,
        addbtn: {
            text: null,
            disabled: false,
            hidden: true
        }
    },

    formulas: {
        /**
         * Return add card button text.
         */
        updateAddBtnInfo: {
            bind: {
                objectTypeDescription: '{objectTypeDescription}'
            },
            get: function (data) {
                this.set("addbtn.text", Ext.String.format(
                    "{0} {1}",
                    CMDBuildUI.locales.Locales.classes.cards.addcard,
                    data.objectTypeDescription
                ));

                this.set("addbtn.hidden", this.get("objectType") !== CMDBuildUI.util.helper.ModelHelper.objecttypes.klass);
            }
        },

        /**
         * Update store info
         */
        updateStoreInfo: {
            get: function () {
                // store type
                var type, object;
                var preferences;
                // sorters
                if(CMDBuildUI.util.helper.UserPreferences.getGridPreferences){
                    preferences = CMDBuildUI.util.helper.UserPreferences.getGridPreferences(
                        this.get("objectType"),
                        this.get('objectTypeName')
                    );
                }
                
                var sorters = [];
                if (preferences && !Ext.isEmpty(preferences.defaultOrder)) {
                    preferences.defaultOrder.forEach(function (o) {
                        sorters.push({
                            property: o.attribute,
                            direction: o.direction === "descending" ? "DESC" : 'ASC'
                        });
                    });
                } else if (object && object.defaultOrder().getCount()) {
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
        },

        /**
         * Save button disabled property.
         * Bindend on selection.
         */
        saveBtnDisabled: {
            bind: '{selection}',
            get: function (selection) {
                return !selection;
            }
        }
    },

    stores: {
        records: {
            type: '{storealias}',
            model: '{modelname}',
            proxy: {
                type: 'baseproxy',
                url: '{storeinfo.proxyurl}'
            },           
            sorters: '{storeinfo.sorters}',
            autoLoad: '{storeinfo.autoload}'
        }
    }

});