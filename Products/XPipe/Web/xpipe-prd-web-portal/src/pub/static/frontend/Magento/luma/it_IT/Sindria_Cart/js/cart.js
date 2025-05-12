define([
    'jquery',
    'Magento_Ui/js/lib/view/utils/async'
], function($) {
    'use strict';

    $.async('.content.minicart-items .details-qty', function (el) {     // add an icon to the each item content
        $(el).after(
            '<div class="remove item">' +
            '<a href="#" title="Bacassa Toia Remove Item" class="remove-item">' +
            '<i class="icon-trash"></i>' +
            '</a>' +
            '</div>'
        );

        $(el).siblings('.remove.item').find('.remove-item').click(function (event){
            var item = event.currentTarget;
            $(item).parents(".product-item-details").find("input[type='number']").val(0).change(); // change the quantity to "0"
        });
    });

})
