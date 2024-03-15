<?php
/**
 * @package Dashboard Meta
 */
/*
Plugin Name: Dashboard Meta
Plugin URI: https://sindria.org
Description: Add meta tags on dashboard
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: dashboardmeta
*/

require_once 'functions.php';

add_filter('admin_head', 'sindria_dashboardmeta', 10);
add_action('admin_enqueue_scripts', 'sindria_dashboardmeta', 10);
