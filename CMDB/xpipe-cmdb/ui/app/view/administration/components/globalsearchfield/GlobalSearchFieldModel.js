Ext.define('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchFieldModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-globalsearchfield-globalsearchfield',

    formulas: {
        emptyText: function () {
            var type = this.getView().getObjectType();
            if (type === 'etlgates') {
                type += this.getView().getSubType();
            }
            return CMDBuildUI.locales.Locales.administration.globalsearch.emptyText[type];
        }
    }
});