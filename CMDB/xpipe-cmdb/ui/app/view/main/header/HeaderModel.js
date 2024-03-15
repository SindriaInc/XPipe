Ext.define('CMDBuildUI.view.main.header.HeaderModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.main-header-header',

    data: {
        companylogoinfo: {
            hidden: true,
            url: null
        }
    },

    formulas: {
        isAdministrator: {
            bind: {
                privileges: '{theSession.rolePrivileges}',
                isAdministrationModule: '{isAdministrationModule}'
            },
            get: function (data) {
                return data.privileges && data.privileges.admin_access && !data.isAdministrationModule;
            }
        },

        calendarbtnhidden: {
            bind: {
                privileges: '{theSession.rolePrivileges}',
                enabled: '{scheduler.enabled}'
            },
            get: function (data) {
                if (data.enabled && data.privileges && (data.privileges.calendar_access || data.privileges.calendar_event_create)) {
                    return false;
                }
                return true;
            }
        },

        languageSelectorText: {
            bind: {
                storeloaded: '{languages.totalCount}',
                selectedlanguage: '{language.default}',
                showselector: '{language.showselector}'
            },
            get: function(data) {
                if (data.storeloaded && data.showselector) {
                    var languages = this.get('languages');
                    var record = languages.findRecord('code', data.selectedlanguage);
                    var menuItems = [];
                    languages.getRange().forEach(function(item) {
                        menuItems.push({
                            text: item.get('description'),
                            icon: item.get('code'),
                            disabled: item.get('code') == data.selectedlanguage,
                            handler: 'onLanguageSelectorItemClick'
                        });
                    });
                    this.set('languagesMenu', {
                        defaults: {
                            renderTpl:
                                '<a id="{id}-itemEl" data-ref="itemEl"' +
                                    ' class="{linkCls} {childElCls}"' +
                                    ' href="#" ' +
                                    ' hidefocus="true"' +
                                    ' unselectable="on"' +
                                    '<tpl if="tabIndex != null">' +
                                        ' tabindex="{tabIndex}"' +
                                    '</tpl>' +
                                    '<tpl foreach="ariaAttributes"> {$}="{.}"</tpl>' +
                                '>' +
                                    '<span id="{id}-textEl" data-ref="textEl" class="{textCls} {textCls}-{ui} {indentCls}{childElCls}" unselectable="on" role="presentation">{text}</span>' +
                                    '<div role="presentation" id="{id}-iconEl" data-ref="iconEl" class="{baseIconCls}-{ui} {baseIconCls}' +
                                        '{iconCls} {childElCls} {glyphCls}">' +
                                        '<img width="20px" src="resources/images/flags/{icon}.png">' +
                                    '</div>' +
                                '</a>'
                        },
                        items: menuItems
                    });
                    return record.get('description');
                }
            }
        }
    },

    stores: {
        languages: {
            model: 'CMDBuildUI.model.Language',
            sorters: 'description',
            pageSize: 0,
            autoLoad: '{isAuthenticated || language.showselector}',
            autoDestroy: true
        }
    }

});
