Ext.define('CMDBuildUI.view.administration.content.setup.elements.LogRetentionController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-logretention',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnCLick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onBeforeRender: function (view) {
        this.loadData();
        this.createFieldsets(view);
    },



    onEditBtnCLick: function () {
        var vm = this.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        vm.set('disabledTabs.log', true);
        vm.set('disabledTabs.retention', false);
        vm.set('disabledTabs.audit', true);
    },

    onSaveBtnClick: function (button) {
        var me = this,
            vm = button.lookupViewModel();
        Ext.getBody().mask(CMDBuildUI.locales.Locales.administration.common.messages.saving);

        var mode = vm.get('mode').selection ? vm.get('mode').selection.get('value') : vm.get('mode');

        if (mode !== 'default') {
            var rules = vm.get('theSetup.org__DOT__cmdbuild__DOT__database__DOT__cleanup_rules').split(','),
                _rules = [];

            rules.forEach(function (rule) {
                var match = vm.get(Ext.String.format('theSetup.{0}__DOT__{1}__DOT__{2}', me._baseParam, rule, 'match'));
                if (match === 'filter') {
                    _rules.push(Ext.String.format('{0}_else', rule));
                }
            });

            vm.set('theSetup.org__DOT__cmdbuild__DOT__database__DOT__cleanup_rules', Ext.Array.merge([], rules, _rules).join(','));
        }

        var setData = CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(
            /** theSetup */
            vm.get('theSetup'),
            /** reloadOnSucces */
            true,
            /** forceDropCache */
            false,
            this
        );

        setData.then(function (transport) {
            me.onCancelBtnClick();
        });
        setData.always(function () {
            if (!button.destroyed) {
                button.enable();
            }
            if (Ext.getBody().isMasked()) {
                Ext.getBody().unmask();
            }
        });
    },
    onCancelBtnClick: function () {
        var vm = this.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        vm.set('disabledTabs.log', false);
        vm.set('disabledTabs.retention', false);
        vm.set('disabledTabs.audit', false);
        this.loadData();

    },
    onAceEditorExpandClick: function (event) {
        this.onAceExpand(Ext.getCmp(event.target.closest("[id^='aceeditortextarea'].x-field").id).inputField);
    },
    privates: {
        _baseParam: 'org__DOT__cmdbuild__DOT__database__DOT__cleanup_rule',
        createFieldsets: function (view) {
            var me = this,
                vm = view.lookupViewModel(),
                fielsets = [{
                    name: 'systemstatuslog',
                    description: CMDBuildUI.locales.Locales.administration.systemconfig.systemstatuslog,
                    defaultParam: 'maxRecordAgeToKeepSeconds',
                    defaultValue: 7776000
                }, {
                    name: 'request',
                    description: CMDBuildUI.locales.Locales.administration.systemconfig.request,
                    defaultParam: 'maxRecordsToKeep',
                    defaultValue: 50000
                }, {
                    name: 'eventlog',
                    description: CMDBuildUI.locales.Locales.administration.systemconfig.eventlog,
                    defaultParam: 'maxRecordAgeToKeepSeconds',
                    defaultValue: 7776000
                }, {
                    name: 'jobrun',
                    description: CMDBuildUI.locales.Locales.administration.systemconfig.jobrun,
                    defaultParam: 'maxRecordsToKeep',
                    defaultValue: 50000
                }, {
                    name: 'etlmessage',
                    description: CMDBuildUI.locales.Locales.administration.navigation.busmessages,
                    defaultParam: 'maxRecordsToKeep',
                    defaultValue: 10000
                }];
            fielsets.forEach(function (fieldset) {

                var validator = function (val) {
                    var errMsg = CMDBuildUI.locales.Locales.errors.fieldrequired,
                        isDefault = vm.get('isDefault');
                    if (isDefault) {
                        return true;
                    }
                    var maxAge = vm.get(Ext.String.format('theSetup.{0}__DOT__{1}__DOT__{2}', me._baseParam, fieldset.name, 'maxRecordAgeToKeepSeconds')),
                        maxRecords = vm.get(Ext.String.format('theSetup.{0}__DOT__{1}__DOT__{2}', me._baseParam, fieldset.name, 'maxRecordsToKeep')),
                        maxSize = vm.get(Ext.String.format('theSetup.{0}__DOT__{1}__DOT__{2}', me._baseParam, fieldset.name, 'maxSizeMegs'));
                    return (Ext.isEmpty(maxAge) && Ext.isEmpty(maxRecords) && Ext.isEmpty(maxSize)) ?
                        errMsg : true;
                };
                view.add({
                    hidden: true,
                    bind: {
                        hidden: '{isDefault}'
                    },
                    xtype: 'fieldset',
                    ui: 'administration-formpagination',
                    columnWidth: 1,
                    title: fieldset.description,
                    listeners: {
                        show: function () {
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(this.down('#match_input'), false, this.up('form'));
                        },
                        hide: function () {
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(this.down('#match_input'), true, this.up('form'));
                        }
                    },
                    items: [{
                        layout: 'column',
                        xtype: 'fieldcontainer',
                        columnWidth: 1,
                        items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('match', {
                            match: {
                                fieldcontainer: {
                                    columnWidth: 0.33,
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.match
                                },
                                combofield: {
                                    forceSelection: true,
                                    viewModel: {
                                        stores: {
                                            comboStore: {
                                                proxy: 'memory',
                                                autoDestroy: true,
                                                data: [{
                                                    value: 'always',
                                                    label: CMDBuildUI.locales.Locales.administration.systemconfig.always
                                                }, {
                                                    value: 'filter',
                                                    label: CMDBuildUI.locales.Locales.administration.systemconfig.filter
                                                }]
                                            }
                                        }
                                    },
                                    bind: {
                                        store: '{comboStore}',
                                        value: Ext.String.format('{theSetup.{0}__DOT__{1}__DOT__{2}}', me._baseParam, fieldset.name, 'match')
                                    }
                                },
                                columnWidth: 1,
                                displayfield: {
                                    bind: {
                                        value: Ext.String.format('{theSetup.{0}__DOT__{1}__DOT__{2}}', me._baseParam, fieldset.name, 'match')
                                    },
                                    renderer: function (value) {
                                        if (value) {
                                            var combo = this.up('fieldcontainer').down('combo'),
                                                store = combo.getViewModel().get('comboStore');
                                            return store.findRecord('value', value).get('label');
                                        }
                                        return value;
                                    }
                                }
                            }
                        }), {
                            xtype: 'fieldcontainer',
                            columnWidth: 0.66,
                            hidden: true,
                            bind: {
                                hidden: Ext.String.format('{theSetup.{0}__DOT__{1}__DOT__{2} != "filter"}', me._baseParam, fieldset.name, 'match')
                            },
                            listeners: {
                                show: function () {
                                    var editor = this.down('aceeditortextarea');
                                    editor.setAllowBlank(false);
                                    this.up('form').isValid();
                                },
                                hide: function () {
                                    var editor = this.down('aceeditortextarea');
                                    editor.setAllowBlank(true);
                                    this.up('form').isValid();
                                }
                            },
                            items: [{
                                columnWidth: 0.66,
                                xtype: 'aceeditortextarea',
                                allowBlank: true,
                                vmObjectName: 'theSetup',
                                inputField: Ext.String.format('{0}__DOT__{1}__DOT__{2}', me._baseParam, fieldset.name, 'filter'),
                                itemId: Ext.String.format('{0}__DOT__{1}__DOT__{2}', me._baseParam, fieldset.name, 'filter'),
                                options: {
                                    mode: 'ace/mode/json',
                                    readOnly: true
                                },
                                bind: {
                                    readOnly: '{actions.view}',
                                    config: {
                                        options: {
                                            readOnly: '{actions.view}'
                                        }
                                    }
                                },
                                validator: validator,
                                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.filter + ' *',
                                minHeight: '85px',
                                labelToolIconCls: 'fa-expand',
                                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
                                labelToolIconClick: 'onAceEditorExpandClick',
                                /**
                                 * @override
                                 */
                                onChange: function () {
                                    var vm = this.lookupViewModel();
                                    this.lastValue = this.value;
                                    this.value = this.getAceEditor().getSession().getValue();
                                    vm.set(Ext.String.format('theSetup.{0}', Ext.String.format('{0}__DOT__{1}__DOT__{2}', me._baseParam, fieldset.name, 'filter')), this.value);
                                    this.isCodeValid();
                                    this.fireEvent('change', this, this.value, this.lastValue);
                                    this.getErrors();
                                    this.up('form').isValid();
                                },
                                listeners: {
                                    render: function (element) {
                                        var aceEditor = element.getAceEditor();
                                        var vm = element.lookupViewModel();
                                        vm.bind({
                                            bindTo: {
                                                theSetup: '{theSetup}'
                                            },
                                            single: true
                                        }, function (data) {
                                            if (data.theSetup) {
                                                aceEditor.setValue(data.theSetup[Ext.String.format('{0}__DOT__{1}__DOT__{2}', me._baseParam, fieldset.name, 'filter')] || '', -1);
                                            }
                                        });
                                        vm.bind({
                                            isView: '{actions.view}'
                                        }, function (data) {
                                            aceEditor.setReadOnly(data.isView);
                                        });
                                    }
                                },
                                name: Ext.String.format('{0}__DOT__{1}__DOT__{2}', me._baseParam, fieldset.name, 'filter')
                            }]
                        }]
                    }, {
                        layout: 'column',
                        xtype: 'fieldcontainer',
                        columnWidth: 1,
                        items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('maxRecordAgeToKeepSeconds', {
                            maxRecordAgeToKeepSeconds: {
                                fieldcontainer: {
                                    columnWidth: 0.33,
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxage
                                },
                                columnWidth: 1,
                                step: 0,
                                minValue: 0,
                                hideTrigger: true,
                                bind: {
                                    value: Ext.String.format('{theSetup.{0}__DOT__{1}__DOT__{2}}', me._baseParam, fieldset.name, 'maxRecordAgeToKeepSeconds')
                                },
                                validator: validator
                            }
                        }), CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('maxRecordsToKeep', {
                            maxRecordsToKeep: {
                                fieldcontainer: {
                                    columnWidth: 0.33,
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxrecords
                                },
                                columnWidth: 1,
                                step: 0,
                                minValue: 0,
                                hideTrigger: true,
                                bind: {
                                    value: Ext.String.format('{theSetup.{0}__DOT__{1}__DOT__{2}}', me._baseParam, fieldset.name, 'maxRecordsToKeep')
                                },
                                validator: validator
                            }
                        }), CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('maxSizeMegs', {
                            maxSizeMegs: {
                                fieldcontainer: {
                                    columnWidth: 0.33,
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxsize
                                },
                                columnWidth: 1,
                                step: 0,
                                minValue: 0,
                                hideTrigger: true,
                                bind: {
                                    value: Ext.String.format('{theSetup.{0}__DOT__{1}__DOT__{2}}', me._baseParam, fieldset.name, 'maxSizeMegs')
                                },
                                validator: validator
                            }
                        })]
                    }, {
                        xtype: 'fieldcontainer',
                        layout: 'column',
                        columnWidth: 1,
                        hidden: true,
                        bind: {
                            hidden: Ext.String.format('{theSetup.{0}__DOT__{1}__DOT__{2} != "filter"}', me._baseParam, fieldset.name, 'match')
                        },
                        listeners: {
                            hide: function (view) {
                                var vm = view.lookupViewModel();
                                vm.set(Ext.String.format('theSetup.{0}__DOT__{1}_else__DOT__{2}', me._baseParam, fieldset.name, 'maxRecordAgeToKeepSeconds'), null);
                                vm.set(Ext.String.format('theSetup.{0}__DOT__{1}_else__DOT__{2}', me._baseParam, fieldset.name, 'maxRecordsToKeep'), null);
                                vm.set(Ext.String.format('theSetup.{0}__DOT__{1}_else__DOT__{2}', me._baseParam, fieldset.name, 'maxSizeMegs'), null);
                            }
                        },
                        items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('maxRecordAgeToKeepSeconds', {
                            maxRecordAgeToKeepSeconds: {
                                fieldcontainer: {
                                    columnWidth: 0.33,
                                    fieldLabel: Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.administration.systemconfig.unmatchingrows, CMDBuildUI.locales.Locales.administration.systemconfig.maxage)
                                },
                                columnWidth: 1,
                                step: 0,
                                minValue: 0,
                                hideTrigger: true,
                                bind: {
                                    value: Ext.String.format('{theSetup.{0}__DOT__{1}_else__DOT__{2}}', me._baseParam, fieldset.name, 'maxRecordAgeToKeepSeconds')
                                }
                            }
                        }), CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('maxRecordsToKeep', {
                            maxRecordsToKeep: {
                                fieldcontainer: {
                                    columnWidth: 0.33,
                                    fieldLabel: Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.administration.systemconfig.unmatchingrows, CMDBuildUI.locales.Locales.administration.systemconfig.maxrecords)
                                },
                                columnWidth: 1,
                                step: 0,
                                minValue: 0,
                                hideTrigger: true,
                                bind: {
                                    value: Ext.String.format('{theSetup.{0}__DOT__{1}_else__DOT__{2}}', me._baseParam, fieldset.name, 'maxRecordsToKeep')
                                }
                            }
                        }), CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('maxSizeMegs', {
                            maxSizeMegs: {
                                fieldcontainer: {
                                    columnWidth: 0.33,
                                    fieldLabel: Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.administration.systemconfig.unmatchingrows, CMDBuildUI.locales.Locales.administration.systemconfig.maxsize)
                                },
                                columnWidth: 1,
                                step: 0,
                                minValue: 0,
                                hideTrigger: true,
                                bind: {
                                    value: Ext.String.format('{theSetup.{0}__DOT__{1}_else__DOT__{2}}', me._baseParam, fieldset.name, 'maxSizeMegs')
                                }
                            }
                        })]
                    }]
                });

            });
        },
        /**
         * Load data from server, format keys and set vm data for binding
         * @private
         */
        loadData: function () {
            var vm = this.getView().getViewModel();
            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(
                function (configs) {
                    if (!vm.destroyed) {
                        configs.forEach(function (key) {
                            if (key._key === 'org__DOT__cmdbuild__DOT__database__DOT__cleanup_rules') {
                                vm.set('defaultMode', key['default']);
                            }
                            vm.set(Ext.String.format('theSetup.{0}', key._key), (key.hasValue) ? key.value : key['default']);
                        });
                    }
                }
            );
        },

        /**
         * @private
         */
        onAceExpand: function (property) {
            var panelTitle = CMDBuildUI.locales.Locales.administration.systemconfig.filter;
            var vm = this.getViewModel();
            var theSetup = vm.get('theSetup');
            var value = theSetup[property];
            var elementId = Ext.String.format('filter_{0}', property);
            var popupId = Ext.String.format('popup-setup-logs-{0}', elementId);

            var editor;

            var popUp,
                content = {
                    xtype: 'panel',
                    layout: {
                        type: 'hbox'
                    },
                    ui: 'administration-formpagination',
                    fieldDefaults: {
                        labelAlign: 'top'
                    },
                    viewModel: {},
                    dockedItems: [{
                        hidden: vm.get('actions.view'),
                        xtype: 'toolbar',
                        dock: 'bottom',
                        ui: 'footer',
                        items: [{
                            xtype: 'component',
                            flex: 1
                        }, {
                            text: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.saveBtn,
                            ui: 'administration-action-small',
                            listeners: {
                                click: function (_button) {
                                    vm.set(Ext.String.format('theSetup.{0}', property), editor.getValue());
                                    var mainInput = CMDBuildUI.util.Navigation.getMainAdministrationContainer().down(Ext.String.format('#{0}', property));
                                    if (mainInput) {
                                        mainInput.getAceEditor().setValue(editor.getValue(), -1);
                                    }
                                    popUp.fireEvent('close');

                                }
                            }
                        }, {
                            text: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.cancelBtn,
                            ui: 'administration-secondary-action-small',
                            listeners: {
                                click: function () {
                                    vm.set(Ext.String.format('theSetup.{0}', property), value);
                                    popUp.fireEvent('close');
                                }
                            }
                        }]
                    }],

                    items: [{
                        flex: 1,
                        layout: 'card',
                        height: '100%',
                        viewModel: {},
                        items: [{
                            xtype: 'component',
                            layout: 'card',
                            height: '100%',
                            viewModel: {},
                            html: Ext.String.format('<div id="{0}"></div>', elementId),
                            listeners: {
                                afterrender: function (cmp) {
                                    editor = ace.edit(elementId, {
                                        //set autoscroll
                                        autoScrollEditorIntoView: true,
                                        maxLines: 90, // this will be changed on "heightUpdateFunction"
                                        // set theme
                                        theme: "ace/theme/chrome",
                                        // show line numbers
                                        showLineNumbers: true,
                                        // hide print margin
                                        showPrintMargin: false,
                                        wrap: true

                                    });
                                    if (vm.get('actions.view')) {
                                        editor.setReadOnly(true);
                                    }

                                    editor.getSession().setMode('ace/mode/json');
                                    editor.getSession().setUseWrapMode(true);
                                    editor.setValue(value || '', -1);

                                    var heightUpdateFunction = function (editor) {
                                        var editorDiv = document.getElementById(elementId); // its container                                
                                        var lineHeight = editor.renderer.lineHeight;
                                        editorDiv.style.height = '100%'; // set new container height 
                                        editorDiv.style.width = '100%'; // force container width to 100%
                                        editor.setOption('maxLines', Math.ceil((cmp.up().getHeight() - lineHeight) / lineHeight));
                                        editor.setOption('minLines', Math.ceil((cmp.up().getHeight() - lineHeight) / lineHeight));
                                    };
                                    cmp.mon(cmp, 'resize', function () {
                                        editor.renderer.$updateSizeAsync();
                                    });

                                    // Set initial size to match initial content
                                    Ext.asap(function (editor) {
                                        heightUpdateFunction(editor);
                                    }, this, [editor]);

                                    // Whenever a change happens inside the ACE editor, update
                                    // the size again
                                    onUpdateFunction = function (event, doc) {
                                        // update stored value 
                                        vm.set(Ext.String.format('theSetup.{0}', property), doc.getValue());
                                    };

                                    editor.getSession().on('change', onUpdateFunction);
                                }
                            }
                        }]
                    }]
                };

            popUp = CMDBuildUI.util.Utilities.openPopup(
                popupId,
                panelTitle,
                content, {
                closed: function () {
                    CMDBuildUI.util.Utilities.closePopup(popupId);
                },
                /**
                 * @param {Ext.panel.Panel} panel
                 * @param {Object} eOpts
                 */
                close: function (panel, eOpts) {
                    CMDBuildUI.util.Utilities.closePopup(popupId);
                }
            }, {
                ui: 'administration',
                width: '80%',
                height: '80%',
                viewModel: {},
                draggable: true
            }
            );
        }
    }

});