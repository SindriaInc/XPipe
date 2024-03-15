<?php
/**
 * @file warning.php
 */
?>

<!-- Warning Modal-->

<!-- Trigger/Open The Modal -->
<button id="testBtn">Open Modal</button>

<!-- The Modal -->
<div id="warningModal" class="modal">

    <!-- Modal content -->
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title" id="warningModalLabel"><?= trans('global.modals.warning.title') ?></h5>
            <span class="close">&times;</span>
        </div>
        <div class="modal-body">
            <?= trans('global.modals.warning.p1') ?> <br /><?= trans('global.modals.warning.p2') ?><br /><br /><?= trans('global.modals.warning.p3') ?>
        </div>
        <div class="modal-footer">
            <form id="warningForm" action="<?= cms_dashboard_page_route('') ?>" method="get">
                <?= csrf_field() ?>
                <input type="hidden" name="page" value="delete-user">

                <button id="warningSubmit" type="submit" data-dismiss="" value="" class="button button-primary"><?= trans('global.button.apply') ?></button>
                <button class="cancel button button-secondary" type="button" data-dismiss="modal"><?= trans('global.button.cancel') ?></button>
            </form>
        </div>
    </div>

</div>


<style>
    /*body {font-family: Arial, Helvetica, sans-serif;}*/

    /* The Modal (background) */
    .modal {
        display: none; /* Hidden by default */
        position: fixed; /* Stay in place */
        z-index: 1; /* Sit on top */
        padding-top: 100px; /* Location of the box */
        left: 0;
        top: 0;
        width: 100%; /* Full width */
        height: 100%; /* Full height */
        overflow: auto; /* Enable scroll if needed */
        background-color: rgb(0,0,0); /* Fallback color */
        background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
    }

    /* Modal Content */
    .modal-content {
        position: relative;
        background-color: #fefefe;
        margin: auto;
        padding: 0;
        border: 1px solid #888;
        width: 80%;
        box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2),0 6px 20px 0 rgba(0,0,0,0.19);
        -webkit-animation-name: animatetop;
        -webkit-animation-duration: 0.4s;
        animation-name: animatetop;
        animation-duration: 0.4s
    }

    /* Add Animation */
    @-webkit-keyframes animatetop {
        from {top:-300px; opacity:0}
        to {top:0; opacity:1}
    }

    @keyframes animatetop {
        from {top:-300px; opacity:0}
        to {top:0; opacity:1}
    }

    /* The Close Button */
    .close {
        color: #333;
        float: right;
        font-size: 28px;
        font-weight: bold;
    }

    .close:hover,
    .close:focus {
        color: #000;
        text-decoration: none;
        cursor: pointer;
    }

    .modal-header {
        padding: 2px 16px;
        background-color: #fefefe;
        color: #333;
    }

    .modal-body {padding: 2px 16px;}

    .modal-footer {
        padding: 2px 16px;
        background-color: #fefefe;
        color: #333;
    }
</style>

<script>

    function ready(callback) {
        // in case the document is already rendered
        if (document.readyState!='loading') callback();
        // modern browsers
        else if (document.addEventListener) document.addEventListener('DOMContentLoaded', callback);
        // IE <= 8
        else document.attachEvent('onreadystatechange', function() {
                if (document.readyState=='complete') callback();
            });
    }





    /**
     * Execute
     */
    ready(function() {


        // Get the modal
        var modal = document.getElementById("warningModal");

        // Get the button that opens the modal
        var btn = document.getElementById("testBtn");

        // Get the <span> element that closes the modal
        var span = document.getElementsByClassName("close")[0];

        // Get the <button> element that closes the modal
        var cancel = document.getElementsByClassName("cancel")[0];

        // When the user clicks the button, open the modal
        btn.onclick = function() {
            modal.style.display = "block";
        }

        // When the user clicks on <span> (x), close the modal
        span.onclick = function() {
            modal.style.display = "none";
        }

        // When the user clicks on <span> (x), close the modal
        cancel.onclick = function() {
            modal.style.display = "none";
        }

        // When the user clicks anywhere outside of the modal, close it
        window.onclick = function(event) {
            if (event.target == modal) {
                modal.style.display = "none";
            }
        }



    });


</script>
