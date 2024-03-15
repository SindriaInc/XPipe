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

import Cookies from 'js-cookie';


// Execute
$(document).ready(function() {


    // Sidebar toggle
    $("#sidenavToggler").click(function (e) {
        e.preventDefault();

        if (Cookies.get('toggled') === 'null') {
            Cookies.set('toggled', false);
        }

        $("body").toggleClass("sidenav-toggled");

        if (Cookies.get('toggled') === 'true') {
            Cookies.set('toggled', false);
            $.post( "/toggle", { value:  Cookies.get('toggled') } );
        } else if (Cookies.get('toggled') === 'false') {
            Cookies.set('toggled', true);
            $.post( "/toggle", { value:  Cookies.get('toggled') } );
        } else {
            Cookies.set('toggled', null);
            $.post( "/toggle", { value:  Cookies.get('toggled') } );
        }


        $(".navbar-sidenav .nav-link-collapse").addClass("collapsed");
        $(".navbar-sidenav .sidenav-second-level, .navbar-sidenav .sidenav-third-level").removeClass("show");
    });



    // Sidebar toggle
    $(".navbar-sidenav .nav-link-collapse").click(function(e) {
        e.preventDefault();
        $("body").removeClass("sidenav-toggled")
    });


    // Scroll to top
    $("body.fixed-nav .navbar-sidenav, body.fixed-nav .sidenav-toggler, body.fixed-nav .navbar-collapse").on("mousewheel DOMMouseScroll", function(e) {
       var o = e.originalEvent, a = o.wheelDelta || -o.detail;
       this.scrollTop += 30 * (a < 0 ? 1 : -1), e.preventDefault()
    });

    // Scroll to top
    $(document).scroll(function() {
        $(this).scrollTop() > 100 ? $(".scroll-to-top").fadeIn() : $(".scroll-to-top").fadeOut()
    });


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
