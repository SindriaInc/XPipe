define([
    'ui/form/element/abstract',
    'ko'
], function (Abstract, ko) {
    'use strict';

    return Abstract.extend({
        defaults: {
            inputType: 'password',
            template: 'Core_SecureInput/form/element/secure-input'
        },
        initialize: function () {
            this._super();
            this.inputType = ko.observable('password');
            return this;
        },
        togglePasswordVisibility: function () {
            this.inputType(this.inputType() === 'password' ? 'text' : 'password');
        }
    });
});
