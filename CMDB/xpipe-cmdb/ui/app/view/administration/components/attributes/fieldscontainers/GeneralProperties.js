// locazation: ok

Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.GeneralProperties', {
    extend: 'Ext.form.Panel',

    alias: 'widget.administration-components-attributes-fieldscontainers-generalproperties',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    items: [{
        layout: 'column',
        items: [{
            // view / view in row
            columnWidth: 0.5,
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.name,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.name'
            },
            name: 'name',
            autoEl: {
                'data-testid': 'attribute-name_display'
            },
            bind: {
                value: '{theAttribute.name}',
                hidden: '{!actions.view}'
            }
        }, {
            // view / viewInRow
            columnWidth: 0.5,
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.description,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.description'
            },
            name: 'description',
            autoEl: {
                'data-testid': 'attribute-description_display'
            },
            bind: {
                value: '{theAttribute.description}',
                hidden: '{!actions.view}'
            }
        }]
    }, {
        layout: 'column',
        items: [{
            itemId: 'groupfield',
            columnWidth: 0.5,
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group'
            },
            autoEl: {
                'data-testid': 'attribute-group_display'
            },
            hidden: true,
            bind: {
                hidden: '{isGroupHidden}',
                value: '{theAttribute._group_description}'
            }
        }, {
            columnWidth: 0.5,
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mode,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mode'
            },
            autoEl: {
                'data-testid': 'attribute-mode_display'
            },
            name: 'mode',
            hidden: true,
            bind: {
                hidden: Ext.String.format('{theAttribute.type == "{0}"}', CMDBuildUI.model.Attribute.types.formula),
                value: '{theAttribute.mode}'
            },
            renderer: CMDBuildUI.util.administration.helper.RendererHelper.getAttributeMode
        }]
    }, {
        layout: 'column',
        xtype: 'fieldcontainer',
        items: [{
            columnWidth: 0.25,
            xtype: 'checkbox',
            autoEl: {
                'data-testid': 'attribute-showInGrid_display'
            },
            name: 'showInGrid',
            readOnly: true,
            hidden: true,
            bind: {
                fieldLabel: '{showInGridLabel}',
                value: '{theAttribute.showInGrid}',
                hidden: '{showInGridHidden}'
            }
        }, {
            columnWidth: 0.25,
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid'
            },
            autoEl: {
                'data-testid': 'attribute-showInReducedGrid_display'
            },
            hidden: true,
            name: 'showInReducedGrid',
            readOnly: true,
            bind: {
                value: '{theAttribute.showInReducedGrid}',
                hidden: Ext.String.format('{objectType == "{0}" || objectType == "{1}"}', CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel, CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.charAt(0).toUpperCase() + CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.slice(1))
            }
        }, {
            columnWidth: 0.5,
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.excludefromgrid,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.excludefromgrid'
            },
            autoEl: {
                'data-testid': 'attribute-excludeFromGrid_display'
            },
            hidden: true,
            name: 'excludeFromGrid',
            readOnly: true,
            bind: {
                value: '{theAttribute.hideInGrid}',
                hidden: Ext.String.format('{objectType == "{0}" || objectType == "{1}"}', CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel, CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.charAt(0).toUpperCase() + CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.slice(1))
            }
        }]
    }, {
        layout: 'column',
        items: [{
            columnWidth: 0.5,
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unique,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unique'
            },
            autoEl: {
                'data-testid': 'attribute-unique_display'
            },
            name: 'unique',
            readOnly: true,
            bind: {
                value: '{theAttribute.unique}',
                hidden: Ext.String.format('{theAttribute.type == "{0}"}', CMDBuildUI.model.Attribute.types.formula)
            },
            hidden: true
        }, {
            columnWidth: 0.5,
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mandatory,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mandatory'
            },
            name: 'mandatory',
            autoEl: {
                'data-testid': 'attribute-mandatory_display'
            },
            readOnly: true,
            bind: {
                value: '{theAttribute.mandatory}',
                hidden: Ext.String.format('{isMandatoryHidden || theAttribute.type == "{0}"}', CMDBuildUI.model.Attribute.types.formula)
            },
            hidden: true
        }]
    }, {
        layout: 'column',
        items: [{
            columnWidth: 0.5,
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.hideinfilter,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.hideinfilter'
            },
            name: 'hideInFilter',
            autoEl: {
                'data-testid': 'attribute-hideInFilter_display'
            },
            readOnly: true,
            hidden: true,
            bind: {
                hidden: Ext.String.format('{objectType == "dmsmodel" || objectType == "Domain" || theAttribute.type == "{0}"}', CMDBuildUI.model.Attribute.types.formula),
                value: '{theAttribute.hideInFilter}'
            }
        }, {
            columnWidth: 0.5,
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.enablesorting,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.enablesorting'
            },
            name: 'sortingEnabled',
            autoEl: {
                'data-testid': 'attribute-enableSorting_input'
            },
            readOnly: true,
            hidden: true,
            bind: {
                hidden: Ext.String.format('{objectType == "dmsmodel" || objectType == "Domain" || theAttribute.hideInFilter || theAttribute.type == "{0}"}', CMDBuildUI.model.Attribute.types.formula),
                value: '{theAttribute.sortingEnabled}'
            }
        }]
    }, {
        layout: 'column',
        items: [{
            columnWidth: 0.5,
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.texts.active,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.texts.active'
            },
            autoEl: {
                'data-testid': 'attribute-active_display'
            },
            name: 'active',
            readOnly: true,
            bind: {
                value: '{theAttribute.active}'
            }
        }]
    }, {
        layout: 'column',
        items: [{
            columnWidth: 0.5,
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.synctodmsattr,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.synctodmsattr'
            },
            name: 'syncToDmsAttr',
            hidden: true,
            bind: {
                hidden: '{objectType !== "dmsmodel"}',
                value: '{theAttribute.syncToDmsAttr}'
            }
        }]
    }]
});