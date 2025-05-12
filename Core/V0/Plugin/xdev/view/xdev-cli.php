<?php

/**
 * @var \Sindria\Xdev\ViewModel\CliViewModel $viewModel
 */
$viewModel = \Sindria\Xdev\ViewModel\CliViewModel::getInstance();

?>


<div class="wrap">


    <div class="tab-wrap">

        <!-- active tab on page load gets checked attribute -->
        <input style="display: none;" type="radio" id="tab1" name="tabGroup1" class="tab" checked>
        <label for="tab1">luca.pitzoi@sindria.org@xdev-cli#session1 <span class="close">&times;</span></label>
        <button id="plus-button" class="tab plus-button button button-secondary">+</button>
<!--        <button onclick="addNewTab()" id="plus-button" class="tab plus-button button button-secondary">+</button>-->

<!--        <input style="display: none;" type="radio" id="tab2" name="tabGroup1" class="tab">-->
<!--        <label for="tab2">luca.pitzoi@sindria.org@xdev-cli#session2 <span class="close">&times;</span></label>-->
<!--        <button class="tab plus-button button button-secondary">+</button>-->

<!--        <input style="display: none;" type="radio" id="tab3" name="tabGroup1" class="tab">-->
<!--        <label for="tab3">luca.pitzoi@sindria.org@xdev-cli#session3 <span class="close">&times;</span></label>-->
<!--        <button class="tab plus-button button button-secondary">+</button>-->

        <div class="tab__content">
            <!-- 1:1 aspect ratio -->
            <div class="embed-responsive embed-responsive-21by9">
                <iframe class="embed-responsive-item" frameborder="0" src="<?= $viewModel->url ?>"></iframe>
            </div>
        </div>

<!--        <div class="tab__content">-->
<!---->
<!--        </div>-->


<!--        <div class="tab__content">-->
<!---->
<!--        </div>-->

    </div>





</div>


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


    function openTerminalSessionRequest(hostname, port, username, password) {
        var xmlhttp = new XMLHttpRequest();

        var data = new FormData();
        data.append('hostname', hostname);
        data.append('port', port);
        data.append('username', username);
        data.append('password', password);


        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == XMLHttpRequest.DONE) { // XMLHttpRequest.DONE == 4
                if (xmlhttp.status == 200) {

                    console.log(xmlhttp.responseText);

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

        xmlhttp.open("POST", "<?= $viewModel->url  ?>", true);
        xmlhttp.send(data);
    }



    function addNewTab() {
        //let test = document.getElementById("plus-button");
        console.log("test");
    }



    /**
     * Execute
     */
    ready(function() {

        //openTerminalSessionRequest('x.x.x.x', 22, 'sindria', 'sindria');

        const btn = document.getElementById("plus-button");

        btn.addEventListener("click", function () {
            console.log("eya");
        });






    });


</script>



<style>

    /* Embed - bootstrap classes */

    .embed-responsive {
        position: relative;
        display: block;
        width: 100%;
        padding: 0;
        overflow: hidden;
    }
    .embed-responsive::before {
        display: block;
        content: "";
    }
    .embed-responsive .embed-responsive-item,
    .embed-responsive iframe,
    .embed-responsive embed,
    .embed-responsive object,
    .embed-responsive video {
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border: 0;
    }

    .embed-responsive-21by9::before {
        padding-top: 42.85714286%;
    }




    .form-container {
        display: none !important;
    }

    label {
        display: inline-block;
        margin-bottom: 0.0rem;
    }
</style>


<style>

    .tab-wrap {
        transition: 0.3s box-shadow ease;
        border-radius: 6px;
        max-width: 100%;
        display: flex;
        /*display: -webkit-box;*/
        flex-wrap: wrap;
        position: relative;
        list-style: none;
        background-color: #fff;
        margin: 40px 0;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);
    }
    .tab-wrap:hover {
        box-shadow: 0 12px 23px rgba(0, 0, 0, 0.23), 0 10px 10px rgba(0, 0, 0, 0.19);
    }

    .tab {
        display: none;
    }
    .tab:checked:nth-of-type(1) ~ .tab__content:nth-of-type(1) {
        opacity: 1;
        transition: 0.5s opacity ease-in, 0.8s transform ease;
        position: relative;
        top: 0;
        z-index: 100;
        transform: translateY(0px);
        text-shadow: 0 0 0;
    }
    .tab:checked:nth-of-type(2) ~ .tab__content:nth-of-type(2) {
        opacity: 1;
        transition: 0.5s opacity ease-in, 0.8s transform ease;
        position: relative;
        top: 0;
        z-index: 100;
        transform: translateY(0px);
        text-shadow: 0 0 0;
    }
    .tab:checked:nth-of-type(3) ~ .tab__content:nth-of-type(3) {
        opacity: 1;
        transition: 0.5s opacity ease-in, 0.8s transform ease;
        position: relative;
        top: 0;
        z-index: 100;
        transform: translateY(0px);
        text-shadow: 0 0 0;
    }
    .tab:checked:nth-of-type(4) ~ .tab__content:nth-of-type(4) {
        opacity: 1;
        transition: 0.5s opacity ease-in, 0.8s transform ease;
        position: relative;
        top: 0;
        z-index: 100;
        transform: translateY(0px);
        text-shadow: 0 0 0;
    }
    .tab:checked:nth-of-type(5) ~ .tab__content:nth-of-type(5) {
        opacity: 1;
        transition: 0.5s opacity ease-in, 0.8s transform ease;
        position: relative;
        top: 0;
        z-index: 100;
        transform: translateY(0px);
        text-shadow: 0 0 0;
    }
    .tab:first-of-type:not(:last-of-type) + label {
        border-top-right-radius: 0;
        border-bottom-right-radius: 0;
    }
    .tab:not(:first-of-type):not(:last-of-type) + label {
        border-radius: 0;
    }
    .tab:last-of-type:not(:first-of-type) + label {
        border-top-left-radius: 0;
        border-bottom-left-radius: 0;
    }
    .tab:checked + label {
        background-color: #fff;
        box-shadow: 0 -1px 0 #fff inset;
        cursor: default;
    }
    .tab:checked + label:hover {
        box-shadow: 0 -1px 0 #fff inset;
        background-color: #fff;
    }
    .tab + label {
        box-shadow: 0 -1px 0 #eee inset;
        border-radius: 6px 6px 0 0;
        cursor: pointer;
        display: block;
        text-decoration: none;
        color: #333;
        flex-grow: 3;
        text-align: center;
        background-color: #f2f2f2;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
        transition: 0.3s background-color ease, 0.3s box-shadow ease;
        height: 50px;
        box-sizing: border-box;
        padding: 15px;
    }
    .tab + label:hover {
        background-color: #f9f9f9;
        box-shadow: 0 1px 0 #f4f4f4 inset;
    }
    .tab__content {
        padding: 10px 25px;
        background-color: transparent;
        position: absolute;
        width: 100%;
        z-index: -1;
        opacity: 0;
        left: 0;
        transform: translateY(-3px);
        border-radius: 6px;
    }

    /** TEST **/


    .plus-button {
        box-shadow: 0 -1px 0 #eee inset;
        border-radius: 6px 6px 0 0;
        cursor: pointer;
        display: block;
        text-decoration: none;
        color: #333;
        flex-grow: 3;
        text-align: center;
        background-color: #f2f2f2;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
        transition: 0.3s background-color ease, 0.3s box-shadow ease;
        height: 50px;
        box-sizing: border-box;
        padding: 15px;
    }

    /* The Close Button */
    .close {
        color: #333;
        float: right;
        font-size: 28px;
        font-weight: bold;
        /*margin-left: 5px;*/
    }

    .close:hover,
    .close:focus {
        color: #000;
        text-decoration: none;
        cursor: pointer;
    }

</style>
