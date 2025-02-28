/*
 *  Copyright Sindria Inc.
 *
 *  backoffice.js
 */


/**
 * General backoffice function
 *
 * @param e
 */
$(document).ready(function() {

    // Hide warningModal when submit and show loader
    $('#warningForm').submit(function(event) {
        $('#warningModal').modal('hide');
        $('#loaderModal').modal('show');
    });

    // Hide uploadModal when submit and show loader
    $('#uploadForm').submit(function(event) {
        $('#uploadModal').modal('hide');
        $('#loaderTopModal').modal('show');
    });

});
