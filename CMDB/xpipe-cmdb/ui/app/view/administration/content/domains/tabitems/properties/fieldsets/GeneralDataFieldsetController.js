Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset',

    control: {
        '#': {
            afterrender: 'onAfterRender',
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        // disable or enable cascade actions ask confirm fileds
        vm.bind({
            bindTo: {
                cascadeActionDirect: '{theDomain.cascadeActionDirect}',
                cascadeActionInverse: '{theDomain.cascadeActionInverse}'
            },
            deep: true
        }, function (data) {
            vm.set('cascadeActionDirect_askConfirm_disabled', Ext.isEmpty(data.cascadeActionDirect) || data.cascadeActionDirect === CMDBuildUI.model.domains.Domain.cascadeAction.restrict);
            vm.set('cascadeActionInverse_askConfirm_disabled', Ext.isEmpty(data.cascadeActionInverse) || data.cascadeActionInverse === CMDBuildUI.model.domains.Domain.cascadeAction.restrict)
        });


    },
    onAfterRender: function () {
        if (this.getViewModel().get('actions.add')) {
            this.lookupReference('domainname').maxLength = 20;
        }
    },

    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickDescription: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theDomain = vm.get('theDomain');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDomainDescription(!vm.get('actions.add') ? theDomain.get('name') : '.');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theDomainDescriptionTranslation', vm.getParent(), true);
    },

    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickDirect: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theDomain = vm.get('theDomain');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDomainDirectDescription(!vm.get('actions.add') ? theDomain.get('name') : '.');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theDirectDescriptionTranslation', vm.getParent(), true);
    },


    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickInverse: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theDomain = vm.get('theDomain');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDomainInverseDescription(!vm.get('actions.add') ? theDomain.get('name') : '.');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theInverseDescriptionTranslation', vm.getParent(), true);
    },


    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickMasterDetail: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theDomain = vm.get('theDomain');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDomainMasterDetail(!vm.get('actions.add') ? theDomain.get('name') : '.');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theMasterDetailTranslation', vm.getParent(), true);
    },
    onSourceChange: function (combo, newValue, oldValue) {
        var vm = combo.lookupViewModel();
        this.resetSummaryGrid(combo, newValue, oldValue);
        var isSourceProcess = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(newValue) ? true : false;
        vm.set('theDomain.sourceProcess', isSourceProcess);
    },
    onDestinationChange: function (combo, newValue, oldValue) {
        var vm = combo.lookupViewModel();
        this.resetSummaryGrid(combo, newValue, oldValue);
        var isSourceProcess = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(newValue) ? true : false;
        vm.set('theDomain.destinationProcess', isSourceProcess);
    },
    resetSummaryGrid: function (combo, newValue, oldValue) {
        if (oldValue) {
            var masterDetailAggregateAttrsGrid = combo.up('form').down('#sumattributesGrid');
            masterDetailAggregateAttrsGrid.getStore().removeAll();
        }
    }
});