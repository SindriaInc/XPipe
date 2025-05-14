define(['jquery'], function ($) {
    'use strict';

    return function () {
        $(document).on('submit', 'form[data-mage-init]', function (e) {
            const $form = $(this);
            if ($form.data('submitted')) {
                e.preventDefault();
                console.log("Warning: your browser is trying to submit the form more than one time!");
                return false;
            }
            $form.data('submitted', true);
        });
    };
});
