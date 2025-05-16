Ext.define('CMDBuildUI.view.administration.content.gis.externalservices.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gis-externalservices-view',
    data: {
        theConfig: {
            geoserverenabled: null,
            geoserveradminuser: '',
            geoserveradminpassword: '',
            geoserverurl: '',
            geoserverworkspace: '',
            servicetype: '',
            defaultzoom: 0,
            minimumzoom: 0,
            maximumzoom: 25,
            osm: '',
            google: '',
            yahoo: ''
        },

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
            bind: {
                canModify: '{theSession.rolePrivileges.admin_sysconfig_modify}'
            },
            get: function (data) {
                this.set('toolAction._canUpdate', data.canModify === true);
            }
        },
        configManager: function () {
            var me = this;

            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(
                function (configs) {
                    if (!me.destroyed) {
                        function getServiceType(osm, google, yahoo) {
                            if (osm.hasvalue && osm.value) {
                                return 'OpenStreetMap';
                            } else {
                                if (google.hasvalue && google.value) {
                                    return 'Google Maps';
                                } else {
                                    if (yahoo.hasvalue && yahoo.value) {
                                        return 'Google Maps';
                                    }
                                }
                            }
                            return 'OpenStreetMap';
                        }

                        var geoserverenabled = configs.filter(function (config) {
                            return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__geoserver__DOT__enabled';
                        })[0];

                        var geoserveradminuser = configs.filter(function (config) {
                            return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__geoserver_admin_user';
                        })[0];

                        var geoserveradminpassword = configs.filter(function (config) {
                            return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__geoserver_admin_password';
                        })[0];

                        var geoserverurl = configs.filter(function (config) {
                            return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__geoserver_url';
                        })[0];

                        var geoserverworkspace = configs.filter(function (config) {
                            return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__geoserver_workspace';
                        })[0];

                        var osm = configs.filter(function (config) {
                            return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__osm';
                        })[0];

                        var google = configs.filter(function (config) {
                            return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__google';
                        })[0];

                        var yahoo = configs.filter(function (config) {
                            return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__yahoo';
                        })[0];

                        var defaultzoom = configs.filter(function (config) {
                            return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__initialZoomLevel';
                        })[0];

                        var minimumzoom = configs.filter(function (config) {
                            return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__osm_minzoom';
                        })[0];

                        var maximumzoom = configs.filter(function (config) {
                            return config._key === 'org__DOT__cmdbuild__DOT__gis__DOT__osm_maxzoom';
                        })[0];

                        var servicetype = getServiceType(osm, google, yahoo);

                        var theConfig = CMDBuildUI.model.gis.Externalservices.create({
                            geoserverenabled: geoserverenabled.hasValue ? geoserverenabled.value : geoserverenabled['default'],
                            geoserveradminuser: geoserveradminuser.hasValue ? geoserveradminuser.value : geoserveradminuser['default'],
                            geoserveradminpassword: geoserveradminpassword.hasValue ? geoserveradminpassword.value : geoserveradminpassword['default'],
                            geoserverurl: geoserverurl.hasValue ? geoserverurl.value : geoserverurl['default'],
                            geoserverworkspace: geoserverworkspace.hasValue ? geoserverworkspace.value : geoserverworkspace['default'],
                            servicetype: servicetype,
                            osm: osm.value,
                            google: google.value,
                            yahoo: yahoo.value,
                            defaultzoom: defaultzoom.hasValue ? defaultzoom.value : defaultzoom['default'],
                            minimumzoom: minimumzoom.hasValue ? minimumzoom.value : minimumzoom['default'],
                            maximumzoom: maximumzoom.hasValue ? maximumzoom.value : maximumzoom['default']
                        });


                        me.set('theConfig', theConfig);
                    }
                }
            );
        },
        servicesData: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getGisServicesData();
        }
    },

    stores: {
        servicetypelist: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },

            sorters: ['label'],
            data: '{servicesData}',
            autoDestroy: true
        }
    }
});