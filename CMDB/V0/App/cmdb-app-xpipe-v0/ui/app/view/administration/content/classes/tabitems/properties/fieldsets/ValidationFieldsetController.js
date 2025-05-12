Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.ValidationFieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabitems-properties-fieldsets-valididationfieldset',

    onAceEditorValidationExpand: function () {
        var vm = this.getViewModel();
        var theObject = vm.get('theObject');
        var value = theObject.get('validationRule');
        var elementId = 'theObject_validationRule';
        var popupId = Ext.String.format('popup-class-{0}', elementId);

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
                                vm.set('theAttribute.validationRule', editor.getValue());
                                var mainInput = CMDBuildUI.util.Navigation.getMainAdministrationContainer().down('#validationRule');
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
                                theObject.set('validationRule', value);
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

                                editor.getSession().setMode("ace/mode/javascript");
                                editor.getSession().setUseWrapMode(true);
                                editor.setValue(value, -1);

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
                                    theObject.set('validationRule', doc.getValue());
                                };

                                editor.getSession().on('change', onUpdateFunction);
                            }
                        }
                    }]
                }]
            };

        popUp = CMDBuildUI.util.Utilities.openPopup(
            popupId,
            CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.validation.inputs.validationRule.label,
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

});