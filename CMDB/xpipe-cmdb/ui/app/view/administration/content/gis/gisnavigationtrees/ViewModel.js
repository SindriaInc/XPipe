Ext.define('CMDBuildUI.view.administration.content.gisnavigationtrees.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gisnavigationtrees-view',

    data: {
        actions: {
            view: false,
            edit: false,
            add: false
        },
        hideForm: false,
        toolAction: {
            _canAdd: false,
            _canClone: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_gis_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canClone', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },

        action: {
            bind: {
                view: '{actions.view}',
                add: '{actions.add}',
                edit: '{actions.edit}'
            },
            get: function (data) {
                if (data.edit) {
                    this.set('formModeCls', 'formmode-edit');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.add) {
                    this.set('gisnavigationactive', true);
                    this.set('formModeCls', 'formmode-add');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    this.set('formModeCls', 'formmode-view');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },

        dataManager: {
            bind: {
                theNavigationtree: '{theNavigationtree}',
                targetClass: '{theNavigationtree.targetClass}'
            },
            get: function (data) {                
                if (!data.targetClass && data.theNavigationtree.get('nodes') && data.theNavigationtree.get('nodes').length) {
                    data.theNavigationtree.set('targetClass', data.theNavigationtree.get('nodes')[0].targetClass);
                } else {
                    data.theNavigationtree.set('targetClass', data.targetClass);
                }
            }
        },

        gisnavigationactiveManager: {
            bind: '{theNavigationtree}',
            get: function (theNavigationtree) {
                var me = this;                
                if(theNavigationtree && !me.get('gisnavigationactive')){
                    CMDBuildUI.util.administration.helper.ConfigHelper.getConfig('org__DOT__cmdbuild__DOT__gis__DOT__navigation__DOT__enabled').then(function (enabled) {
                        me.set('gisnavigationactive', enabled);
                    });
                }
            }
        },
        formtoolbarHidden: {
            bind: {
                isView: '{actions.view}',
                isHiddenForm: '{hideForm}'
            },
            get: function (data) {
                if (data.isView && !data.isHiddenForm) {
                    return false;
                }
                return true;
            }
        }
    }
});