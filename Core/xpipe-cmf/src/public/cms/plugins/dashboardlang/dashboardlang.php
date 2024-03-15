<?php
/**
 * @package Dashboard Lang
 */
/*
Plugin Name: Dashboard Lang
Plugin URI: https://sindria.org
Description: Add language selection on dashboard
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: dashboardlang
*/

require_once 'functions.php';

add_action('admin_bar_menu', 'sindria_dashboardlang', 10);
