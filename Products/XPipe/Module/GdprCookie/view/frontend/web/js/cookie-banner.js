define(['jquery'], function ($) {
    'use strict';

    return {
        init: function () {
            if (!localStorage.getItem('gdpr_cookie_accepted')) {
                $('#gdpr-cookie-banner').show();
            }

            $('#gdpr-cookie-accept').on('click', function () {
                localStorage.setItem('gdpr_cookie_accepted', 'true');
                $('#gdpr-cookie-banner').fadeOut();
            });
        }
    };
});
