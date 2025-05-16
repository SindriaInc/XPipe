Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabitems-values-card-viewinrow',

    mixins: [
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ToolsMixin'
    ],
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
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
     * @param {CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        Ext.asap(function () {
            try {
                view.mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
            } catch (error) {
                CMDBuildUI.util.Logger.log("unable to mask lookup value forminrow", CMDBuildUI.util.Logger.levels.debug);
            }
        });

        var me = this;
        var vm = me.getViewModel();

        var selected = view._rowContext.record;
        vm.set('_is_system', selected.get('_is_system'));
        var lookupType = CMDBuildUI.util.Utilities.stringToHex(selected.get('_type'));

        CMDBuildUI.model.lookups.Lookup.getProxy().setExtraParam('active', false);
        CMDBuildUI.model.lookups.Lookup.getProxy().setUrl('/lookup_types/' + lookupType + '/values/');

        vm.linkTo('theValue', {
            type: 'CMDBuildUI.model.lookups.Lookup',
            id: selected.get('_id')
        });
        me.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        var emptyStore = Ext.create('Ext.data.Store', {
            fields: ['_id', 'description'],
            data: [],
            proxy: {
                type: 'memory'
            }
        });
        vm.set('parentValuesStore', view.up('panel').up().getViewModel().get('parentLookupsStore') || emptyStore);
    },

    onAfterRender: function (view) {
        var me = this;
        var vm = me.getViewModel();
        vm.bind({
            bindTo: {
                theValue: '{theValue}'
            }
        }, function (data) {
            if (data.theValue) {
                Ext.asap(function () {
                    try {
                        view.unmask();
                    } catch (error) {
                        CMDBuildUI.util.Logger.log("unable to unmask view in row", CMDBuildUI.util.Logger.levels.debug);
                    }
                });
            } else {
                CMDBuildUI.util.Logger.log("unable to unmask view in row, theValue is undefined", CMDBuildUI.util.Logger.levels.debug);
            }
        });
    },

    /**
     * On translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theValue = vm.get('theValue');
        var translationCode = Ext.String.format('lookup.{0}.{1}.description', theValue.get('_type'), theValue.get('code'));
        var popup = CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, CMDBuildUI.util.administration.helper.FormHelper.formActions.view, 'theTranslation', vm);
        popup.setPagePosition(event.getX() - 450, event.getY() + 20);
    }
});