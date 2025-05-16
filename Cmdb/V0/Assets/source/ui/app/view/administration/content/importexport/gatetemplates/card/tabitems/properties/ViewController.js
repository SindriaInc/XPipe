Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-importexport-gatetemplates-card-tabitems-properties-view',
    mixins: [
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.prperties.ViewMixin'
    ],
    control: {
        '#': {
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
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        }

    },

    /**
     * 
     * @param {CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.View} view 
     */
    onAfterRender: function (view) {
        switch (view.lookupViewModel().get('gateType')) {
            case 'gis':
            case 'cad':
                view.add(CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.FormHelper.getGeoserverDisabledMessage());
                view.add(CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.FormHelper.getShapePropertiesFieldset());
                break;
            case 'ifc':
                view.add(CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.FormHelper.getAssociationPropertiesFieldset());
                break;
            case 'database':
                view.add(CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.FormHelper.getDatabasePropertiesFieldset());
                break;
            default:
                break;
        }
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
        var vm = this.getViewModel();
        var theGate = vm.get('theGate');
        if (theGate.isValid()) {
            CMDBuildUI.util.Utilities.showLoader(true, button.up('panel'));
            button.setDisabled(true);
            // remove not needed field
            var handlers = theGate.handlers();
            handlers.each(function (handler) {
                delete handler.data._shape_import_include_or_exclude;
                delete handler.data._shape_import_target_attr_description;
                delete handler.data._shape_import_key_attr_description;
            });
            theGate.save({
                success: function (record, operation) {
                    vm.getParent().set('enabledTab', null);
                    var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getGateTemplateUrl(theGate.get('config').tag, record.get('_id'));
                    if (vm.get('action') === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
                        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                            function () {
                                if (button.el.dom) {
                                    button.setDisabled(false);
                                }
                                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
                                CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                            });
                    } else {
                        // CMDBuildUI.util.Stores.loadClassesStore().then(function () {
                        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                        CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, record.get('description'), me);
                        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                        if (button.el.dom) {
                            button.setDisabled(false);
                        }
                        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
                        // });
                    }
                },
                failure: function () {
                    button.setDisabled(false);
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
        var me = this;
        var vm = me.getViewModel();
        var nextUrl;
        if (vm.get('action') === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
            nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getGateTemplateUrl(vm.get("theGate.config.tag"));
        } else {
            nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getGateTemplateUrl(vm.get("theGate.config.tag"), vm.get("theGate._id"));
        }
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
    }
});