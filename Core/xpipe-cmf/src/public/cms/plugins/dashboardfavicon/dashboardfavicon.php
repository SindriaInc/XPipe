<?php
/**
 * @package Dashboard Favicon
 */
/*
Plugin Name: Dashboard Favicon
Plugin URI: https://sindria.org
Description: Add favicon on login page and dashboard
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: dashboardfavicon
*/

require_once('functions.php');

add_action('login_head', 'dashboardfavicon');
add_action('admin_head', 'dashboardfavicon');
