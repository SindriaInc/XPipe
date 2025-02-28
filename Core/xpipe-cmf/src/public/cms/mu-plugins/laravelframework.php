<?php
/**
 * @package Laravel Framework
 */
/*
Plugin Name: Laravel Framework
Plugin URI: https://sindria.org
Description: This plugin bootstrap laravel framework inside wp core using custom front controller
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: laravelframework
*/

// Lib
require __DIR__ .'/../plugins/ssokeycloak/lib/oauth2-keycloak/vendor/autoload.php';

// CMS
require __DIR__ . '/../../cms.php';
