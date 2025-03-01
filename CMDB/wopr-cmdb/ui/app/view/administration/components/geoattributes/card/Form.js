Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.Form', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.geoattributes.card.FormController',
        'CMDBuildUI.view.administration.components.geoattributes.card.FormModel',
        'CMDBuildUI.view.administration.components.geoattributes.card.FieldsHelper'
    ],
    alias: 'widget.administration-components-geoattributes-card-form',
    controller: 'administration-components-geoattributes-card-form',
    viewModel: {
        type: 'administration-components-geoattributes-card-form'
    },

    config: {
        theGeoAttribute: null,
        subtype: null,
        objectTypeName: null
    },
    bind: {
        userCls: '{formModeCls}' // this is used for hide label localzation icon in `view` mode
    },
    autoScroll: 'y',
    items: [{
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        items: [{
            xtype: 'administration-components-geoattributes-card-fieldscontainers-generalproperties'
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        bind: {
            hidden: Ext.String.format('{theGeoAttribute.type !== "{0}"}', CMDBuildUI.model.map.GeoAttribute.type.geometry)
        },
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.geoattributes.strings.specificproperty,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.geoattributes.strings.specificproperty'
        },
        items: [{
            xtype: 'administration-components-geoattributes-card-fieldscontainers-typeproperties'
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        bind: {
            hidden: Ext.String.format('{theGeoAttribute.type !== "{0}"}', CMDBuildUI.model.map.GeoAttribute.type.geometry)
        },
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.geoattributes.strings.infowindow,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.geoattributes.strings.infowindow'
        },
        items: [{
            xtype: 'administration-components-geoattributes-card-fieldscontainers-infowindow'
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.visibility,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.visibility'
        },
        items: [{
            xtype: 'administration-components-geoattributes-card-fieldscontainers-visibilitytree'
        }]
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        this.callParent(arguments);
    },
    listeners: {
        afterlayout: function (panel) {
            var vm = this.getViewModel();
            vm.getParent().set('title', vm.get('grid').lookupViewModel().get('objectTypeName') + ' - ' + 'geo attribute' + ' - ' + vm.get('theGeoAttribute.name'));
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    }
});