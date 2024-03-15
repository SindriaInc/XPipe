/**
 * @file CMDBuildUI.util.Msg
 * @module CMDBuildUI.util.Msg
 * @author Tecnoteca srl
 * @access public
 */
Ext.define("CMDBuildUI.util.Msg", {
    singleton: true,

    /**
     * Open confirm message.
     *
     * @param {String} title
     * @param {String} message
     * @param {Function} [callback]
     * @param {Object} [scope]
     *
     */
    confirm: function (title, message, callback, scope) {
        Ext.Msg.closeToolText = CMDBuildUI.locales.Locales.common.actions.close;
        Ext.Msg.preventRefocus = true;
        Ext.Msg.alwaysOnTop = CMDBuildUI.util.Utilities._popupAlwaysOnTop + 1;
        Ext.Msg.autoEl = {
            'data-testid': 'msg-window'
        };
        Ext.Msg.confirm(
            title,
            message,
            callback,
            scope
        );
    },

    /**
     * Open alert message.
     *
     * @param {String} title
     * @param {String} message
     * @param {Function} [callback]
     * @param {Object} [scope]
     *
     */
    alert: function (title, message, callback, scope) {
        Ext.Msg.closeToolText = CMDBuildUI.locales.Locales.common.actions.close;
        Ext.Msg.alwaysOnTop = CMDBuildUI.util.Utilities._popupAlwaysOnTop + 1;
        Ext.Msg.autoEl = {
            'data-testid': 'msg-window'
        };
        Ext.Msg.alert(
            title,
            message,
            callback,
            scope
        );
    },

    /**
     *
     * @param {String} title
     * @param {String} [message]
     * @param {Function} [callback]
     * @param {Object} [scope]
     * @param {Boolean} [multiline]
     * @param {String} [value]
     *
     */
    prompt: function (title, message, callback, scope, multiline, value) {
        Ext.Msg.closeToolText = CMDBuildUI.locales.Locales.common.actions.close;
        Ext.Msg.autoEl = {
            'data-testid': 'prompt-window'
        };
        var _prompt = Ext.Msg.prompt(
            title,
            message,
            callback,
            scope,
            multiline,
            value);

        _prompt.setAlwaysOnTop(Infinity);
    },

    /**
     * Open a dialog.
     *
     * @param {String} title Dialog title.
     * @param {Object} [config] Other configurations to add to the popup. See {@link Ext.window.Window}.
     *
     */
    openDialog: function (title, config) {
        var dialog = Ext.create('Ext.window.Window', Ext.apply({
            title: title,
            width: 400,
            layout: 'fit',
            alwaysOnTop: CMDBuildUI.util.Utilities._popupAlwaysOnTop++,
            closeToolText: CMDBuildUI.locales.Locales.common.actions.close,
            modal: true,
            ui: 'management',
            /**
             * set position within body
             * @param {Numeric} x
             * @param {Numeric} y
             */
            afterSetPosition: function (x, y) {
                var newx, newy;
                var viewsize = Ext.getBody().getViewSize();
                if (x < 0) {
                    newx = "0";
                } else if (x > (viewsize.width - this.getWidth())) {
                    newx = viewsize.width - this.getWidth();
                }
                if (y < 0) {
                    newy = "0";
                } else if (y > (viewsize.height - this.getHeight())) {
                    newy = viewsize.height - this.getHeight();
                }

                if (newx !== undefined || newy !== undefined) {
                    newx = newx || x;
                    newy = newy || y;
                    this.setPosition(newx, newy);
                }
            },

            /**
             *
             * @param {Number} width
             * @param {Number} height
             * @param {Number} oldWidth
             * @param {Number} oldHeight
             */
            onResize: function (width, height, oldWidth, oldHeight) {
                var viewsize = Ext.getBody().getViewSize();
                if (width > viewsize.width) {
                    this.setWidth(viewsize.width);
                }
                if (height > viewsize.height) {
                    this.setHeight(viewsize.height);
                }
            }
        }, config));
        dialog.show();
        return dialog;
    }
});