define(['jquery'], function ($) {
    'use strict';

    return function () {
        $(document).on('submit', 'form[data-mage-init]', function (e) {
            const $form = $(this);
            if ($form.data('submitted')) {
                e.preventDefault();
                return false;
            }
            $form.data('submitted', true);
        });
    };
});
