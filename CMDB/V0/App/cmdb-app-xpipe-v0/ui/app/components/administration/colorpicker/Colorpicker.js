Ext.define('CMDBuildUI.components.administration.colorpicker.Colorpicker', {
    extend: 'Ext.form.field.Picker',

    alias: 'widget.cmdbuild-colorpicker',
    requires: [
        'Ext.picker.Color'
    ],

    editable: true,
    vtype: 'hexColorValidation',
    config: {
        picker: null,
        displaySlot: "color",
        colors: [
            '000000', '993300', '30373D', '003300', '003366', '000080', '333399', '333333',
            '800000', 'FF6600', '808000', '008000', '008080', '0000FF', '666699', '808080',
            'FF0000', 'FF9900', '99CC00', '339966', '33CCCC', '3366FF', '800080', '969696',
            'FF00FF', 'FFCC00', 'FFFF00', '00FF00', '00FFFF', '00CCFF', '993366', 'C0C0C0',
            'FF99CC', 'FFCC99', 'FFFF99', 'CCFFCC', 'CCFFFF', '99CCFF', 'CC99FF', 'FFFFFF'

        ]
    },

    /**
     * Create the color picker 
     */
    createPicker: function () {
        var me = this, picker;
        if (!me.picker) {
            picker = Ext.create('Ext.picker.Color', {
                value: me.getValue(),
                renderTo: Ext.bodyEl,
                colors: this.getColors(),
                floating: true,
                style: {
                    height: 'auto'
                },
                listeners: {
                    select: {
                        fn: me.onColorPickerChange,
                        scope: me
                    }
                }
            });
            this.setPicker(picker);
        }

        return me.getPicker();
    },

    /**
     * @param {Ext.picker.Color} colorPicker
     * @param {Hex} color
     */
    onColorPickerChange: function (colorPicker, color) {
        this.setValue('#' + color);
        this.collapse();
    },

    /**
     * Collapses this field's picker dropdown.
     * @override
     */
    collapse: function () {
        var me = this;
        if (me.isExpanded && !me.destroyed && !me.destroying) {
            var openCls = me.openCls,
                picker = me.picker,
                aboveSfx = '-above';
            // hide the picker and set isExpanded flag
            picker.hide();
            me.isExpanded = false;
            // remove the openCls
            me.bodyEl.removeCls([
                openCls,
                openCls + aboveSfx
            ]);
            if (picker.el) {
                picker.el.removeCls(picker.baseCls + aboveSfx);
            }
            if (!me.ariaStaticRoles[me.ariaRole]) {
                me.ariaEl.dom.setAttribute('aria-expanded', false);
            }
            // remove event listeners
            me.touchListeners.destroy();
            me.scrollListeners.destroy();
            Ext.un('resize', me.alignPicker, me);
            me.fireEvent('collapse', me);
            me.onCollapse();
        }
    }
});