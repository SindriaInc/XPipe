<?php

/**
 * Copyright Sindria Inc.
 *
 * helpers.php
 *
 */


/**
 * Global date format
 *
 * @param $field
 * @return false|string
 */
function formatted_date($field) {
    return date_format(date_create($field),"d/m/Y");
}


/**
 * Formatted tournament boolean status
 *
 * @param $field
 * @return false|string
 */
function formatted_status($field) {
    if ($field) {
        return trans('dashboard.users.field.status.true');
    }
    return trans('dashboard.users.field.status.false');
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
 * Generate Frontend url
 *
 * @param string $uri
 * @return string
 */
function frontend_url($uri) {
    return env('FRONTEND_URL') . $uri;
}


/**
 * Generate Blog url
 *
 * @param string $uri
 * @return string
 */
function blog_url($uri) {
    if (session('locale') == 'it') {
        return env('BLOG_IT_URL') . $uri;
    }
    return env('BLOG_URL') . $uri;
}


/**
 * Generate Gallery url
 *
 * @param string $uri
 * @return string
 */
function gallery_url($uri) {
    if (session('locale') == 'it') {
        return env('GALLERY_URL') . $uri;
    }
    return env('GALLERY_URL') . $uri;
}

/**
 * Generate Pages url
 *
 * @param string $uri
 * @return string
 */
function pages_url($uri) {
    if (session('locale') == 'it') {
        return env('PAGES_URL') . $uri;
    }
    return env('PAGES_URL') . $uri;
}


/**
 * Generate Cms url
 *
 * @param $uri
 * @return string
 */
function cms_url($uri) {
    return env('CMS_URL') . $uri;
}


/**
 * Get current app version
 *
 * @return mixed
 */
function app_version() {
    return env('APP_VERSION');
}


/**
 * Get current logged user
 *
 * @return \Illuminate\Session\SessionManager|\Illuminate\Session\Store|mixed
 */
function current_user() {
    return session('logged_user');
}

/**
 * Determine if user is logged
 *
 * @return bool
 */
function user_is_logged() {
    if (session()->has('access_token')) {
        return true;
    }
    return false;
}

/**
 * Determine if current access token is expired (session idle)
 *
 * @return bool
 */
function token_is_expired() {
    if (now()->gte(current_expires_at())) {
        return true;
    }
    return false;
}

/**
 * Determine if current access token need refresh
 *
 * @return bool
 */
function token_need_refresh() {
    if (now()->gte(current_refresh_at())) {
        return true;
    }
    return false;
}


/**
 * Get current bearer access token
 *
 * @return \Illuminate\Session\SessionManager|\Illuminate\Session\Store|mixed
 */
function current_token() {
    return session('access_token');
}

/**
 * Get current expires at
 *
 * @return \Illuminate\Session\SessionManager|\Illuminate\Session\Store|mixed
 */
function current_expires_at() {
    return session('expires_at');
}

/**
 * Get current refresh at
 *
 * @return \Illuminate\Session\SessionManager|\Illuminate\Session\Store|mixed
 */
function current_refresh_at() {
    return session('refresh_at');
}


/**
 * Get current bearer refresh token
 *
 * @return \Illuminate\Session\SessionManager|\Illuminate\Session\Store|mixed
 */
function current_refresh_token() {
    return session('refresh_token');
}

/**
 * Get current refresh token count
 *
 * @return \Illuminate\Session\SessionManager|\Illuminate\Session\Store|mixed
 */
function current_refresh_count() {
    return session('refresh_count');
}
