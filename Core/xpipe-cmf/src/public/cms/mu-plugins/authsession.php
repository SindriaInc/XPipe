<?php
/**
 * @package Auth Session
 */
/*
Plugin Name: Auth Session
Plugin URI: https://sindria.org
Description: This plugin manage auth session between locales and laravel framework
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: authsession
*/

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Http;


function authsession_auto_login() {

    global $pagenow;

    $action = NULL;

    if (isset($_GET['action'])) {
        $action = $_GET['action'];
    }

    if ($pagenow == 'wp-login.php') {

        if ($action != "logout") {

            $username = "operator";

            $user = get_user_by('login', $username);

            if ($user) {
                clean_user_cache($user->ID);
                wp_clear_auth_cookie();

                wp_set_current_user( $user->ID );
                wp_set_auth_cookie( $user->ID , true, false);

                update_user_caches($user);

                if (is_user_logged_in()) {
                    $redirect_to = user_admin_url();
                    //$redirect_to = wp_redirect(  '/wp-admin/edit.php');
                    wp_safe_redirect($redirect_to);
                    exit;
                }
            }
        }
    }
}


function authsession_redirect_after_login() {
    //wp_redirect('http://localhost/' . session()->get('locale') . '/dashboard');
    //exit();
    return 'http://localhost' . session()->get('locale') . '/dashboard';
}

function authsession_redirect_after_logout() {
    wp_redirect('http://localhost' . session()->get('locale') . '/login');
    exit();
}

function authsession_redefine_locale() {
    return cms_current_locale_code();
}

function authsession_save_user( $user_login, $user ) {

    if (!session_id()) {
        session_start();
    }

    $sessionKey = 'user_' . md5($user->user_login);
    $_SESSION[$sessionKey] = json_encode($user);

    $redirect_to = env('APP_URL'). '/auth/login?u=' . $user_login;
    //wp_safe_redirect($redirect_to);
    echo("<script>location.href = '".$redirect_to."';</script>");
    exit;
}

function authsession_destroy_session() {

    $redirect_to = env('APP_URL'). '/auth/logout';
    wp_safe_redirect($redirect_to);
    echo("<script>location.href = '".$redirect_to."';</script>");
    exit;
}


// Auto Login
//add_action('init','authsession_auto_login');

// Logout redirect
//add_action('wp_logout','authsession_redirect_after_logout');

// Login redirect
//add_filter('login_redirect', 'authsession_redirect_after_login');

// Set locale
add_filter('locale','authsession_redefine_locale',10);

// Save user into session
add_action('wp_login', 'authsession_save_user', 10, 2);

// Logout session
add_action('wp_logout', 'authsession_destroy_session', 10);


add_action('init','authsession_refresh_token');

function authsession_refresh_token() {

    $user_id = get_current_user_id();

    $hasAccessToken = metadata_exists( 'user', $user_id, 'access_token');
    $hasRefreshToken = metadata_exists( 'user', $user_id, 'refresh_token');
    $hasExpires = metadata_exists( 'user', $user_id, 'expires');
    $hasExpiresAt = metadata_exists( 'user', $user_id, 'expires_at');
    $hasRefreshAt = metadata_exists( 'user', $user_id, 'refresh_at');

    if ($hasAccessToken && $hasRefreshToken && $hasExpiresAt) {

        $currentAccessToken = get_user_meta($user_id, 'access_token', true);
        $currentRefreshToken = get_user_meta($user_id, 'refresh_token', true);
        $currentExpiresAt = get_user_meta($user_id, 'expires_at', true);
        $currentRefreshAt = get_user_meta($user_id, 'refresh_at', true);

        // TODO: check expire

        //dd($currentAccessToken, $currentRefreshToken, $currentExpiresAt, $currentRefreshAt, gmdate("Y-m-d\TH:i:s\Z", time()));

        $form = [];
        $form['refresh_token'] = $currentRefreshToken;

        $url = api_gateway_url() . '/api/auth/refresh';
        $response = Http::put($url, $form);
        $result = $response->body();
        $resource = json_decode($result);

        if ($resource->success) {

            $access_token = $resource->data->access_token;
            $expires_in = $resource->data->expires_in;
            $refresh_token = $resource->data->refresh_token;


            update_user_meta($user_id, 'access_token', $access_token);
            update_user_meta($user_id, 'refresh_token', $refresh_token);
            // TODO: update expire
            //update_user_meta($user_id, 'expires_at', $refresh_expires_in);


            // Refresh Counter
            $hasRefreshCounter = metadata_exists( 'user', $user_id, 'refresh_counter');

            if ($hasRefreshCounter) {
                $currentRefreshCount = get_user_meta($user_id, 'refresh_counter', true);
                $updated_refresh_count = $currentRefreshCount + 1;
                update_user_meta($user_id, 'refresh_counter', $updated_refresh_count);
            }

        }


    }


}
