Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.card.Card', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-importexport-datatemplates-card',

    requires: [
        'CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardController',
        'CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],
    controller: 'view-administration-content-importexport-datatemplates-card',
    viewModel: {
        type: 'view-administration-content-importexport-datatemplates-card'
    },
    bubbleEvents: [
        'itemupdated',
        'cancelupdating'
    ],
    modelValidation: true,

    hidden: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,
    ui: 'administration-formpagination',
    items: [

    ],

    initComponent: function () {
        Ext.asap(function () {
            try {
                this.up().mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
            } catch (error) {

            }
        }, this);
        this.callParent(arguments);
    }
});