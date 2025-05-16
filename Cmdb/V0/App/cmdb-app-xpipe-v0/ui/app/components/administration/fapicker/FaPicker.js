Ext.define('CMDBuildUI.components.administration.fapicker.FaPicker', {
    extend: 'Ext.form.field.Picker',

    currentFontAwesomeVersion: 4.2,

    alias: 'widget.cmdbuild-fapicker',
    requires: [
        'CMDBuildUI.components.administration.fapicker.FaPickerIcons'
    ],

    editable: false,

    icons: CMDBuildUI.components.administration.fapicker.FaPickerIcons.icons,




    createPicker: function () {
        var me = this,
            icons = me.icons,
            fieldSet = [];

        var handler = function (button, event) {

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

        icons.forEach(function (icon) {
            var groups = icon.Icons,
                countItems = 0,
                buttons = [];

            groups.forEach(function (group) {
                if (group.created <= me.currentFontAwesomeVersion) {
                    var name = group.name;
                    var id = group.id;

                    var btn = Ext.create('Ext.Button', {
                        ui: 'administration-fapicker-btn',
                        iconCls: id,
                        margin: '4 4 4 4',
                        match: false,
                        tooltip: Ext.String.format('{0}: {1}<br>{2}: {3}',
                            CMDBuildUI.locales.Locales.administration.common.labels.description,
                            name,
                            CMDBuildUI.locales.Locales.administration.classes.toolbar.classLabel,
                            id
                        ),
                        handler: handler
                    });
                    if (me.getValue() === id) {
                        btn.addCls('active');
                    }
                    buttons.push(btn);
                    countItems++;
                }
            });

            if (countItems > 0) {
                fieldSet.push({
                    xtype: 'fieldset',
                    title: icon.name + ' (' + countItems + ')',
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