Ext.define('CMDBuildUI.view.emails.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.emails-grid',

    data: {
        readonly: true
    },

    formulas: {

        updateCounter: {
            bind: '{emails}',
            get: function (emails) {
                if (emails) {
                    var me = this;
                    me.getView().mon(emails, "datachanged", function (store, eOpts) {
                        me.set("tabcounters.emails", store.getCount());
                    });
                }
            }
        },

        disableButtonOnView: {
            get: function () {
                var view = this.getView();
                this.set('readonly', view.getReadOnly());
                return view.isFormWritable() ? false : true;
            }
        },

        isAsync: {
            bind: {
                theTarget: '{theTarget}'
            },
            get: function (data) {
                return data.theTarget.phantom;
            }
        }

    }
});