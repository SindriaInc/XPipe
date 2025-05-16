Ext.define('CMDBuildUI.view.main.notifications.MenuListModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.main-notifications-menulist',

    formulas: {
        emptyTextMessage: {
            get: function() {
                return Ext.String.format(
                    '<div class="{0}notification-empty-message"><p><span class="{0}fa fa-3x fa-info-circle"></span></p>{1}</div>',
                    Ext.baseCSSPrefix,
                    CMDBuildUI.locales.Locales.notifications.emptymessage
                );
            }
        }
    }
});
