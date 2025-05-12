<?php
/**
 * @package Dashboard Notifications
 */
/*
Plugin Name: Dashboard Notifications
Plugin URI: https://sindria.org
Description: Add notifications bell icon on admin bar and page on dashboard instead of updates.
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: dashboardnotifications
*/

// Core
require_once 'functions.php';
require_once 'routes.php';
require_once 'Plugin.php';

// MVVM
require_once 'Controller.php';
require_once 'View.php';
require_once 'Helper.php';

// View Models
require_once 'viewmodel/IndexViewModel.php';

// Init plugin
add_action('init', 'dashboardnotifications', 9, 0);

// Remove update core dashboard submenu
add_action('admin_menu', 'dashboardnotifications_remove_update_core', 10);

// Add notifications bell on admin bar
add_action('admin_bar_menu', 'dashboardnotifications_bell', 20);
