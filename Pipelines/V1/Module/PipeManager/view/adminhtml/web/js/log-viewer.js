define(['jquery', 'mage/translate', 'uiComponent'], function ($, $t, Component) {
    'use strict';

    return Component.extend({
        initialize: function () {
            this._super();

            const $root   = $('#devops-log-viewer');
            const $body   = $root.find('.dlv-log__body');

            // auto-scroll to bottom on load
            $body.scrollTop($body[0].scrollHeight);

            // simple handlers
            $root.on('click', '[data-action="copy"]', function () {
                navigator.clipboard.writeText($body.text())
                    .then(() => alert($t('Log copied to clipboard')))
                    .catch(() => alert($t('Unable to copy')));
            });

            $root.on('click', '[data-action="clear"]', function () {
                if (confirm($t('Clear the log?'))) {
                    $body.empty();
                }
            });
        }
    });
});
