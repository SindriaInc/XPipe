<?php
/**
 * @package Login Screen
 */
/*
Plugin Name: Login Screen
Plugin URI: https://sindria.org
Description: Customize wp login screen
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: loginscreen
*/

require_once('functions.php');

// Load custom css and js files
add_action('login_enqueue_scripts', 'loginscreen_login_stylesheet');

// Add custom background image
add_filter('login_footer', 'loginscreen_background');

// Remove language selector from login page
add_filter( 'login_display_language_dropdown', '__return_false' );

// Set remember me checkbox always to checked
add_filter('login_footer', 'loginscreen_always_checked_rememberme');

// Remove WordPress string from title
add_filter('login_title', 'loginscreen_login_title' );

// Login screen footer
add_filter('login_footer', 'loginscreen_footer');
