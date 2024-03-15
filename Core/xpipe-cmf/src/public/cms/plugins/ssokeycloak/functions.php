<?php

function ssokeycloak_login_error_messages($message) {

    $reason = isset( $_REQUEST['reason'] ) ? $_REQUEST['reason'] : '';

    $errors = new WP_Error();
    $errors->add('sso_error', '<strong>Error</strong>: Something went wrong with sso login.');
    $errors->add('profile_error', '<strong>Error</strong>: Something went wrong with profile.');
    $errors->add('sso_exception', '<strong>Error</strong>: Fatal error during sso login.');


    switch ( $reason ):
        case 'sso_error':
            $message .= '<p id="login_error" class="message">' . __( $errors->get_error_message($reason), 'text_domain' ) . '</p>';
            break;
        case 'profile_error':
            $message .= '<p id="login_error" class="message">' . __( $errors->get_error_message($reason), 'text_domain' ) . '</p>';
            break;
        case 'sso_exception':
            $message .= '<p id="login_error" class="message">' . __( $errors->get_error_message($reason), 'text_domain' ) . '</p>';
            break;
    endswitch;

    return $message;
}

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


/**
 * Init Plugin
 *
 * @return void
 */
function ssokeycloak() : void
{
    if (class_exists('\Sindria\SsoKeycloak\Plugin')) {
        $plugin = new \Sindria\SsoKeycloak\Plugin();
    }
}
