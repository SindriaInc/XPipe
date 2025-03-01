Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.IfcTemplateMixin', {
    mixinId: 'administration-task-importifcmixin',
    mixins: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin'
    ],
    requires: [
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],
    ifc: {
        getGeneralPropertyPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer(
                    [
                        ctx.getNameInput(theVmObject, 'code'),
                        ctx.getDescriptionInput(theVmObject, 'description')
                    ]
                )
            ];

            return items;

        },


        getCronPanel: function (theVmObject, step, data, ctx) {
            var items = [
                /**
                 * Cron: combo con valori: Every hour, Every day, Every month, Every year, Custom.
                 * Se Cron è Custom allora compariranno i campi per impostare il cron, come per i task asincroni in CMDBuild 2.5.
                 */
                ctx.getRowFieldContainer(
                    [
                        ctx.getBasicCronInput(theVmObject, 'config.cronExpression')
                    ]
                ),
                ctx.getRowFieldContainer(
                    [
                        ctx.getAdvancedCronInput(theVmObject, 'config.cronExpression')
                    ], {
                    layout: 'fit'
                }
                )
            ];

            return items;
        },

        getSettingsPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer(
                    [
                        ctx.getRowFieldContainer(
                            [
                                ctx.getIfcGateTemplateInput(theVmObject, 'config.gateconfig_handlers_1_target', {
                                    allowBlank: false
                                })
                            ]
                        ),
                        ctx.getRowFieldContainer(
                            [
                                ctx.getIFCSourceTypeInput(theVmObject, 'config.gateconfig_handlers_0_type')
                            ]
                        ),
                        ctx.getRowFieldContainer(
                            [
                                ctx.getBimProjectInput(theVmObject, 'config.gateconfig_handlers_0_bimprojectId')
                            ], {
                            bind: {
                                hidden: '{' + theVmObject + '.config.gateconfig_handlers_0_type !== "urlreader"}'
                            },
                            hidden: true,
                            listeners: {
                                hide: function (component, eOpts) {
                                    var input = component.down('#gateconfig_handlers_0_bimprojectId_input');
                                    input.setValue('');
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                                },
                                show: function (component, eOpts) {
                                    var input = component.down('#gateconfig_handlers_0_bimprojectId_input');
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                                }
                            }
                        }
                        ),
                        ctx.getRowFieldContainer(
                            [
                                ctx.getDirectoryInput(theVmObject, 'config.gateconfig_handlers_0_directory', {
                                    allowBlank: true
                                }),
                                ctx.getFilepatternInput(theVmObject, 'config.gateconfig_handlers_0_filePattern', {
                                    allowBlank: true
                                })
                            ], {
                            bind: {
                                hidden: '{' + theVmObject + '.config.gateconfig_handlers_0_type !== "filereader"}'
                            },
                            hidden: true,
                            listeners: {
                                hide: function (component, eOpts) {
                                    var directoryInput = component.down('#gateconfig_handlers_0_directory_input');
                                    directoryInput.setValue('');
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(directoryInput, true, directoryInput.up('form'));

                                    var filePatternInput = component.down('#gateconfig_handlers_0_filePattern_input');
                                    filePatternInput.setValue('');
                                },
                                show: function (component, eOpts) {
                                    var directoryInput = component.down('#gateconfig_handlers_0_directory_input');
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(directoryInput, false, directoryInput.up('form'));
                                }
                            }
                        }
                        ),
                        ctx.getRowFieldContainer(
                            [
                                ctx.getPostImportActionInput(theVmObject, 'config.gateconfig_handlers_0_postImportAction'),
                                ctx.getTargetDirectoryInput(theVmObject, 'config.gateconfig_handlers_0_targetDirectory', {
                                    allowBlank: true,
                                    fieldcontainer: {
                                        bind: {
                                            hidden: '{!isMoveFiles}'
                                        },
                                        hidden: true,
                                        listeners: {
                                            hide: function (component, eOpts) {
                                                var input = component.down('#gateconfig_handlers_0_targetDirectory_input');
                                                input.setValue('');
                                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                                            },
                                            show: function (component, eOpts) {
                                                var input = component.down('#gateconfig_handlers_0_targetDirectory_input');
                                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                                            }
                                        }
                                    }
                                })
                            ], {
                            hidden: true,
                            bind: {
                                hidden: '{' + theVmObject + '.config.gateconfig_handlers_0_type !== "filereader"}'
                            },
                            listeners: {
                                hide: function (component, eOpts) {
                                    var postImportActionInput = component.down('#gateconfig_handlers_0_postImportAction_input');
                                    postImportActionInput.setValue('');
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(postImportActionInput, true, postImportActionInput.up('form'));
                                },
                                show: function (component, eOpts) {
                                    var postImportActionInput = component.down('#gateconfig_handlers_0_postImportAction_input');
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(postImportActionInput, false, postImportActionInput.up('form'));
                                }
                            }
                        }
                        ),

                        ctx.getRowFieldContainer(
                            [
                                ctx.getAssociationModeInput(theVmObject, 'config.gateconfig_handlers_1_config_bimserver_project_master_card_mode')
                            ], {
                            bind: {
                                hidden: '{' + theVmObject + '.config.gateconfig_handlers_0_type === "urlreader"}'
                            },
                            hidden: true,
                            listeners: {
                                hide: function (component, eOpts) {
                                    var input = component.down('#gateconfig_handlers_1_config_bimserver_project_master_card_mode_input');
                                    input.setValue('');
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                                },
                                show: function (component, eOpts) {
                                    var input = component.down('#gateconfig_handlers_1_config_bimserver_project_master_card_mode_input');
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                                }
                            }
                        }
                        ),

                        ctx.getRowFieldContainer(
                            [
                                ctx.getTargetCardIdInput(theVmObject, 'config.gateconfig_handlers_1_config_bimserver_project_master_card_id', {
                                    bind: {
                                        hidden: '{' + theVmObject + '.config.gateconfig_handlers_1_config_bimserver_project_master_card_mode !== "auto"}'
                                    },
                                    hidden: true
                                })
                            ]
                        )
                    ]
                )
            ];

            return items;
        },

        getNotificationPanel: function (theVmObject, step, data, ctx) {
            var items = [

                ctx.getRowFieldContainer([
                    ctx.getNotificationInput(theVmObject, 'config.notificationMode')
                ]),
                ctx.getRowFieldContainer([
                    /*
                     * Error email template: combo con elenco dei template delle email. Placeholder: Use the one defined in template.
                     * Account: combo con elenco degli account. Placeholder: Use the one defined in template.
                     */
                    ctx.getRowFieldContainer(
                        [
                            ctx.getErrorEmailTemplateInput(theVmObject, 'config.errorTemplate', {
                                allowBlank: true
                            }),
                            ctx.getErrorEmailAccountInput(theVmObject, 'config.errorAccount')
                        ]),
                    /**
                     * email template: combo con elenco dei template delle email. Placeholder: Use the one defined in template.
                     * Account: combo con elenco degli account. Placeholder: Use the one defined in template.
                     */

                    ctx.getRowFieldContainer(
                        [
                            ctx.getNotificationEmailTemplateInput(theVmObject, 'config.notificationTemplate'),
                            ctx.getAttachImportReport(theVmObject, 'config.attachImportReport')
                        ])
                ], {
                    hidden: true,
                    bind: {
                        hidden: '{isNeverNotification}'
                    },
                    listeners: {
                        hide: function (component, eOpts) {
                            var input = component.down('#errorEmailTemplate_input');
                            input.setValue('');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, true, input.up('form'));
                        },
                        show: function (component, eOpts) {
                            var input = component.down('#errorEmailTemplate_input');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(input, false, input.up('form'));
                        }
                    }
                })


            ];

            return items;
        },


        validateForm: function (form) {

            var _form = form.form,
                vm = form.lookupViewModel(),
                invalid = [];
            _form.getFields().items.forEach(function (field) {
                if (!field.validate()) {
                    invalid.push(field);
                }
            });

            invalid = CMDBuildUI.util.administration.helper.CronValidatorHelper.taskCronValidation(vm, invalid);
            if (invalid.length) {
                CMDBuildUI.util.administration.helper.FormHelper.showInvalidFieldsMessage({ items: invalid });
            }

            return invalid;
        }

    }



});