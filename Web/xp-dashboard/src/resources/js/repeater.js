/*
 * AC Form Field repeater 1.0 fork by Sindria Inc.
 *
 * Copyright 2018
 */

;(function( $ ){
    //Check for elements that need to repeat
    var element = $('[data-ac-repeater]');

    if (element.length > 0){

        //There may be more than one element that needs to repeat
        //so for each element
        element.each(function (i) {

            var elementToRepeat = $(this);
            var elements = [];
            elements.push($(this));
            var buttonsPosition =  elementToRepeat.attr('data-ac-buttons-position') ? elementToRepeat.attr('data-ac-buttons-position') : 'prepend';

            var repeaterText = '<i class="fa fa-fw fa-plus"></i>';
            var derepeaterText = '<i class="fa fa-fw fa-minus"></i>';
            var adderId = 'acAdder' + i;
            var removerId = 'acRemover' + i;
            var adder = '<button type="button" style="height: 45px; margin: 28px 0px;" class="btn btn-primary plus-button" id="'+adderId+'">'+repeaterText+'</button>';

            var settings = {
                'cloneCount' : 1,
                'elements' : elements,
                'buttonsPosition' : buttonsPosition
            };

            //Get the current element from the repeater object and clone it
            var clone = elementToRepeat.clone();

            //Append the adder markup to the element in the document
            if (settings.buttonsPosition !== 'before'){
                elementToRepeat.append(adder);
            }else {
                elementToRepeat.prepend(adder);
            }

            $(document).on('click', '#'+adderId, function () {

                var status = $.session.get('status');

                if (status === 'ko') {
                    $.session.remove('status');
                    return;
                } else {
                    var newElement = clone.clone();

                    //Add the cloned element after the element that was cloned
                    settings.elements.slice(-1)[0].after(newElement);

                    var remover = '<button type="button" style="height: 45px; margin: 28px 0px;" class="btn btn-danger minus-button" id="'+removerId+'" data-ac-remover-id="'+settings.cloneCount+'">'+derepeaterText+'</button>';
                    if (settings.buttonsPosition !== 'before'){
                        newElement.append(remover);
                    }else {
                        newElement.prepend(remover);
                    }

                    //Update the clone count
                    settings.cloneCount = settings.cloneCount + 1;

                    //Reset the updated clone
                    settings.elements.push(newElement);
                }


            });

            $(document).on('click', '#'+removerId, function () {
                var index = $(this).attr('data-ac-remover-id');

                settings.elements[index].remove();
                settings.elements.splice(index, 1);

                settings.elements.forEach(function (element, i) {
                    element.find('#'+removerId).attr('data-ac-remover-id', i);
                });

                //Update the clone count
                settings.cloneCount = settings.cloneCount - 1;
            });

        });

    }
})( window.jQuery);
