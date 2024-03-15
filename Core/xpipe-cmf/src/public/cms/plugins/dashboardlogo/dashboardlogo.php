<?php
/**
 * @package Dashboard Logo
 */
/*
Plugin Name: Dashboard Logo
Plugin URI: https://sindria.org
Description: Add brand logo on dashboard
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: dashboardlogo
*/

require_once 'functions.php';

// Dashboard Logo
add_action('admin_bar_menu', 'dashboardlogo', 999);

// Remove comments icon from admin navbar
add_action('wp_before_admin_bar_render', 'dashboardlogo_comments');
