Ext.define('CMDBuildUI.view.administration.content.setup.elements.DocumentManagementSystem', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.DocumentManagementSystemController',
        'CMDBuildUI.view.administration.content.setup.elements.DocumentManagementSystemModel'
    ],

    alias: 'widget.administration-content-setup-elements-documentmanagementsystem',
    controller: 'administration-content-setup-elements-documentmanagementsystem',
    viewModel: {
        type: 'administration-content-setup-elements-documentmanagementsystem'
    },

    items: [{
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
        },

        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'isEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__enabled}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-dms-active_input'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'fieldcontainer',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dmscategories.dmscategory,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dmscategories.dmscategory'
                    },
                    items: [{
                        xtype: 'displayfield',
                        name: 'attachmentTypeLookup',
                        hidden: true,
                        bind: {
                            value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__category}',
                            hidden: '{!actions.view}'
                        },
                        renderer: function (value) {
                            if (!Ext.isEmpty(value)) {
                                var store = Ext.getStore('dms.DMSCategoryTypes');
                                var record = store.findRecord('code', value);
                                if (record) {
                                    return record.get('description');
                                }
                            }
                            return value;
                        },
                        autoEl: {
                            'data-testid': 'administration-systemconfig-dms-dmscategory_display'
                        }
                    }, {
                        /********************* Category Lookup **********************/
                        xtype: 'combobox',
                        queryMode: 'local',
                        forceSelection: true,
                        displayField: 'name',
                        valueField: 'name',
                        name: 'attachmentTypeLookup',
                        hidden: true,
                        bind: {
                            store: '{dmsCategoryTypesStore}',
                            value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__category}',
                            hidden: '{actions.view}'
                        },
                        autoEl: {
                            'data-testid': 'administration-systemconfig-dms-dmscategory_input'
                        }
                    }]
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    /********************* Service Type **********************/
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.gis.servicetype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.servicetype'
                    },
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type}',
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value) {
                        switch (value) {
                            case 'cmis':
                                return CMDBuildUI.locales.Locales.administration.systemconfig.cmis;
                            case 'alfresco':
                                return ""; // not supported
                            case 'postgres':
                                return CMDBuildUI.locales.Locales.administration.systemconfig.postgres; // not supported
                            case 'sharepoint_online':
                                return CMDBuildUI.locales.Locales.administration.systemconfig.sharepoint; // not supported                                
                            default:
                                return value;
                        }
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-dms-dmscategory_display'
                    }
                }, {
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.gis.servicetype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.servicetype'
                    },
                    name: 'dmsServiceType',
                    itemId: 'dmsServiceType',
                    editable: false,
                    queryMode: 'local',
                    forceSelection: true,
                    allowBlank: true,
                    displayField: 'label',
                    valueField: 'value',
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type}',
                        hidden: '{actions.view}',
                        store: '{dmsServiceTypesStore}'
                    },
                    applyValue: function () {
                        var me = this;
                        var vm = me.lookupViewModel();
                        vm.bind({
                            bindTo: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type}',
                            single: true
                        }, function (servicetype) {
                            Ext.asap(function () {
                                me.setValue(servicetype);
                            });
                        });
                    },
                    listeners: {
                        show: function () {
                            this.applyValue();
                        },
                        afterrender: function () {
                            this.applyValue();
                        }
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-dms-dmscategory_input'
                    }
                }]
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.cmis,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.cmis'
        },
        hidden: true,
        bind: {
            hidden: '{!isCmis}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    name: 'cmisHost',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.host,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.host'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__url}',
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-dms-cmisurl_display'
                    }
                }, {
                    xtype: 'textfield',
                    name: 'cmisHost',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.host,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.host'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__url}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-dms-cmisurl_input'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'displayfield',
                    name: 'webServicePath',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.webservicepath,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.webservicepath'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__path}',
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-dms-cmispath_display'
                    }
                }, {
                    xtype: 'textfield',
                    name: 'webServicePath',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.webservicepath,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.webservicepath'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__path}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-dms-cmispath_input'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    name: 'username',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__user}',
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-dms-cmisusername_display'
                    }
                }, {
                    xtype: 'textfield',
                    name: 'username',
                    listeners: {
                        afterrender: function (cmp) {
                            cmp.inputEl.set({
                                autocomplete: 'new-username'
                            });
                        }
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__user}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-dms-cmisusername_input'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'displayfield',
                    name: 'password',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                    },
                    bind: {
                        hidden: '{!actions.view}',
                        value: '{hiddenPassword}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-dms-cmispassword_display'
                    }
                }, {
                    xtype: 'passwordfield',
                    name: 'password',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__password}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-dms-cmispassword_input'
                    }
                }]
            }]
        }]
    }, {
        /**
         *  # sharepoint auth client id:
         *  ##org.cmdbuild.dms.service.sharepoint.auth.clientId=
         *  
         *  # sharepoint auth client secret:
         *  ##org.cmdbuild.dms.service.sharepoint.auth.clientSecret=
         *        
         *  ##org.cmdbuild.dms.service.sharepoint.user=admin         
         *  ##org.cmdbuild.dms.service.sharepoint.password=admin
         * 
         *  # sharepoint auth protocol (es: `msazureoauth2`):
         *  ##org.cmdbuild.dms.service.sharepoint.auth.protocol=msazureoauth2
         *         
         *  # sharepoint auth resource id:
         *  ##org.cmdbuild.dms.service.sharepoint.auth.resourceId=
         * 
         *  ##org.cmdbuild.dms.service.sharepoint.graphApi.url=https://graph.microsoft.com/v1.0/
         * 
         *  # sharepoint auth service url:
         *  ##org.cmdbuild.dms.service.sharepoint.auth.serviceUrl=https://login.microsoftonline.com
         *          
         *  # sharepoint auth tenant id:
         *  ##org.cmdbuild.dms.service.sharepoint.auth.tenantId=          
         *  
         *  # sharepoint custom author column:
         *  ##org.cmdbuild.dms.service.sharepoint.model.authorColumn=         
         *  
         *  # sharepoint custom category column:
         *  ##org.cmdbuild.dms.service.sharepoint.model.categoryColumn=
         *           
         *  # sharepoint custom description column:
         *  ##org.cmdbuild.dms.service.sharepoint.model.descriptionColumn=Label
         *  
         *  #org.cmdbuild.dms.service.sharepoint.path=/
         *  #org.cmdbuild.dms.service.sharepoint.url=
         * 
         * 
         */
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.sharepoint,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.sharepoint'
        },
        hidden: true,
        bind: {
            hidden: '{!isSharepoint}'
        },
        defaults: {
            layout: 'column',
            defaults: {
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    bind: {
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'textfield',
                    bind: {
                        hidden: '{actions.view}'
                    }
                }]
            }
        },
        items: [{
            // clientid
            // clientsecret
            items: [{
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.clientid,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.clientid'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__auth__DOT__clientId}'
                    }
                }
            }, {
                style: {
                    paddingLeft: '15px'
                },
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.clientsecret,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.clientsecret'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__auth__DOT__clientSecret}'
                    }
                }
            }]
        }, {
            // username
            // password
            items: [{
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__user}'
                    }
                }
            }, {
                style: {
                    paddingLeft: '15px'
                },
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                    }
                },
                items: [{
                    xtype: 'displayfield',
                    bind: {
                        hidden: '{!actions.view}',
                        value: '{hiddenPassword}'
                    }
                }, {
                    xtype: 'passwordfield',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__password}',
                        hidden: '{actions.view}'
                    }
                }]
            }]
        }, {
            // protocol
            // resourceid
            items: [{
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.protocol,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.protocol'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__auth__DOT__protocol}'
                    }
                }
            }, {
                style: {
                    paddingLeft: '15px'
                },
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.resourceid,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.resourceid'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__auth__DOT__resourceId}'
                    }
                }
            }]
        }, {
            // serviceUrl
            // graphApi
            items: [{
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.serviceurl,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.serviceurl'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__auth__DOT__serviceUrl}'
                    }
                }
            }, {
                style: {
                    paddingLeft: '15px'
                },
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.graphapiurl,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.graphapiurl'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__graphApi__DOT__url}'
                    }
                }
            }]
        }, {
            // tenantId
            // authorColumn
            items: [{
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.tenantid,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.tenantid'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__auth__DOT__tenantId}'
                    }
                }
            }, {
                style: {
                    paddingLeft: '15px'
                },
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.authorcolumn,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.authorcolumn'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__model__DOT__authorColumn}'
                    }
                }
            }]
        }, {
            // categoryColumn
            // descriptionColumn
            items: [{
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.categorycolumn,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.categorycolumn'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__model__DOT__categoryColumn}'
                    }
                }
            }, {
                style: {
                    paddingLeft: '15px'
                },
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.descriptioncolumn,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.descriptioncolumn'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__model__DOT__descriptionColumn}'
                    }
                }
            }]
        }, {
            // path
            // url
            items: [{
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.path,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.path'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__path}'
                    }
                }
            }, {
                style: {
                    paddingLeft: '15px'
                },
                defaults: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.url,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.url'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__sharepoint__DOT__url}'
                    }
                }
            }]
        }]
    }, {

        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.attachmentsvalidation,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.attachmentsvalidation'
        },
        items: [{
            layout: 'column',
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('maxFileSize', {
                    maxFileSize: {
                        minValue: 1,
                        step: 1,
                        fieldcontainer: {
                            allowBlank: true,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxfilesize,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxfilesize'
                            },
                            cls: 'ignore-first-type-rule'
                        },
                        unitOfMeasure: 'MB',
                        cls: 'ignore-first-type-rule',
                        bind: {
                            value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__regularAttachments__DOT__maxFileSize}'
                        }
                    }
                })
            ]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'textarea',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.allowonlythisfiletypesforcardandemail,
                    emptyText: CMDBuildUI.locales.Locales.administration.systemconfig.allowonluthisfiletypesemptyvalue,
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.administration.systemconfig.allowonluthisfiletypesemptyvalue',
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.allowonlythisfiletypesforcardandemail'
                    },

                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__regularAttachments__DOT__allowedFileExtensions}',
                        readOnly: '{actions.view}'
                    },
                    resizable: {
                        handles: "s"
                    },
                    listeners: {
                        blur: function (textarea, event, eOpts) {
                            var value = textarea.getValue();
                            var extensions = value.split(',');
                            var cleanedExtension = [];
                            Ext.Array.forEach(extensions, function (item) {
                                var value = item.trim();
                                if (value && value.length) {
                                    cleanedExtension.push(value);
                                }
                            });
                            textarea.setValue(cleanedExtension.join(','));
                        }
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'textarea',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.allowonlythisfiletypesofincomingmailattachments,
                    emptyText: CMDBuildUI.locales.Locales.administration.systemconfig.allowonluthisfiletypesemptyvalue,
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.administration.systemconfig.allowonluthisfiletypesemptyvalue',
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.allowonlythisfiletypesofincomingmailattachments'
                    },

                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__incomingEmailAttachments__DOT__allowedFileExtensions}',
                        readOnly: '{actions.view}'
                    },
                    resizable: {
                        handles: "s"
                    },
                    listeners: {
                        blur: function (textarea, event, eOpts) {
                            var value = textarea.getValue();
                            var extensions = value.split(',');
                            var cleanedExtension = [];
                            Ext.Array.forEach(extensions, function (item) {
                                var value = item.trim();
                                if (value && value.length) {
                                    cleanedExtension.push(value);
                                }
                            });
                            textarea.setValue(cleanedExtension.join(','));
                        }
                    }
                }]
            }]
        }]
    }]
});