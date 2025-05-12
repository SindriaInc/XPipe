Ext.define('CMDBuildUI.model.localizations.LocalizationByCode', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        types: {
            class: 'class',
            process: 'class',
            activity: 'activity',
            role: 'role',
            domain: 'domain',
            view: 'view',
            lookup: 'lookup',
            dashboard: 'dashboard',
            filter: 'filter',
            report: 'report',
            menu: 'menuitem',
            attributeclass: 'attributeclass',
            attributegroupclass: 'attributegroupclass',
            attributeprocess: 'attributeclass',
            attributedomain: 'attributedomain',
            attributegroupprocess: 'attributegroupclass',
            widget: 'widget',
            contextmenu: 'contextmenu',
            custompage: 'custompage'
        },
        attributeTypes: {
            class: 'attributeclass',
            process: 'attributeclass',
            domain: 'attributedomain'
        }
    },

    fields: [{
            name: 'code',
            type: 'string',
            critical: true
        }, {
            name: 'default',
            type: 'string',
            critical: true
        }, {
            name: 'values',
            type: 'auto',
            critical: true
        }, {
            name: 'type',
            type: 'string',
            calculate: function (data) {
                var codeParts = data.code.split('.');
                var type = codeParts[0];
                switch (type) {
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.class:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.process:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.view:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.lookup:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.filter:
                        if (codeParts.length === 3 && codeParts[codeParts.length - 1] === 'help') {
                            return CMDBuildUI.locales.Locales.administration.processes.helps.help;
                        }
                        return CMDBuildUI.locales.Locales.administration.common.labels.description;
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.activity:
                        if (codeParts[codeParts.length - 1] === 'instructions') {
                            var activityName = codeParts[2];
                            var _activityName = [];
                            if (codeParts.length > 4) {
                                for (var i = 2; i < codeParts.length - 1; i++) {
                                    _activityName.push(codeParts[i]);
                                }
                                if (_activityName.length) {
                                    activityName = _activityName.join('.');
                                }
                            }
                            return Ext.String.format('{0} - {1} - {2}', CMDBuildUI.locales.Locales.common.tabs.activity, activityName, CMDBuildUI.locales.Locales.administration.processes.helps.help);
                        }
                        return CMDBuildUI.locales.Locales.common.tabs.activity;
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.report:
                        if (codeParts[2] === 'attribute') {
                            return CMDBuildUI.locales.Locales.administration.localizations.attributedescription;
                        }
                        return CMDBuildUI.locales.Locales.administration.common.labels.description;
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.role:
                        return CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group;
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.domain:

                        var _type;
                        switch (codeParts[2]) {
                            case 'directdescription':
                                _type = CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription;
                                break;
                            case 'inversedescription':
                                _type = CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription;
                                break;
                            case 'masterdetaillabel':
                                _type = CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdataillong;
                                break;
                        }
                        return _type;

                    case CMDBuildUI.model.localizations.LocalizationByCode.types.dashboard:
                        // TODO get sub elements
                        return CMDBuildUI.locales.Locales.administration.localizations.dashboard;

                    case CMDBuildUI.model.localizations.LocalizationByCode.types.menu:
                        return CMDBuildUI.locales.Locales.administration.localizations.menuitem;

                    case CMDBuildUI.model.localizations.LocalizationByCode.types.attributeprocess:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.attributeclass:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.attributedomain:
                        return CMDBuildUI.locales.Locales.administration.localizations.attributedescription;
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.attributegroupclass:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.attributegroupprocess:
                        return CMDBuildUI.locales.Locales.administration.localizations.attributegroupdescription;
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.widget:
                        return CMDBuildUI.locales.Locales.administration.customcomponents.strings.widget;
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.contextmenu:
                        return CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.contextmenuitem;
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.custompage:
                        return CMDBuildUI.locales.Locales.administration.localizations.custompagedescription;
                    default:
                        break;
                }

            }
        },
        {
            name: '_element',
            type: 'string',
            calculate: function (data) {
                var codeParts = data.code.split('.');
                var type = codeParts[0];
                switch (type) {
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.class:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.process:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.domain:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.view:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.report:
                        return codeParts[1];

                    case CMDBuildUI.model.localizations.LocalizationByCode.types.dashboard:
                        // TODO get sub elements

                        return CMDBuildUI.locales.Locales.administration.localizations.dashboard;

                    case CMDBuildUI.model.localizations.LocalizationByCode.types.menu:
                        return CMDBuildUI.locales.Locales.administration.localizations.menuitem;

                    case CMDBuildUI.model.localizations.LocalizationByCode.types.lookup:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.attributeclass:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.attributegroupclass:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.attributeprocess:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.attributedomain:
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.filter:
                        return Ext.String.format('{0} - {1}', codeParts[1], codeParts[2]);
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.widget:
                        return codeParts[1];
                    case CMDBuildUI.model.localizations.LocalizationByCode.types.contextmenu:
                        return codeParts[1];
                    default:
                        break;
                }

            }
        }, {
            name: 'element',
            type: 'string',
            calculate: function (data) {
                return data.code.split('.')[1];
            }
        }, {
            name: 'json',
            calculate: function (data) {
                return Ext.JSON.encode(data);
            }
        }
    ]
});