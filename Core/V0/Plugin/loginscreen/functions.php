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

function loginscreen_login_stylesheet() {
    wp_enqueue_style('custom-login', plugin_dir_url( __FILE__ ) . '/static/css/loginscreen.css' );
    wp_enqueue_script('custom-login', plugin_dir_url( __FILE__ ) . '/static/js/loginscreen.js' );
}

function loginscreen_background() {
    ?>

    <style>
        /* Background */
        body.login {
            background-image: url(<?= plugin_dir_url( __FILE__ ) . '/static/images/bg.jpg' ?>);
            background-size: cover;
            display: block;
            background-repeat: no-repeat;
            background-color: #fefefe;
        }
    </style>

    <?php
}

function loginscreen_login_title( $login_title ) {
    return str_replace(array( ' &lsaquo;', ' &#8212; WordPress'), array( ' &bull;', ''),$login_title );
}

function loginscreen_always_checked_rememberme() {
    echo "<script>document.getElementById('rememberme').checked = true;</script>";
}

function loginscreen_footer() {
    $privacy = env('APP_PRIVACY_POLICY_URL', '#');
    $terms = env('APP_TERMS_URL', '#');
    $cookie = env('APP_COOKIE_URL', '#');
    echo '<footer><div class="copyright"><p>Copyright &copy;<a style="color:inherit; text-decoration: none;" href="https://sindria.org" target="_blank"> Sindria Inc.</a> ' . date('Y') . '</p></div><div class="footerLinks"><ul><li><a href="'.$privacy.'" target="_blank">Privacy</a></li><li><a href="'.$terms.'" target="_blank">Terms</a></li><li><a href="'.$cookie.'" target="_blank">Cookie</a></li></ul></div></footer>';
}
