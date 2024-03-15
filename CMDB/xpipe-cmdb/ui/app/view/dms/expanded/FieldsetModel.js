Ext.define('CMDBuildUI.view.dms.expanded.FieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dms-expanded-fieldset',

    data: {
        recordsCount: 0
    },

    formulas: {
        /**
         * Set fieldset title merging title and records count
         */
        fieldsetTitle: {
            bind: {
                title: '{basetitle}',
                recordsCount: '{recordsCount}'
            },
            get: function (data) {
                return Ext.String.format('{0} ({1})', data.title, data.recordsCount);
            }
        },

        filterStore: {
            get: function () {
                var dmsCategory = this.get('dmsCategory');
                return [function (attachment) {
                    return attachment.get('_category_name') === dmsCategory;
                }]
            }
        }
    },

    stores: {
        attachmentsCategory: {
            type: 'chained',
            source: '{attachments}',
            filters: '{filterStore}',
            autoDestroy: true
        }
    }

});