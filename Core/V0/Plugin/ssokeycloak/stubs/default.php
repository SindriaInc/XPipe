<?php
/**
 * @package SSO Keycloak
 */
/*
Plugin Name: SSO Keycloak
Plugin URI: https://sindria.org
Description: This plugin add sso integration with keycloak
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: ssokeycloak
*/

// Lib Standalone
//require __DIR__ .'/lib/oauth2-keycloak/vendor/autoload.php';

// Core
require_once 'functions.php';
require_once 'Plugin.php';

// MVC
require_once 'Controller.php';
require_once 'Service.php';
require_once 'Helper.php';

// Init plugin
add_action('init', 'ssokeycloak', 9, 0);
// Other hooks
add_filter('login_message', 'ssokeycloak_login_error_messages');