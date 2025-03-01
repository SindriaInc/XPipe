Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-dmscategorytypes-tabitems-values-card-viewinrow',

    mixins: [
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.ToolsMixin'
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
     * @param {CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        Ext.asap(function(){            
            try {
                view.mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
            } catch (error) {
                CMDBuildUI.util.Logger.log("unable to mask lookup value forminrow", CMDBuildUI.util.Logger.levels.debug);
            }
        });
        var me = this;
        var vm = me.getViewModel();
        
        var selected = view._rowContext.record;
        var panelVm = Ext.getCmp('CMDBuildAdministrationContentDMSCategoryTypesView').getViewModel();        
        vm.set('_is_system', panelVm.get('theDMSCategoryType._is_system'));

        CMDBuildUI.model.dms.DMSCategory.getProxy().setExtraParam('active', false);
        CMDBuildUI.model.dms.DMSCategory.getProxy().setUrl( CMDBuildUI.util.administration.helper.ApiHelper.server.getDMSCategoryValuesUrl(panelVm.get('objectTypeName')));
        vm.linkTo('theValue', {
            type: 'CMDBuildUI.model.dms.DMSCategory',
            id: selected.get('_id')
        });
        me.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);      
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
                Ext.asap(function(){
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
    }
});