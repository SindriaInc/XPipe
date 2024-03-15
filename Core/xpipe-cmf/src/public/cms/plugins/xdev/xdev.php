<?php
/**
 * @package XDev
 */
/*
Plugin Name: XDev
Plugin URI: https://sindria.org
Description: Add xdev integration.
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: xdev
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
require_once 'viewmodel/CliViewModel.php';
require_once 'viewmodel/GuiViewModel.php';

// Init plugin
add_action('init', 'xdev', 9, 0);

// Add xdev on admin bar
add_action('admin_bar_menu', 'wp_admin_bar_xdev_item', 30);
add_action('admin_bar_menu', 'wp_admin_bar_xdev_menu', 30);
// Remove xdev from admin bar if user not have capabilities
add_action('admin_bar_menu', 'xdev_remove_menu', 999);
