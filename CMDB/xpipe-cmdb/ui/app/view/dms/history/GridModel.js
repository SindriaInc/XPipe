Ext.define('CMDBuildUI.view.dms.history.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dms-history-grid',

    formulas: {
        proxyUrlHistory: {
            bind: {
                proxyUrl: '{proxyUrl}',
                attachmentId: '{dms-history-grid.attachmentId}'
            },
            get: function (data) {
                return Ext.String.format('{0}/{1}/history',
                    data.proxyUrl,
                    data.attachmentId
                );
            }
        }
    },
    stores: {
        attachmentshistory: {
            type: 'attachments',
            autoLoad: true,
            proxy: {
                url: '{proxyUrlHistory}',
                type: 'baseproxy'
            }
        }
    }
});
