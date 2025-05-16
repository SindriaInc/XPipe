Ext.define('CMDBuildUI.components.administration.fapicker.FaPicker', {
    extend: 'Ext.form.field.Picker',

    // Font Awesome version currently used: 5.6.3,

    alias: 'widget.cmdbuild-fapicker',

    editable: false,

    icons: CMDBuildUI.util.helper.IconHelper.icons,

    /**
     * @override
     * 
     * @returns 
     */
    createPicker: function () {
        let me = this,
            fieldSet = [],
            categories = Ext.Array.sort(CMDBuildUI.util.helper.IconHelper.getCategories(), function (a, b) {
                return a[1] < b[1] ? -1 : 1;
            }),
            keyCategories = Ext.Array.pluck(categories, 0);

        let handler = function (button, event) {

            me.setConfig({
                value: button.iconCls
            });

            button.up().up().items.items.forEach(function (fieldset) {
                fieldset.items.items.forEach(function (btn) {
                    if (btn.hasCls('active')) {
                        btn.removeCls('active');
                    }
                });
            });

            button.addCls('active');
        };

        Ext.Object.eachValue(me.icons, function (value) {
            const index = Ext.Array.indexOf(keyCategories, value.category);
            !categories[index][2] ? categories[index][2] = [value] : categories[index][2].push(value);
        });

        Ext.Array.forEach(categories, function (iconsCategory, indexIcon, allIcons) {
            let countItems = 0,
                buttons = [];

            Ext.Array.forEach(iconsCategory[2], function (item, index, allitems) {
                const idIcons = [item["cls_base"], item["cls_regular"], item["cls_solid"]];
                Ext.Array.forEach(Ext.Array.clean(idIcons), function (elem, index, allitems) {
                    let btn = Ext.create('Ext.Button', {
                        ui: 'administration-fapicker-btn',
                        iconCls: elem,
                        margin: '4 4 4 4',
                        match: false,
                        tooltip: Ext.String.format('<b>{0}</b>: {1}<br><b>{2}</b>: {3}',
                            CMDBuildUI.locales.Locales.administration.common.labels.description,
                            item.name,
                            CMDBuildUI.locales.Locales.administration.classes.toolbar.classLabel,
                            elem
                        ),
                        handler: handler
                    });
                    if (me.getValue() === elem) {
                        btn.addCls('active');
                    }
                    buttons.push(btn);
                    countItems++;
                });
            });

            if (countItems > 0) {
                fieldSet.push({
                    xtype: 'fieldset',
                    title: iconsCategory[1] + ' (' + countItems + ')',
                    ui: 'administration-formpagination',
                    layout: 'column',
                    items: buttons
                });
            }
        });

        var picker = new Ext.panel.Panel({
            pickerField: me,
            floating: true,
            hidden: true,
            ownerCt: this.ownerCt,
            renderTo: Ext.bodyEl,
            bodyPadding: 5,
            height: 450,
            autoScroll: true,
            items: fieldSet
        });

        return picker;
    },

    listeners: {
        change: function (picker, newValue, oldValue, eOpts) {
            picker.collapse();
        }
    }
});