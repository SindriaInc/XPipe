Ext.define('CMDBuildUI.view.administration.content.gis.thematisms.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gis-thematisms-grid',
    data: {
        storeAutoLoad: false,
        storeProxyUrl: '',
        actions: {
            view: true,
            edit: false,
            add: false
        },
        toolAction: {
            _canUpdate: false
        }
    },
    formulas: {
        toolsManager: {
            get: function (data) {
                var canUpdate = this.get('theSession.rolePrivileges.admin_gis_modify') === true;
                this.set('toolAction._canUpdate', canUpdate);
            }
        },
        action: {
            bind: {
                isView: '{actions.view}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}'
            },
            get: function (data) {
                if (data.isView) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },
        updateStoreVariables: {
            get: function (data) {
                this.set(
                    "storeProxyUrl",
                    Ext.String.format(
                        '{0}/classes/_ANY/geostylerules',
                        CMDBuildUI.util.Config.baseUrl
                    )
                );
                // set auto load
                this.set("storeAutoLoad", true);

            }
        }
    },

    stores: {
        thematismsStore: {
            model: 'CMDBuildUI.model.thematisms.Thematism',
            proxy: {
                type: 'baseproxy',
                url: '{storeProxyUrl}'
            },
            pageSize: 0,
            autoLoad: '{storeAutoLoad}',
            autoDestroy: true
        }
    }

});