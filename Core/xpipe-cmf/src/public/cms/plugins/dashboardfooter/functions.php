<?php

if (!function_exists('env')) {
    /**
     * Get env variable with default fallback
     *
     * @param $key string
     * @param $default string
     */
    function env(string $key, string $default = '')
    {
        $env = getenv($key);

        if (! $env) {
            $env = $default;
        }

        return $env;
    }
}

function dashboardfooter_links() {

    ?>

    <style>

        .footer-links ul {
            display: inline-block;
        }

        .footer-links ul li {
            display: inline-block;
        }

        .footer-links ul li a {
            color:inherit;
            text-decoration: none;
        }

        .footer-links ul li:before {
            content: ' ';
            margin-right: 0.5em;
        }

        .footer-links {
            margin: 0;
            font-size: 13px;
            line-height: 1.55;
            text-align: center;
            /*margin-right: 5px;*/
            /*position: relative;*/
            /*top: 50%;*/
            /*-ms-transform: translateY(+15%);*/
            /*transform: translateY(+15%);*/
            /*float: right;*/
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

        ready(function() {

            let footer = document.querySelector('#wpfooter');

            // Get the DOM nodes
            let links = document.querySelector('#footer-links');
            let left = document.querySelector('#footer-left');
            let right = document.querySelector('#footer-upgrade');
            //let clear = document.querySelector('.clear');


            // Move stuff
            // footer.prepend(right);
            // footer.prepend(links);
            // footer.prepend(left);

            left.append(links);


        });

    </script>


    <?php


    $privacy = env('APP_PRIVACY_POLICY_URL', '#');
    $terms = env('APP_TERMS_URL', '#');
    $cookie = env('APP_COOKIE_URL', '#');
    echo '<span id="footer-links" class="footer-links"> |<ul><li><a href="'.$privacy.'" target="_blank">Privacy</a></li><li><a href="'.$terms.'" target="_blank">Terms</a></li><li><a href="'.$cookie.'" target="_blank">Cookie</a></li></ul></span>';
}
