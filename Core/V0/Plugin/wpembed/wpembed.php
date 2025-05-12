<?php
/**
 * @package WP Embed
 */
/*
Plugin Name: WP Embed
Plugin URI: https://sindria.org
Description: This plugin for allow embed WordPress with iframe inside another app
Version: 0.1.0
Author: Sindria Inc.
Author URI: https://sindria.org
License: GPLv2 or later
Text Domain: wpembed
*/

require_once('functions.php');


//remove the restriction
remove_action('login_init', 'send_frame_options_header');
remove_action('admin_init', 'send_frame_options_header');

//for added security
add_action('login_init', 'sindria_access_control_allow_origin');
add_action('admin_init', 'sindria_access_control_allow_origin');
