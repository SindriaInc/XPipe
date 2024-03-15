Ext.define('CMDBuildUI.view.administration.content.dashboards.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dashboards-tabpanel',
    data: {
        activeTab: 0,
        disabledTabs: {
            properties: false,
            permissions: false
        },
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        actions: {
            view: false,
            edit: true,
            add: false
        }
    },
    formulas: {
        tabManager: {
            bind: '{action}',
            get: function (action) {
                if (action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
                    this.toggleEnableTabs(0);
                }
            }
        },
        panelTitle: {
            bind: {
                theDashboard: '{theDashboard}',
                description: '{theDashboard.description}'
            },
            get: function (data) {
                var title = Ext.String.format(
                    '{0} {1} {2}',
                    CMDBuildUI.locales.Locales.administration.dashboards.dashboards,
                    data.description ? ' - ' : '',
                    data.description
                );
                this.getParent().set('title', title);
            }
        }

    },

    /**
    * 
    * @param {Number} currrentTabIndex 
    */
    toggleEnableTabs: function (currrentTabIndex) {
        var me = this;
        var view = me.getView();
        var tabs = view.items.items;

        tabs.forEach(function (tab) {
            if (tab.tabConfig.tabIndex !== currrentTabIndex) {
                me.set('disabledTabs.' + tab.reference, true);
            }
        });

    }
});