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
        itemId: 'domain-filterfieldset',
        ui: 'administration-formpagination',
        items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('sourceFilter', {
            sourceFilter: {
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.originfilter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.originfilter'
                },
                resizable: {
                    handles: "s"
                },
                name: 'sourceFilter',
                bind: {
                    readOnly: '{actions.view}',
                    value: '{theDomain.sourceFilter}'
                }
            }
        }), CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('targetFilter', {
            targetFilter: {
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destinationfilter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destinationfilter'
                },
                resizable: {
                    handles: "s"
                },
                name: 'targetFilter',
                bind: {
                    readOnly: '{actions.view}',
                    value: '{theDomain.targetFilter}'
                }
            }
        })
        ]
    }]
});