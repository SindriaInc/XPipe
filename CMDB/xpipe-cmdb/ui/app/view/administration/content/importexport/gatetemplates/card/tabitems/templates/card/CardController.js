Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.CardController', {
    extend: 'Ext.app.ViewController',
    mixins: [
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.CardMixin'
    ],
    alias: 'controller.view-administration-content-importexport-gatetemplates-tabitems-templates-card',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        }

    },

    /**
     * @param {CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.CardController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        if (vm.get('actions.view')) {
            var topbar = {
                xtype: 'components-administration-toolbars-formtoolbar',
                dock: 'top',
                hidden: true,
                bind: {
                    hidden: '{!actions.view}'
                },
                items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                        edit: true, // #editBtn set true for show the button
                        view: false, // #viewBtn set true for show the button
                        clone: true, // #cloneBtn set true for show the button
                        'delete': true, // #deleteBtn set true for show the button
                        activeToggle: true // #enableBtn and #disableBtn set true for show the buttons       
                    },

                    /* testId */
                    'importexportgatetemplates',

                    /* viewModel object needed only for activeTogle */
                    'theGateTemplate',

                    /* add custom tools[] on the left of the bar */
                    [],

                    /* add custom tools[] before #editBtn*/
                    [],

                    /* add custom tools[] after at the end of the bar*/
                    []
                )
            };
            view.addDocked(topbar);

        } else {
            var formButtons = {
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                hidden: true,
                bind: {
                    hidden: '{actions.view}'
                },
                items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true, {}, {
                    listeners: {
                        mouseover: function () {
                            var invalidFields = CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(this.up('form').form);
                            Ext.Array.forEach(invalidFields, function (field) {
                                CMDBuildUI.util.Logger.log(Ext.String.format('{0} is invalid', field.itemId), CMDBuildUI.util.Logger.levels.debug);
                            });
                        }
                    }
                })
            };
            view.addDocked(formButtons);
        }
    },

    onAfterRender: function (view) {
        Ext.asap(function () {
            try {
                view.setHidden(false);
                view.up().unmask();
            } catch (error) {

            }
        }, this);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = me.getView();
        var vm = this.getViewModel();
        var theGateTemplate = vm.get('theGateTemplate');
        var grid = vm.get('grid');
        if (theGateTemplate.isValid()) {
            CMDBuildUI.util.Utilities.showLoader(true, button.up('panel'));
            theGateTemplate.save({
                success: function (record, operation) {
                    var theGate = grid.getViewModel().get('theGate');
                    var handler = theGate.handlers().first();
                    delete handler.data._shape_import_include_or_exclude;
                    delete handler.data._shape_import_target_attr_description;
                    delete handler.data._shape_import_key_attr_description;
                    handler.addTemplate(record.get('code'));
                    theGate.save({
                        success: function (gate, gateOperation) {
                            // var eventToCall = (operation.getRequest().getMethod() === 'PUT') ? 'itemupdated' : 'itemcreated';
                            handler.getTemplates().then(function (templatesStore) {
                                Ext.suspendLayouts();
                                grid.lookupViewModel().get('allGateTemplates').setData(templatesStore.getRange());
                                Ext.resumeLayouts();
                                form.up().fireEvent("closed");
                            });
                        }
                    });
                },
                failure: function () {
                    CMDBuildUI.util.Utilities.showLoader(false, button.up('panel'));
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        vm.get("theGateTemplate").reject(); // discard changes
        this.getView().up().fireEvent("closed");
    }
});