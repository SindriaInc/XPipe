<?php
/**
 * @file helpers.php
 */

use Illuminate\Support\Facades\Auth;

/**
 * Global date format
 *
 * @param $field
 * @return false|string
 */
function format_date($field) {
    return date_format(date_create($field),"d/m/Y");
}


/**
 * Generate a unique code
 *
 * @param $limit
 * @return bool|string
 */
function unique_code($limit) {
    return substr(base_convert(sha1(uniqid(mt_rand())), 16, 36), 0, $limit);
}


/**
 * Get current app version
 *
 * @return string
 */
function app_version() {
    return env('APP_VERSION');
}


/**
 * Get current api base url
 *
 * @return string
 */
function api_base_url() {
    return env('API_BASE_URL');
}

/**
 * Get current api gateway url
 *
 * @return string
 */
function api_gateway_url() {
    return env('API_GATEWAY_URL');
}


/**
 * Get current cms base url
 *
 * @return string
 */
function cms_base_url() {
    return env('APP_URL') . '/_';
}

/**
 * Get current cms dashboard base url
 *
 * @return string
 */
function cms_dashboard_base_url() {
    return cms_base_url() . '/wp-admin';
}


/**
 * Get cms dashboard page route
 *
 * @param string $slug
 * @return string
 */
function cms_dashboard_page_route($slug) {
    return cms_dashboard_base_url() . '/admin.php?page='. $slug;
}

/**
 * Get cms locale code based on current locale
 *
 * @return string
 * @throws \Psr\Container\ContainerExceptionInterface
 * @throws \Psr\Container\NotFoundExceptionInterface
 */
function cms_current_locale_code() {

    $current_locale = session()->get('locale');

    switch ($current_locale) {
        case 'en':
            $code = 'en_US';
            break;
        case 'it':
            $code = 'it_IT';
            break;
        case 'es':
            $code = 'es_ES';
            break;
        default:
            $code = 'en_US';
    }

    return $code;
}

/**
 * Get current access token from user meta database
 *
 * @return mixed|null
 */
function access_token() {
    $access_token = NULL;
    $user_id = get_current_user_id();
    $hasAccessToken = metadata_exists( 'user', $user_id, 'access_token');

    if ($hasAccessToken) {
        $access_token = get_user_meta($user_id, 'access_token', true);
    }

    return $access_token;
}


/**
 * Get all tokens objects from $_SESSION
 *
 * @return array
 */
function current_tokens() {

    if (!session_id()) {
        session_start();
    }

    $tokens = [];

    if (! empty($_SESSION)) {
        foreach ($_SESSION as $entry) {
            if (preg_match('/(token_)(.*+)/', $entry)) {
                $tokens[] = $entry;
            }
        }
    }

    return $tokens;
}

/**
 * Get current token object as an array
 *
 * @return array
 */
function current_token() {

    $tokens = current_tokens();
    $currentData = [];

    if (! empty($tokens)) {
        $currentData = json_decode(end($tokens), true);
        return $currentData;
    }

    return $currentData;
}

/**
 * Get current bearer access token
 *
 * @return string
 */
function current_access_token() {

    $access_token = '';

    $data = current_token();

    if (! empty($data)) {
        $access_token = $data['access_token'];
    }

    return $access_token;
}

/**
 * Get current user data
 *
 * @return array
 */
function current_user() {

    if (!session_id()) {
        session_start();
    }

    $users = [];

    if (! empty($_SESSION)) {
        foreach ($_SESSION as $entry) {
            if (preg_match('/(user_)(.*+)/', $entry)) {
                $users[] = $entry;
            }
        }
    }

    $currentData = [];

    if (! empty($users)) {
        $currentData = json_decode(end($users), true);
//        if (isset($currentData['user'])) {
//            $currentData['user'] = json_decode($currentData['user']);
//        }

        return $currentData;
    }

    return $currentData;
}

/**
 * Determine if user is logged on dashboard side
 *
 * @return bool
 */
function logged() {
    if (session()->has('logged') && session()->get('logged')) {
        return true;
    }
    return false;
}


/**
 * Determine if user is logged on both sides (dashboard and framework)
 *
 * @return bool
 */
function user_is_logged() {
    if (session()->has('logged') && session()->get('logged') && Auth::check()) {
        return true;
    }
    return false;
}
