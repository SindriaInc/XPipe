Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.attributes.AttributesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-domains-tabitems-attributes-attributes',
    data: {
        selected: {},
        isOtherPropertiesHidden: true
    },
    formulas: {
        storeSourceManager: {
            bind: '{theDomain}',
            get: function (data) {
                var me = this;
                if (data.get('name').length) {
                    Ext.asap(function () {
                        if (me && !me.destroyed) {
                            me.set('storeSource', data.attributes());
                        }
                    });
                }
            }
        }
    },
    stores: {
        allAttributes: {
            source: '{storeSource}',
            filters: [
                function (item) {
                    return item.canAdminShow();
                }
            ]
        }
    }
});