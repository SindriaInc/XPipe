define([
    'ui/form/element/abstract',
    'ko'
], function (Abstract, ko) {
    'use strict';

    return Abstract.extend({
        defaults: {
            inputType: null,
            tracks: {
                inputType: true
            }
        },

        initialize: function () {
            this._super();

            // Fallback se KO non ha tracciato inputType
            if (typeof this.inputType !== 'function') {
                this.inputType = ko.observable('password');
            } else {
                this.inputType('password');
            }

            return this;
        },

        togglePasswordVisibility: function () {
            this.inputType(
                this.inputType() === 'password' ? 'text' : 'password'
            );
        }
    });
});
