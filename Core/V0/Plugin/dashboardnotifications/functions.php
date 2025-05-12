<?php

use Illuminate\Http\Request;
use Sindria\DashboardNotifications\Helper;

/**
 * Remove update core dashboard submenu
 */
function dashboardnotifications_remove_update_core() {
    remove_submenu_page('index.php', 'update-core.php');
}


function dashboardnotifications_bell($admin_bar) {

    ?>

    <style>

        #wp-toolbar>ul>li#wp-admin-bar-bell {
            margin: 8px 0px 0px 0px;
        }

        #sindria-notifications-bell {
            border: none;
            background-color: transparent;
            color: #666;
            margin-top: 5px;
        }

        li #wp-admin-bar-bell {
            margin-top: 5px;
        }

        .notification-container {
            position: relative;
            width: 30px;
            height: 30px;
            top: 15px;
            left: 15px;

            i {
                color: #fff;
            }
        }

        .notification-counter {
            position: absolute;
            top: -2px;
            left: 12px;
            width: 30px;
            height: 30px;

            /* Important */
            background-color: rgb(212, 19, 13);
            /* Info */
            /*background-color: rgb(13, 142, 212);*/
            color: #fff;
            border-radius: 3px !important;
            padding: 1px 3px;
            /*font: 8px Verdana;*/
        }

        /* Desktop only */
        @media screen and (min-width: 783px) {

            .notification-container {
                padding: calc( (var(--bar-height) - 32px)/2 ) 10px !important;
            }

        }


        /* Responsive */

        @media screen and (max-width: 782px) {

            #wpadminbar .ab-sub-wrapper, #wpadminbar ul, #wpadminbar ul li {
                background: 0 0;
                clear: none;
                list-style: none;
                margin: 0;
                padding: 0;
                position: relative;
                text-indent: 0;
                z-index: 99999;
            }


            #wp-toolbar>ul>li {
                display: block;
            }

            #wp-toolbar>ul>li#wp-admin-bar-lang {
                display: none;
            }

            #wp-toolbar>ul>li#wp-admin-bar-xdev {
                display: none;
            }

            #wp-toolbar>ul>li#wp-admin-bar-bell {
                margin: 0px 0px 0px 0px;
            }


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

        function setCookie(name,value,days) {
            var expires = "";
            if (days) {
                var date = new Date();
                date.setTime(date.getTime() + (days*24*60*60*1000));
                expires = "; expires=" + date.toUTCString();
            }
            document.cookie = name + "=" + (value || "")  + expires + "; path=/";
        }

        function getCookie(name) {
            var nameEQ = name + "=";
            var ca = document.cookie.split(';');
            for(var i=0;i < ca.length;i++) {
                var c = ca[i];
                while (c.charAt(0)==' ') c = c.substring(1,c.length);
                if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
            }
            return null;
        }

        function eraseCookie(name) {
            document.cookie = name +'=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
        }


        /**
         * Make ajax request with GET method and generate tbody content
         *
         * @param filter
         */
        function getNotificationsCounterRequest(filter) {
            var xmlhttp = new XMLHttpRequest();

            xmlhttp.onreadystatechange = function() {
                if (xmlhttp.readyState == XMLHttpRequest.DONE) { // XMLHttpRequest.DONE == 4
                    if (xmlhttp.status == 200) {

                        var json = JSON.parse(xmlhttp.responseText);
                        var counter = json.data.counter;

                        var counter_registry = getCookie('notifications-counter');

                        // default
                        if (counter_registry == null) {
                            setCookie('notifications-counter', counter, 1);
                        }


                        if (counter > counter_registry) {

                            // Play sound
                            let file = '<?= Helper::getSoundsUrl() . '/task-completed-message-ringtone.mp3' ?>';
                            var audio = new Audio(file);
                            audio.play();

                        }

                        setCookie('notifications-counter', counter, 1);

                        // reset cunter value
                        document.getElementById("notifications-counter").innerHTML = '';

                        if (counter !== 0) {
                            document.getElementById("notifications-counter").innerHTML = counter;
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
                xmlhttp.open("GET", "<?= Helper::getNotificationsUrl() . '?filter=' ?>" + filter, true);
                xmlhttp.send();
            },1000)
        }



        /**
         * Execute
         */
        ready(function() {

            getNotificationsCounterRequest("unread");

        });


    </script>

    <?php

    $url = cms_dashboard_page_route('notifications');

    $html = <<<EOF
        <a class="notification-container" href="{$url}">
        <i id="sindria-notifications-bell" class="dashicons dashicons-before dashicons-bell"></i>
        <span id="notifications-counter" class="notification-counter"></span>
        </a>
        EOF;



    $args = array(
        'id'        => 'bell',
        'title'     =>  $html,
        'parent'    =>  'top-secondary',
        'href'      =>  '',
        'group'     =>  '',
        'meta'      =>  array(
            'html'     =>  '',
            'class'     =>  '',
            'rel'       =>  '',
            'lang'      =>  '',
            'dir'       =>  '',
            'onclick'   =>  '',
            'title'     =>  'Notifications'
        ),
    );
    $admin_bar->add_node($args);

}

/**
 * Load routes from routes.php file into array
 *
 * @return array
 */
function dashboardnotifications_load_routes(): array
{
    return include 'routes.php';
}

/**
 * Micro MVVM router
 *
 * @return mixed
 */
function dashboardnotifications_router()
{
    $routes = dashboardnotifications_load_routes();

    foreach ($routes['routes'] as $key => $value) {

        if (isset($_GET['page'])) {
            if ($_GET['page'] == $key) {
                $controllerAction = explode('@', $value);
                $controllerClass = $controllerAction[0];
                $method = $controllerAction[1];
            }
        }

        if (isset($_POST['page'])) {
            if ($_POST['page'] == $key) {
                $controllerAction = explode('@', $value);
                $controllerClass = $controllerAction[0];
                $method = $controllerAction[1];
            }
        }
    }


    $controller = new $controllerClass(new \Sindria\DashboardNotifications\View());
    return $controller->$method(Request::capture());
}

/**
 * Init Plugin
 *
 * @return void
 */
function dashboardnotifications(): void
{
    if (class_exists('\Sindria\DashboardNotifications\Plugin')) {
        $plugin = new \Sindria\DashboardNotifications\Plugin();
    }
}

