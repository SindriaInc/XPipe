Ext.define('CMDBuildUI.view.joinviews.configuration.ConfigurationModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.joinviews-configuration-configuration',
    data: {
        activeTab: null,
        theView: null,

        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_views_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },
        theViewManager: {
            bind: '{theView}',
            get: function (theView) {
                if (this.get('showForm') === 'false') {
                    var title = this.get('isAdministrationModule') ? CMDBuildUI.locales.Locales.administration.navigation.views + ' - ' + CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromjoin : this.get('theView').phantom ? CMDBuildUI.locales.Locales.joinviews.newjoinview : CMDBuildUI.locales.Locales.joinviews.joinview;
                    this.set('panelTitle', title);
                }
                Ext.asap(function () {
                    this.set('activeTab', this.get('activeTabs.joinView') || 0);
                }, this);
            }
        },
        panelTitleManager: {
            bind: {
                panelTitle: '{panelTitle}'
            },
            get: function (data) {
                var me = this;
                me.getParent().set('title', data.panelTitle);
            }
        }
    }

});