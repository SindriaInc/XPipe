Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.FiltersFieldset', {
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.FiltersFieldsetController',
        'CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.FiltersFieldsetModel'
    ],

    alias: 'widget.administration-content-domains-tabitems-properties-fieldsets-filtersfieldset',

    controller: 'administration-content-domains-tabitems-properties-fieldsets-filtersfieldset',
    viewModel: {
        type: 'administration-content-domains-tabitems-properties-fieldsets-filtersfieldset'
    },
    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.filters,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.filters'
        },
        itemId: 'domain-generaldatafieldset',
        ui: 'administration-formpagination',
        items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('classReferenceFiltersSource', {
            classReferenceFiltersSource: {
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.originfilter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.originfilter'
                },
                resizable: {
                    handles: "s"
                },
                name: 'classReferenceFiltersSource',
                bind: {
                    readOnly: '{actions.view}',
                    value: '{theDomain.classReferenceFilters.sourceFilter}'
                },
                listeners: {
                    change: function (textarea, newValue, oldValue) {
                        var vm = textarea.lookupViewModel();
                        vm.get('theDomain.classReferenceFilters').sourceFilter = newValue;
                    }
                }
            }
        }), CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('classReferenceFiltersDestination', {
            classReferenceFiltersDestination: {
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destinationfilter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destinationfilter'
                },
                resizable: {
                    handles: "s"
                },
                name: 'classReferenceFiltersDestination',
                bind: {
                    readOnly: '{actions.view}',
                    value: '{theDomain.classReferenceFilters.destinationFilter}'
                },
                listeners: {
                    change: function (textarea, newValue, oldValue) {
                        var vm = textarea.lookupViewModel();
                        vm.get('theDomain.classReferenceFilters').destinationFilter = newValue;
                    }
                }
            }
        })
        ]
    }]
});