<?php
/**
 * @package Dashboard Plausible
 */
/*
Plugin Name: Dashboard Plausible
Plugin URI: https://sindria.org
Description: Add plausible scripts on dashboard
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: dashboardplausible
*/

require_once 'functions.php';

//add_filter('admin_head', 'sindria_dashboardplausible', 10);
//add_action('admin_enqueue_scripts', 'sindria_dashboardplausible', 10);
add_filter('login_footer', 'sindria_dashboardplausible');
add_action('admin_footer', 'sindria_dashboardplausible');
