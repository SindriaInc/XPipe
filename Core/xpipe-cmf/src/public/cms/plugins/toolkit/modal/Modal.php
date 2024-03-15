<?php

namespace Sindria\Toolkit\Modal;

use WP_List_Table;

abstract class Modal extends WP_List_Table
{
    /**
     * @var string
     */
    public string $id;

    /**
     * @var string
     */
    public string $button;

    /**
     * @var string
     */
    public string $entityId;

    /**
     * @var string
     */
    public string $action;

    /**
     * @var string
     */
    public string $method;

    /**
     * Form constructor
     *
     * @param $id
     * @param $button
     * @param $entityId
     * @param $action
     * @param $method
     * @param $args
     */
    public function __construct($id, $button, $entityId, $action, $method, $args = array())
    {
        // Modal
        $this->id = $id;
        $this->button = $button;
        $this->entityId = $entityId;

        // Modal Form
        $this->action = $action;
        $this->method = $method;

        parent::__construct($args);
    }

    /**
     * Displays the form.
     *
     * @override
     * @since 3.1.0
     */
    public function display()
    {
        $this->display_tablenav( 'top' );
        $this->renderModal();
        $this->display_tablenav( 'bottom' );
    }

    /**
     * Default modal example - needs to be overridden in modal subclasses
     *
     * @return void
     */
    protected function makeModal()
    {
        ?>

        <!-- The Modal -->
        <div id="<?= $this->id ?>" class="modal">

            <!-- Modal content -->
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="<?= $this->id . "-label" ?>"><?= trans('global.modals.warning.title') ?></h5>
                    <span id="<?= $this->id . "-close" ?>" class="close">&times;</span>
                </div>
                <div class="modal-body">
                    <?= trans('global.modals.warning.p1') ?> <br /><?= trans('global.modals.warning.p2') ?><br /><br /><?= trans('global.modals.warning.p3') ?>
                </div>
                <div class="modal-footer">
                    <form id="<?= $this->id . "-form" ?>" name="warning-form" action="<?= $this->action ?>" method="<?= $this->method ?>">
                        <?= csrf_field() ?>
                        <input name="action" type="hidden" value="<?= $this->action ?>">
                        <input name="id" type="hidden" value="<?= $this->entityId ?>">

                        <button id="<?= $this->id . "-submit" ?>" type="submit" data-dismiss="" value="" class="button button-primary"><?= trans('global.button.apply') ?></button>
                        <button id="<?= $this->id . "-cancel" ?>" class="cancel button button-secondary" type="button" data-dismiss="modal"><?= trans('global.button.cancel') ?></button>
                    </form>
                </div>
            </div>

        </div>

        <?php
    }

    /**
     * Render entire modal with style and script blocks
     *
     * @return void
     */
    private function renderModal()
    {
        ?>

        <?php $this->makeModal(); ?>

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
                var modal = document.getElementById("<?= $this->id ?>");

                // Get the button that opens the modal
                var btn = document.getElementById("<?= $this->button ?>");

                // Get the <span> element that closes the modal
                //var span = document.getElementsByClassName("close")[0];
                var span = document.getElementById("<?= $this->id ?>" + "-close");

                // Get the <button> element that closes the modal
                //var cancel = document.getElementsByClassName("cancel")[0];
                var cancel = document.getElementById("<?= $this->id ?>" + "-cancel");

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


        <?php
    }



}
