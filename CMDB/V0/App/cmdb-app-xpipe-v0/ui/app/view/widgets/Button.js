Ext.define('CMDBuildUI.view.widgets.Button', {
    extend: 'Ext.button.Button',

    mixins: ['CMDBuildUI.view.widgets.ValidationMixin'],

    alias: 'widget.widgets-button',

    ui: 'widget-button',

    /**
     * @override
     */
    getErrorDomElement: function () {
        return this.el ? this.el.dom : null;
    }
});