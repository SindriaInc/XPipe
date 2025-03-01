Ext.define('CMDBuildUI.view.administration.content.gis.externalservices.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gis-externalservices-view',
    control: {
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onEditBtnClick: function () {
        this.getViewModel().set('actions.view', false);
        this.getViewModel().set('actions.edit', true);
        this.getViewModel().set('actions.add', false);
    },

    onCancelBtnClick: function () {
        this.getViewModel().set('actions.view', true);
        this.getViewModel().set('actions.edit', false);
        this.getViewModel().set('actions.add', false);
        var vm = this.getViewModel();
        var theConfig = vm.get('theConfig');
        theConfig.reject();
    },

    setServiceType: function (theConfig) {
        theConfig.set('osm', false);
        theConfig.set('google', false);
        theConfig.set('yahoo', false);
        switch (theConfig.get('servicetype')) {
            case 'OpenStreetMap':
                theConfig.set('osm', true);
                break;
            case 'Google Maps':
                theConfig.set('google', true);
                break;
            case 'Yahoo Maps':
                theConfig.set('yahoo', true);
                break;
            default:
                theConfig.set('osm', true);
                break;
        }
    },

    onSaveBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var theConfig = vm.get('theConfig');
        
        this.setServiceType(theConfig);        
        var config = {
            'org__DOT__cmdbuild__DOT__gis__DOT__geoserver__DOT__enabled': theConfig.get('geoserverenabled'),
            'org__DOT__cmdbuild__DOT__gis__DOT__geoserver_admin_user': theConfig.get('geoserveradminuser'),
            'org__DOT__cmdbuild__DOT__gis__DOT__geoserver_admin_password': theConfig.get('geoserveradminpassword'),
            'org__DOT__cmdbuild__DOT__gis__DOT__geoserver_url': theConfig.get('geoserverurl'),
            'org__DOT__cmdbuild__DOT__gis__DOT__geoserver_workspace': theConfig.get('geoserverworkspace'),
            'org__DOT__cmdbuild__DOT__gis__DOT__osm': theConfig.get('osm'),
            'org__DOT__cmdbuild__DOT__gis__DOT__google': theConfig.get('google'),
            'org__DOT__cmdbuild__DOT__gis__DOT__yahoo': theConfig.get('yahoo'),
            'org__DOT__cmdbuild__DOT__gis__DOT__initialZoomLevel': theConfig.get('defaultzoom'),
            'org__DOT__cmdbuild__DOT__gis__DOT__osm_minzoom': theConfig.get('minimumzoom'),
            'org__DOT__cmdbuild__DOT__gis__DOT__osm_maxzoom': theConfig.get('maximumzoom')
        };
        CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(config, null, null, this).then(function () {
            if (!vm.destroyed) {
                button.setDisabled(false);
                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            }
        });
        this.getViewModel().set('actions.view', true);
        this.getViewModel().set('actions.edit', false);
        this.getViewModel().set('actions.add', false);
    }
});