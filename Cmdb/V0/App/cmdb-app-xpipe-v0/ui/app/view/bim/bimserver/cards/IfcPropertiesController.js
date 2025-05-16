Ext.define('CMDBuildUI.view.bim.bimserver.tab.cards.IfcPropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-bimserver-tab-cards-ifcproperties',
    listen: {
        global: {
            ifcpropertychange: 'onIfcPropertyChange',
            ifcpropertyremove: 'onIfcPropertyRemove'
        }
    },

    onIfcPropertyChange: function (groupId, object, data) {
        var view = this.getView(),
            formitems = [],
            nameFieldset,
            nameFieldsetItems = [];

        // base field information
        var formfield = CMDBuildUI.util.helper.FormHelper.fieldDefaults;

        if (view.disabled) {
            view.enable();
        }

        if (!Ext.isEmpty(object.getName())) {
            nameFieldsetItems.push(Ext.mergeIf({
                xtype: 'displayfield',
                value: object.getName(),
                fieldLabel: CMDBuildUI.locales.Locales.bim.ifcproperties.name
            }, formfield));
        }

        if (!Ext.isEmpty(object.getType())) {
            nameFieldsetItems.push(Ext.mergeIf({
                xtype: 'displayfield',
                value: object.getType(),
                fieldLabel: CMDBuildUI.locales.Locales.bim.ifcproperties.type
            }, formfield));
        }

        if (!Ext.isEmpty(object.getDescription())) {
            nameFieldsetItems.push(Ext.mergeIf({
                xtype: 'displayfield',
                value: object.getDescription(),
                fieldLabel: CMDBuildUI.locales.Locales.bim.ifcproperties.globalid
            }, formfield));
        }

        if (!Ext.isEmpty(object.getGlobalId())) {
            nameFieldsetItems.push(Ext.mergeIf({
                xtype: 'displayfield',
                value: object.getGlobalId(),
                fieldLabel: CMDBuildUI.locales.Locales.bim.ifcproperties.guid
            }, formfield));
        }

        if (!Ext.isEmpty(object.object._u)) {
            nameFieldsetItems.push(Ext.mergeIf({
                xtype: 'displayfield',
                value: object.object._u,
                fieldLabel: CMDBuildUI.locales.Locales.bim.ifcproperties.uuid
            }, formfield));
        }

        nameFieldset = {
            xtype: 'formpaginationfieldset',
            items: nameFieldsetItems,
            title: CMDBuildUI.locales.Locales.common.attributes.nogroup,
            collapsible: true,
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding
        };
        //adds fieldset
        formitems.push(nameFieldset);

        var calculatedFieldset,
            calculatedFieldsetitems = [];
        for (var item in data) {
            calculatedFieldsetitems.push(Ext.mergeIf({
                xtype: 'displayfield',
                value: data[item],
                fieldLabel: CMDBuildUI.locales.Locales.bim.ifcproperties[item]
            }, formfield));
        }
        calculatedFieldset = {
            xtype: 'formpaginationfieldset',
            items: calculatedFieldsetitems,
            title: CMDBuildUI.locales.Locales.bim.ifcproperties.calculated,
            collapsible: true,
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding
        };
        //adds fieldset
        formitems.push(calculatedFieldset);

        view.removeAll();
        view.add(formitems);
    },

    onIfcPropertyRemove: function () {
        var view = this.getView();
        if (!view.disabled) {
            view.disable();
        }

        // view.removeAll();
    }

});
