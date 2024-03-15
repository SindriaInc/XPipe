<?php
/**
 * @package Dashboard Theme
 */
/*
Plugin Name: Dashboard Theme
Plugin URI: https://sindria.org
Description: Customize Dashboard Theme
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: dashboardtheme
*/

// Core
require_once 'functions.php';
require_once 'Plugin.php';

// Init plugin
add_action('init', 'dashboardtheme', 9, 0);
