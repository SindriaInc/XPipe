define([
    'jquery',
    'mage/translate',
    'uiComponent'
], function ($, $t, Component) {
    'use strict';
    return Component.extend({
        initialize: function () {
            this._super();
            $('#templatestore-card-grid')
                .on('click', '.app-card button', function () {
                    const app = $(this).closest('.app-card').data('app');
                    alert($t('Install ') + app.title);
                    // Ajax call → POST to your install endpoint
                });
        }
    });
});
