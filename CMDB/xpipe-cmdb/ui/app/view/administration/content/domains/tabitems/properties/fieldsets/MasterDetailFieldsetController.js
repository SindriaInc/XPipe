Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.MasterDetailFieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-tabitems-properties-fieldsets-masterdetailfieldset',


    onAllDetailAttributesDatachanged: function (store) {
        var vm = this.getViewModel();
        var allAggregateAttrs = vm.get('theDomain.masterDetailAggregateAttrs');

        Ext.Array.forEach(allAggregateAttrs, function (id) {
            var record = store.getById(id);
            if (record) {
                vm.getStore('masterDetailAggregateAttrsStore').add(record);
            }
        });
        vm.get('newSelectedAttributesStore').removeAll();
        vm.get('newSelectedAttributesStore').add(CMDBuildUI.model.Attribute.create());

    }
});
