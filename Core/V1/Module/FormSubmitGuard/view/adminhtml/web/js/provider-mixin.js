define(['jquery'], function ($) {
    'use strict';

    return function (Component) {
        return Component.extend({
            save: function () {
                if (this._submittedOnce) {
                    console.warn('[Sindria_FormSubmitGuard] Save action blocked to prevent double-submit.');
                    return;
                }

                this._submittedOnce = true;

                setTimeout(() => {
                    this._submittedOnce = false;
                }, 10000);

                return this._super();
            }
        });
    };
});
