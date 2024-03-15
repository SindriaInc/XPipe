<?php

/**
 * @var \Sindria\DashboardNotifications\ViewModel\IndexViewModel $viewModel
 */
$viewModel = \Sindria\DashboardNotifications\ViewModel\IndexViewModel::getInstance();

?>


<div class="wrap">

    <h1>Notifications</h1>


    <span id="void-result-message" class="void-result-message"></span>

    <div class="table-responsive">
        <table id="results-table" class="table table-hover table-light">
            <thead>

            </thead>

            <tbody id="results-table-body">

            </tbody>
        </table>
    </div>




</div>


<style>


    /* Table Responsive - bootstrap classes */

    @media (max-width: 575.98px) {
        .table-responsive-sm {
            display: block;
            width: 100%;
            overflow-x: auto;
            -webkit-overflow-scrolling: touch;
        }
        .table-responsive-sm > .table-bordered {
            border: 0;
        }
    }
    @media (max-width: 767.98px) {
        .table-responsive-md {
            display: block;
            width: 100%;
            overflow-x: auto;
            -webkit-overflow-scrolling: touch;
        }
        .table-responsive-md > .table-bordered {
            border: 0;
        }
    }
    @media (max-width: 991.98px) {
        .table-responsive-lg {
            display: block;
            width: 100%;
            overflow-x: auto;
            -webkit-overflow-scrolling: touch;
        }
        .table-responsive-lg > .table-bordered {
            border: 0;
        }
    }
    @media (max-width: 1199.98px) {
        .table-responsive-xl {
            display: block;
            width: 100%;
            overflow-x: auto;
            -webkit-overflow-scrolling: touch;
        }
        .table-responsive-xl > .table-bordered {
            border: 0;
        }
    }
    .table-responsive {
        display: block;
        width: 100%;
        overflow-x: auto;
        -webkit-overflow-scrolling: touch;
    }
    .table-responsive > .table-bordered {
        border: 0;
    }

    /* Table - bootstrap classes */

    .table {
        width: 100%;
        margin-bottom: 1rem;
        color: #212529;
    }
    .table th,
    .table td {
        padding: 0.75rem;
        vertical-align: top;
        border-top: 1px solid #dee2e6;
    }
    .table thead th {
        vertical-align: bottom;
        border-bottom: 2px solid #dee2e6;
    }
    .table tbody + tbody {
        border-top: 2px solid #dee2e6;
    }


    /* Table Hover - bootstrap classes */

    .table-hover tbody tr:hover {
        color: #212529;
        background-color: rgba(0, 0, 0, 0.075);
    }

    .table-hover .table-primary:hover {
        background-color: #b0d4f1;
    }
    .table-hover .table-primary:hover > td,
    .table-hover .table-primary:hover > th {
        background-color: #b0d4f1;
    }

    .table-hover .table-secondary:hover {
        background-color: #c8cbcf;
    }
    .table-hover .table-secondary:hover > td,
    .table-hover .table-secondary:hover > th {
        background-color: #c8cbcf;
    }

    .table-hover .table-success:hover {
        background-color: #b3e8ca;
    }
    .table-hover .table-success:hover > td,
    .table-hover .table-success:hover > th {
        background-color: #b3e8ca;
    }

    .table-hover .table-info:hover {
        background-color: #c0ddf6;
    }
    .table-hover .table-info:hover > td,
    .table-hover .table-info:hover > th {
        background-color: #c0ddf6;
    }

    .table-hover .table-warning:hover {
        background-color: #fff8b3;
    }
    .table-hover .table-warning:hover > td,
    .table-hover .table-warning:hover > th {
        background-color: #fff8b3;
    }

    .table-hover .table-danger:hover {
        background-color: #f4b0af;
    }
    .table-hover .table-danger:hover > td,
    .table-hover .table-danger:hover > th {
        background-color: #f4b0af;
    }

    .table-hover .table-light:hover {
        background-color: #ececf6;
    }
    .table-hover .table-light:hover > td,
    .table-hover .table-light:hover > th {
        background-color: #ececf6;
    }

    .table-hover .table-dark:hover {
        background-color: #b9bbbe;
    }
    .table-hover .table-dark:hover > td,
    .table-hover .table-dark:hover > th {
        background-color: #b9bbbe;
    }

    .table-hover .table-active:hover {
        background-color: rgba(0, 0, 0, 0.075);
    }
    .table-hover .table-active:hover > td,
    .table-hover .table-active:hover > th {
        background-color: rgba(0, 0, 0, 0.075);
    }

    .table-dark.table-hover tbody tr:hover {
        color: #fff;
        background-color: rgba(255, 255, 255, 0.075);
    }


    /* Table Light - bootstrap classes */

    .table-light,
    .table-light > th,
    .table-light > td {
        background-color: #fdfdfe;
    }
    .table-light th,
    .table-light td,
    .table-light thead th,
    .table-light tbody + tbody {
        border-color: #fbfcfc;
    }

    .table-hover .table-light:hover {
        background-color: #ececf6;
    }
    .table-hover .table-light:hover > td,
    .table-hover .table-light:hover > th {
        background-color: #ececf6;
    }


    /* Alert - bootstrap classes */


    .alert {
        position: relative;
        padding: 0.75rem 1.25rem;
        margin-bottom: 1rem;
        border: 1px solid transparent;
        border-radius: 0.25rem;
    }


    .alert-heading {
        color: inherit;
    }

    .alert-link {
        font-weight: 700;
    }

    .alert-dismissible {
        padding-right: 3.85rem;
    }
    .alert-dismissible .close {
        position: absolute;
        top: 0;
        right: 0;
        z-index: 2;
        padding: 0.75rem 1.25rem;
        color: inherit;
    }

    .alert-primary {
        color: #1b4b72;
        background-color: #d6e9f8;
        border-color: #c6e0f5;
    }
    .alert-primary hr {
        border-top-color: #b0d4f1;
    }
    .alert-primary .alert-link {
        color: #113049;
    }

    .alert-secondary {
        color: #383d41;
        background-color: #e2e3e5;
        border-color: #d6d8db;
    }
    .alert-secondary hr {
        border-top-color: #c8cbcf;
    }
    .alert-secondary .alert-link {
        color: #202326;
    }

    .alert-success {
        color: #1d643b;
        background-color: #d7f3e3;
        border-color: #c7eed8;
    }
    .alert-success hr {
        border-top-color: #b3e8ca;
    }
    .alert-success .alert-link {
        color: #123c24;
    }

    .alert-info {
        color: #385d7a;
        background-color: #e2f0fb;
        border-color: #d6e9f9;
    }
    .alert-info hr {
        border-top-color: #c0ddf6;
    }
    .alert-info .alert-link {
        color: #284257;
    }

    .alert-warning {
        color: #857b26;
        background-color: #fffbdb;
        border-color: #fffacc;
    }
    .alert-warning hr {
        border-top-color: #fff8b3;
    }
    .alert-warning .alert-link {
        color: #5d561b;
    }

    .alert-danger {
        color: #761b18;
        background-color: #f9d6d5;
        border-color: #f7c6c5;
    }
    .alert-danger hr {
        border-top-color: #f4b0af;
    }
    .alert-danger .alert-link {
        color: #4c110f;
    }

    .alert-light {
        color: #818182;
        background-color: #fefefe;
        border-color: #fdfdfe;
    }
    .alert-light hr {
        border-top-color: #ececf6;
    }
    .alert-light .alert-link {
        color: #686868;
    }

    .alert-dark {
        color: #1b1e21;
        background-color: #d6d8d9;
        border-color: #c6c8ca;
    }
    .alert-dark hr {
        border-top-color: #b9bbbe;
    }
    .alert-dark .alert-link {
        color: #040505;
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



    function markAsReadRequest(id) {
        var xmlhttp = new XMLHttpRequest();

        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == XMLHttpRequest.DONE) { // XMLHttpRequest.DONE == 4
                if (xmlhttp.status == 201) {

                    var json = JSON.parse(xmlhttp.responseText);
                    console.log(json);

                    //document.getElementById("myDiv").innerHTML = xmlhttp.responseText;
                }
                else if (xmlhttp.status == 400) {
                    console.log(xmlhttp.responseText);
                    //alert('There was an error 400');
                }
                else {
                    console.log(xmlhttp.responseText);
                    //alert('something else other than 200 was returned');
                }
            }
        };

        xmlhttp.open("GET", "<?= $viewModel->url . '/markasread?id=' ?>" + id, true);
        xmlhttp.send();
    }


    /**
    * Make ajax request with GET method and generate tbody content
    *
    * @param filter
    */
    function getNotificationsRequest(filter) {
        var xmlhttp = new XMLHttpRequest();

        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == XMLHttpRequest.DONE) { // XMLHttpRequest.DONE == 4
                if (xmlhttp.status == 200) {

                    var json = JSON.parse(xmlhttp.responseText);

                    // reset table body content
                    document.getElementById("results-table").getElementsByTagName('tbody')[0].innerHTML = '';

                    if (json.data.notifications.length === 0) {
                        // No notifications found message
                        let span = document.getElementById('void-result-message');
                        span.textContent = "No notifications found";

                    } else  {

                        // TODO: convert to vanilla
                        //$('#results_table').show();

                        // reset span no notifications found message text
                        let span = document.getElementById('void-result-message');
                        span.textContent = "";

                        let notifications = json.data.notifications;

                        for (var notification of notifications) {

                            var action_id = notification.id;

                            var action = '<td><button id="'+action_id+'" type="button" class="button button-secondary" name="read">Mark as read</button></td>';

                            var message = notification.data.message;
                            var type = notification.data.type;

                            var content = '';

                            if (type === "danger") {
                                content = '<td class="notification-message"><div class="alert alert-danger" role="alert"><em>' + message + '</em></div></td>';
                            }

                            if (type === "warning") {
                                content = '<td class="notification-message"><div class="alert alert-warning" role="alert"><em>' + message + '</em></div></td>';
                            }

                            if (type === "info") {
                                content = '<td class="notification-message"><div class="alert alert-info" role="alert"><em>' + message + '</em></div></td>';
                            }

                            // Insert new row into tbody
                            var tableRef = document.getElementById('results-table').getElementsByTagName('tbody')[0];
                            var newRow = tableRef.insertRow(tableRef.rows.length);
                            newRow.innerHTML = content + action;

                            // Click event
                            var anchor = document.getElementById(action_id);
                            //console.log(anchor);

                            if (anchor.addEventListener) {
                                anchor.addEventListener('click', clickHandler, false);
                            } else if (anchor.attachEvent) { // this is for IE, because it doesn't support addEventListener
                                anchor.attachEvent('onclick', function() {
                                    return clickHandler.apply(anchor, [window.event])
                                }); // this strange part for making the keyword 'this' indicate the clicked anchor
                            }

                        }

                    }
                }
                else if (xmlhttp.status == 400) {
                    console.log(xmlhttp.responseText);
                    //alert('There was an error 400');
                }
                else {
                    console.log(xmlhttp.responseText);
                    //alert('something else other than 200 was returned');
                }
            }
        };

        setInterval(()=>{
            xmlhttp.open("GET", "<?= $viewModel->url . '?filter=' ?>" + filter, true);
            xmlhttp.send();
        },1000)

    }


    function clickHandler(event) {
        let id = event.target.id;
        markAsReadRequest(id);
    }



    /**
     * Execute
     */
    ready(function() {

        getNotificationsRequest("unread");

    });


</script>
