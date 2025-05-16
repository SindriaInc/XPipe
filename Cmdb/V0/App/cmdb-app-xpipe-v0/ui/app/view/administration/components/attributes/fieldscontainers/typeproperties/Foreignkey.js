Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Foreignkey', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-foreignkeyfields',

    items: [{
        xtype: 'container',
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'allelementscombo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                name: 'initialPage',
                withClasses: true,
                withProcesses: true,
                bind: {
                    value: '{theAttribute.targetClass}',
                    disabled: '{actions.edit}'
                }
            }]

        }]
    }, {
        xtype: 'container',
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
            layout: 'column',
            hidden: true,
            bind: {
                hidden: '{objectType == "dmsmodel"}'
            },            
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetail,
                name: 'isMasterDetail',
                bind: {
                    value: '{theAttribute.isMasterDetail}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdataillong,
                name: 'masterDetailDescription',
                bind: {
                    value: '{theAttribute.masterDetailDescription}'
                },
                labelToolIconCls: 'fa-flag',
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                labelToolIconClick: 'onTranslateClickMasterDetail'
            }]

        }]
    }, {
        xtype: 'fieldcontainer',
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                bind: {
                    value: '{theAttribute.targetClassDescription}'
                }
            }]
        }]
    }, {
        xtype: 'fieldcontainer',
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            layout: 'column',
            hidden: true,
            bind: {
                hidden: '{objectType == "dmsmodel"}'
            },
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetail,
                readOnly: true,
                bind: {
                    value: '{theAttribute.isMasterDetail}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdataillong,
                bind: {
                    value: '{theAttribute.masterDetailDescription}'
                }
            }]

        }]
    }]
});