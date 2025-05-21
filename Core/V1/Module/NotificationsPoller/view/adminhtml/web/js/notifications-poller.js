define([
    'jquery'
], function ($) {
    'use strict';

    return function (config) {
        const POLLING_INTERVAL = 10000;
        const wrapperSelector = '.notifications-wrapper';
        const linkSelector = '.notifications-action';
        const counterClass = 'notifications-counter';
        const wrapperHighlightClass = 'admin__action-has-new';
        let lastCount = 0;

        console.log('[Core] Notification Poller internal booting...');
        console.log('[Core] Using polling URL:', config.url);

        const updateCounter = function (count) {
            const $wrapper = $(wrapperSelector);
            const $link = $wrapper.find(linkSelector);
            if (!$wrapper.length || !$link.length) return;

            // Update or create counter inside the <a>
            let $counter = $link.find(`.${counterClass}`);
            if (!$counter.length) {
                $counter = $('<span/>', {
                    class: counterClass
                }).appendTo($link);
            }

            if (count > 0) {
                $counter.text(count).show();
                $wrapper.attr('data-notification-count', count);
                $wrapper.addClass(wrapperHighlightClass);
            } else {
                $counter.text('').hide();
                $wrapper.attr('data-notification-count', 0);
                $wrapper.removeClass(wrapperHighlightClass);
            }
        };

        setInterval(function () {
            console.log('[Core] Executing polling...');

            $.ajax({
                url: config.url,
                method: 'GET',
                success: function (response) {
                    const notifications = response.notifications || [];
                    const count = notifications.length;

                    if (count !== lastCount) {
                        lastCount = count;
                        updateCounter(count);
                        console.log('[Core] Updated notification count:', count);
                    }
                },
                error: function (xhr, status, error) {
                    console.error('[Core] Polling error:', status, error);
                }
            });
        }, POLLING_INTERVAL);
    };
});
