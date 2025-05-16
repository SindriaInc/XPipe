/**
 * @file CMDBuildUI.util.Notifier
 * @module CMDBuildUI.util.Notifier
 * @author Tecnoteca srl 
 * @access public
 */
Ext.define("CMDBuildUI.util.Notifier", {
    singleton: true,

    /**
     * @private
     * 
     * @property {Boolean} disabled If true for disable showMessage. Currently needed by tests.
     */
    disabled: false,

    /**
     * Available toast icons.
     * 
     * @constant {Object} icons
     * @property {String} error
     * @property {String} info
     * @property {String} success
     * @property {String} warning
     */
    icons: {
        error: 'fa-times-circle',
        info: 'fa-info-circle',
        success: 'fa-check-circle',
        warning: 'fa-exclamation-circle'
    },

    /**
     * Show toast message.
     * 
     * @param {String} message The content of the message box.
     * @param {Object} [options] Message configuration.
     * @param {String} [options.title] The title of the message box.
     * @param {String} [options.icon] The icont to display in message box header. One of {@link CMDBuildUI.util.Notifier#icons CMDBuildUI.util.Notifier.icons} properties or FontAwsome css class.
     * @param {String} [options.ui="managemenent"] Specify the ui for message box.
     * @param {String} [options.details] An optional text with more details.
     * 
     */
    showMessage: function (message, options) {
        if (message && !CMDBuildUI.util.Notifier.disabled) {
            var w;
            options = Ext.applyIf(options, {
                title: CMDBuildUI.locales.Locales.notifier.info,
                icon: this.icons.info,
                ui: 'default'
            });
            var config = {
                title: Ext.String.format('<span data-testid="message-window-title">{0}</span>', options.title),
                iconCls: 'x-fa ' + options.icon,
                html: Ext.String.format('<span data-testid="message-window-text">{0}</span>', message),
                width: 200,
                align: 'br',
                ui: options.ui,
                alwaysOnTop: 9999,
                autoEl: {
                    'data-testid': 'message-window'
                }
            };

            if (options.icon === this.icons.error) {
                config.autoClose = false;
                config.closable = true;
            }

            if (options.details) {
                config.bbar = [{
                    xtype: 'component',
                    flex: 1
                }, {
                    xtype: 'tool',
                    type: 'help',
                    callback: function (owner, tool, event) {
                        CMDBuildUI.util.Utilities.openPopup(
                            null,
                            options.title, {
                                xtype: 'panel',
                                cls: 'x-selectable',
                                html: CMDBuildUI.util.helper.FieldsHelper.renderTextField(options.details),
                                scrollable: true,
                                bodyPadding: 10
                            },
                            null, {
                                width: '50%',
                                height: '50%'
                            }
                        );
                        w.close();
                    }
                }];
            }

            w = Ext.create('Ext.window.Toast', config);
            w.show();
        }
    },

    /**
     * Show success message.
     * 
     * @param {String} message The content of the message box.
     * @param {String} [code] A code for this message.
     * @param {String} [ui="management"] Specify the ui for message box.
     * 
     */
    showSuccessMessage: function (message, code, ui, details) {
        var fullmessage = message;
        if (code) {
            fullmessage = Ext.String.format("{0} - {1}", code, message);
        }
        CMDBuildUI.util.Notifier.showMessage(fullmessage, {
            title: CMDBuildUI.locales.Locales.notifier.success,
            icon: CMDBuildUI.util.Notifier.icons.success,
            ui: ui,
            details: details
        });
        CMDBuildUI.util.Logger.log(message, CMDBuildUI.util.Logger.levels.log, code);
    },

    /**
     * Show info message.
     * 
     * @param {String} message The content of the message box.
     * @param {String} [code] A code for this message.
     * @param {String} [ui="management"] Specify the ui for message box.
     * 
     */
    showInfoMessage: function (message, code, ui, details) {
        var fullmessage = message;
        if (code) {
            fullmessage = Ext.String.format("{0} - {1}", code, message);
        }
        CMDBuildUI.util.Notifier.showMessage(fullmessage, {
            title: CMDBuildUI.locales.Locales.notifier.info,
            icon: CMDBuildUI.util.Notifier.icons.info,
            ui: ui,
            details: details
        });
        CMDBuildUI.util.Logger.log(message, CMDBuildUI.util.Logger.levels.log, code);
    },

    /**
     * Show warning message.
     * 
     * @param {String} message The content of the message box.
     * @param {String} [code] A code for this message.
     * @param {String} [ui="management"] Specify the ui for message box.
     * 
     */
    showWarningMessage: function (message, code, ui, details) {
        var fullmessage = message;
        if (code) {
            fullmessage = Ext.String.format("{0} - {1}", code, message);
        }
        CMDBuildUI.util.Notifier.showMessage(fullmessage, {
            title: CMDBuildUI.locales.Locales.notifier.warning,
            icons: CMDBuildUI.util.Notifier.icons.warning,
            ui: ui,
            details: details
        });
        CMDBuildUI.util.Logger.log(message, CMDBuildUI.util.Logger.levels.warn, code);
    },

    /**
     * Show error message.
     * 
     * @param {String} message The content of the message box.
     * @param {String} [code] A code for this message.
     * @param {String} [ui="management"] Specify the ui for message box.
     * @param {String} [details] An optional text with more details.
     * 
     */
    showErrorMessage: function (message, code, ui, details) {
        var fullmessage = message;
        if (code) {
            fullmessage = Ext.String.format("{0} - {1}", code, message);
        }
        CMDBuildUI.util.Notifier.showMessage(fullmessage, {
            title: CMDBuildUI.locales.Locales.notifier.error,
            icon: CMDBuildUI.util.Notifier.icons.error,
            ui: ui,
            details: details
        });
        CMDBuildUI.util.Logger.log(message, CMDBuildUI.util.Logger.levels.error, code);
        if (details) {
            CMDBuildUI.util.Logger.log(details, CMDBuildUI.util.Logger.levels.error);
        }
    },

    /**
     * Close all opened messages.
     * 
     */
    closeAll: function () {
        while (Ext.WindowManager.getActive() && Ext.WindowManager.getActive().xtype === "toast") {
            Ext.WindowManager.getActive().destroy();
        }
    }
});