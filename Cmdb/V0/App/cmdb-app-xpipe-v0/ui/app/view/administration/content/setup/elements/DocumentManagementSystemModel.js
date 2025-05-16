Ext.define('CMDBuildUI.view.administration.content.setup.elements.DocumentManagementSystemModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-documentmanagementsystem',

    data: {
        isAlfresco: false,
        isCmis: false,
        isPostgres: false,
        isSharepoint: false,
        dmsServicesStoreData: null
    },

    formulas: {
        updateDisplayPassword: {
            bind: {
                password: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__password}'
            },
            get: function (data) {
                var hiddenPassword = CMDBuildUI.util.administration.helper.RendererHelper.getDisplayPassword(data.password);
                this.set('hiddenPassword', hiddenPassword);
            }
        },
        dmsType: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type}',
            get: function (type) {
                switch (type) {
                    case 'alfresco':
                        this.set('isAlfresco', true);
                        this.set('isCmis', false);
                        this.set('isPostgres', false);
                        this.set('isSharepoint', false);
                        break;
                    case 'cmis':
                        this.set('isAlfresco', false);
                        this.set('isCmis', true);
                        this.set('isPostgres', false);
                        this.set('isSharepoint', false);
                        break;
                    case 'postgres': // sperimental
                        this.set('isAlfresco', false);
                        this.set('isCmis', false);
                        this.set('isPostgres', true);
                        this.set('isSharepoint', false);
                        break;
                    case 'sharepoint_online':
                        this.set('isAlfresco', false);
                        this.set('isCmis', false);
                        this.set('isPostgres', false);
                        this.set('isSharepoint', true);
                        break;
                }
            }
        },
        dmsServicesStoreDataManager: function () {
            var me = this;    
            CMDBuildUI.util.administration.helper.ConfigHelper.getConfig('org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type').then(function (value) {
                var types = [{
                    "value": "cmis",
                    "label": CMDBuildUI.locales.Locales.administration.systemconfig.cmis
                }, {
                    "value": "sharepoint_online",
                    "label": CMDBuildUI.locales.Locales.administration.systemconfig.sharepoint
                }];

                if (value === 'postgres') {
                    types.push({
                        "value": "postgres",
                        "label": CMDBuildUI.locales.Locales.administration.systemconfig.postgres // sperimental
                    });
                } else if (value === 'postgres') {
                    types.push({
                        "value": "alfresco",
                        "label": "Afresco (v. 3.4 or lower)" // deprecated
                    });
                }                
                me.set('dmsServicesStoreData', types);                
            });
        }

    },
    stores: {
        dmsCategoryTypesStore: {
            source: 'dms.DMSCategoryTypes',
            autoDestroy: true
        },
        dmsServiceTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',            
            data: '{dmsServicesStoreData}',
            sorters: ['label']
        }

    }
});