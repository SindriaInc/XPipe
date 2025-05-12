Ext.define('Override.form.field.Text', {
    override: 'Ext.form.field.Text',

    onFocus: function (e) {
        var me = this;
        me.callSuper([e]);

        // This handler may be called when the focus has already shifted to another element; 
        // calling inputEl.select() will forcibly focus again it which in turn might set up 
        // a nasty circular race condition if focusEl !== inputEl. 
        Ext.asap(function () {
            // This ensures the carret will be at the end of the input element 
            // while tabbing between editors. 
            if (!me.destroyed && document.activeElement === me.inputEl.dom && me.selectOnFocus) {
                me.selectText(0, me.inputEl.dom.value.length);
            }
        });

        if (me.emptyText) {
            me.autoSize();
        }

        me.addCls(me.fieldFocusCls);
        me.triggerWrap.addCls(me.triggerWrapFocusCls);
        me.inputWrap.addCls(me.inputWrapFocusCls);
        me.invokeTriggers('onFieldFocus', [e]);
    },

    onBlur: function (e) {
        var me = this;

        // if the field cannot be empty, we need to trim the value otherwise, if the value 
        // contains only spaces, it is considered valid.
        if (me.allowBlank === false) {
            var value = me.getValue();
            if (value && Ext.isString(value)) {
                me.setValue(value.trim());
            }
        }

        me.callParent(e);
    }
});